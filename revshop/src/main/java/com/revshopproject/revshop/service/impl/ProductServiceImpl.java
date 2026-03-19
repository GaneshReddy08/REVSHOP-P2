package com.revshopproject.revshop.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revshopproject.revshop.dto.ProductRequestDTO;
import com.revshopproject.revshop.entity.Category;
import com.revshopproject.revshop.entity.Product;
import com.revshopproject.revshop.entity.ProductImage;
import com.revshopproject.revshop.entity.User;
import com.revshopproject.revshop.repository.CartItemRepository;
import com.revshopproject.revshop.repository.CategoryRepository;
import com.revshopproject.revshop.repository.FavoriteRepository;
import com.revshopproject.revshop.repository.OrderItemRepository;
import com.revshopproject.revshop.repository.ProductImageRepository;
import com.revshopproject.revshop.repository.ProductRepository;
import com.revshopproject.revshop.repository.ReviewRepository;
import com.revshopproject.revshop.service.NotificationService;
import com.revshopproject.revshop.service.ProductService;
import com.revshopproject.revshop.service.UserService;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LogManager.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    private final CartItemRepository cartItemRepository;
    private final FavoriteRepository favoriteRepository;
    private final OrderItemRepository orderItemRepository;
    private final ReviewRepository reviewRepository;

    public ProductServiceImpl(ProductRepository productRepository,
            CategoryRepository categoryRepository,
            ProductImageRepository productImageRepository,
            UserService userService,
            NotificationService notificationService,
            CartItemRepository cartItemRepository,
            FavoriteRepository favoriteRepository,
            OrderItemRepository orderItemRepository,
            ReviewRepository reviewRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productImageRepository = productImageRepository;
        this.userService = userService;
        this.notificationService = notificationService;
        this.cartItemRepository = cartItemRepository;
        this.favoriteRepository = favoriteRepository;
        this.orderItemRepository = orderItemRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    @Transactional
    public Product saveProduct(ProductRequestDTO dto) {
        User seller = userService.getCurrentUser();

        if (!"SELLER".equalsIgnoreCase(seller.getRole())) {
            throw new RuntimeException("Access Denied: Only users with the 'SELLER' role can add products.");
        }

        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setMrp(dto.getMrp());
        product.setStock(dto.getStock());
        product.setInventoryThreshold(dto.getInventoryThreshold());
        product.setSeller(seller);

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found."));
            product.setCategory(category);
        }

        validateProductRules(product, seller);

        Product saved = productRepository.save(product);
        log.info("Product saved: {} (ID: {})", saved.getName(), saved.getProductId());

        checkAndNotifyLowStock(saved, seller);

        return saved;
    }

    // ── Shared validation helper ──────────────────────────────────────────────
    private void validateProductRules(Product product, User seller) {
        if (product.getPrice() != null && product.getMrp() != null
                && product.getPrice().compareTo(product.getMrp()) > 0) {
            throw new RuntimeException(
                "Validation Error: Selling price cannot be higher than the MRP.");
        }

        if (product.getStock() != null && product.getStock() < 0) {
            throw new RuntimeException(
                "Validation Error: Stock cannot be negative.");
        }

        if (product.getStock() != null && product.getInventoryThreshold() != null
                && product.getStock() <= product.getInventoryThreshold()) {
            throw new RuntimeException(
                "Validation Error: Stock (" + product.getStock() +
                ") must be greater than the inventory threshold (" +
                product.getInventoryThreshold() + ").");
        }
    }

    // ── Low stock notification helper ─────────────────────────────────────────
    private void checkAndNotifyLowStock(Product product, User seller) {
        int threshold = (product.getInventoryThreshold() != null)
                ? product.getInventoryThreshold() : 5;
        if (product.getStock() != null && product.getStock() <= threshold) {
            notificationService.sendNotification(seller,
                "[LOW STOCK] Alert: " + product.getName() +
                " has only " + product.getStock() + " units left.");
        }
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategory_CategoryId(categoryId);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        User currentUser = userService.getCurrentUser();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getSeller().getUserId().equals(currentUser.getUserId())) {
            throw new RuntimeException("Unauthorized: You can only delete your own products.");
        }

        // 1. Remove from all carts
        List<com.revshopproject.revshop.entity.CartItem> cartItems =
                cartItemRepository.findByProduct_ProductId(id);
        if (!cartItems.isEmpty()) {
            cartItemRepository.deleteAll(cartItems);
            cartItemRepository.flush();
        }

        // 2. Remove all favorites
        List<com.revshopproject.revshop.entity.Favorite> favorites =
                favoriteRepository.findByProduct_ProductId(id);
        if (!favorites.isEmpty()) {
            favoriteRepository.deleteAll(favorites);
            favoriteRepository.flush();
        }

        // 3. Nullify product ref in order items (preserves order history)
        List<com.revshopproject.revshop.entity.OrderItem> orderItems =
                orderItemRepository.findByProduct_ProductId(id);
        if (!orderItems.isEmpty()) {
            orderItems.forEach(oi -> oi.setProduct(null));
            orderItemRepository.saveAll(orderItems);
            orderItemRepository.flush();   // ← forces UPDATE before DELETE
        }

        // 4. Delete reviews
        List<com.revshopproject.revshop.entity.Review> reviews =
                reviewRepository.findByProduct_ProductId(id);
        if (!reviews.isEmpty()) {
            reviewRepository.deleteAll(reviews);
            reviewRepository.flush();
        }

        // 5. Delete product (images cascade)
        productRepository.delete(product);
        productRepository.flush();
        log.info("Product deleted: '{}' (ID: {}) by userId={}", product.getName(), id, currentUser.getUserId());
    }
    @Override
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
    }

    @Override
    public List<Product> getProductsBySellerId(Long sellerId) {
        return productRepository.findBySeller_UserId(sellerId);
    }

    @Override
    @Transactional
    public void addImageToProduct(Long productId, String imageUrl) {
        User currentUser = userService.getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getSeller().getUserId().equals(currentUser.getUserId())) {
            throw new RuntimeException("Unauthorized: You can only add images to your own products.");
        }

        ProductImage productImage = new ProductImage();
        productImage.setProduct(product);
        productImage.setImageUrl(imageUrl);

        productImageRepository.save(productImage);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, ProductRequestDTO dto) {
        User seller = userService.getCurrentUser();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getSeller().getUserId().equals(seller.getUserId())) {
            throw new RuntimeException("Unauthorized: You can only update your own products.");
        }

        if (dto.getName() != null) product.setName(dto.getName());
        if (dto.getDescription() != null) product.setDescription(dto.getDescription());
        if (dto.getPrice() != null) product.setPrice(dto.getPrice());
        if (dto.getMrp() != null) product.setMrp(dto.getMrp());
        if (dto.getStock() != null) product.setStock(dto.getStock());
        if (dto.getInventoryThreshold() != null) product.setInventoryThreshold(dto.getInventoryThreshold());

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found."));
            product.setCategory(category);
        }

        validateProductRules(product, seller);

        Product saved = productRepository.save(product);
        log.info("Product updated: {} (ID: {})", saved.getName(), saved.getProductId());

        checkAndNotifyLowStock(saved, seller);

        return saved;
    }
}
