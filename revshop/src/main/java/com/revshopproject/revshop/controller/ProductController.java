package com.revshopproject.revshop.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.revshopproject.revshop.dto.ProductResponseDTO;
import com.revshopproject.revshop.entity.Product;
import com.revshopproject.revshop.entity.ProductImage;
import com.revshopproject.revshop.repository.ProductImageRepository;
import com.revshopproject.revshop.repository.ProductRepository;
import com.revshopproject.revshop.service.ProductService;
import com.revshopproject.revshop.utils.FileUploadUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger logger = LogManager.getLogger(ProductController.class);

    private final ProductService productService;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    public ProductController(ProductService productService, ProductRepository productRepository,
            ProductImageRepository productImageRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Sanitizes a filename: trims, replaces spaces with underscores,
     * strips any character that isn't alphanumeric, dot, dash, or underscore.
     * Falls back to a timestamp-based name if the result is empty.
     */
    private String sanitizeFilename(String original) {
        if (original == null || original.trim().isEmpty()) {
            return "image_" + System.currentTimeMillis();
        }
        String clean = StringUtils.cleanPath(original)
                .trim()
                .replaceAll("\\s+", "_")
                .replaceAll("[^a-zA-Z0-9._-]", "");
        return clean.isEmpty() ? "image_" + System.currentTimeMillis() : clean;
    }

    // ── Search ────────────────────────────────────────────────────────────────

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDTO>> search(@RequestParam("q") String keyword) {
        List<ProductResponseDTO> dtos = productService.searchProducts(keyword)
                .stream()
                .map(ProductResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ── Add product via URL ───────────────────────────────────────────────────

    @PostMapping("/{productId}/add-image-url")
    public ResponseEntity<String> addImageByUrl(
            @PathVariable Long productId,
            @RequestBody java.util.Map<String, String> body) {
        String imageUrl = body.get("imageUrl");
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("imageUrl is required");
        }
        productService.addImageToProduct(productId, imageUrl.trim());
        return ResponseEntity.ok("Image URL saved: " + imageUrl.trim());
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<ProductResponseDTO> addProduct(
            @RequestBody com.revshopproject.revshop.dto.ProductRequestDTO productRequest) {
        logger.info("Received request to add a new product: {}", productRequest.getName());
        Product savedProduct = productService.saveProduct(productRequest);
        return ResponseEntity.ok(ProductResponseDTO.fromEntity(savedProduct));
    }

    @GetMapping
    public List<ProductResponseDTO> getAll() {
        return productService.getAllProducts()
                .stream()
                .map(ProductResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/category/{categoryId}")
    public List<ProductResponseDTO> getByCategory(@PathVariable Long categoryId) {
        return productService.getProductsByCategory(categoryId)
                .stream()
                .map(ProductResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ProductResponseDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/images")
    public ResponseEntity<List<String>> getProductImages(@PathVariable Long id) {
        List<String> imageUrls = productImageRepository.findByProduct_ProductId(id)
                .stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());
        return ResponseEntity.ok(imageUrls);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        logger.info("Received request to delete product with ID: {}", id);
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }

    @org.springframework.web.bind.annotation.PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @RequestBody com.revshopproject.revshop.dto.ProductRequestDTO productRequest) {
        Product updatedProduct = productService.updateProduct(id, productRequest);
        return ResponseEntity.ok(ProductResponseDTO.fromEntity(updatedProduct));
    }

    // ── Top picks / rated ─────────────────────────────────────────────────────

    @GetMapping("/top-rated")
    public ResponseEntity<List<ProductResponseDTO>> getTopRatedProducts() {
        List<ProductResponseDTO> dtos = productRepository.findTopRatedProducts()
                .stream()
                .limit(10)
                .map(ProductResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/homepage/top-picks")
    public ResponseEntity<List<ProductResponseDTO>> getTopPicks() {
        List<ProductResponseDTO> dtos = productRepository.findTopRatedProducts()
                .stream()
                .limit(5)
                .map(ProductResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ── Image upload ──────────────────────────────────────────────────────────

    @PostMapping("/{productId}/upload-image")
    public ResponseEntity<String> uploadProductImage(
            @PathVariable Long productId,
            @RequestParam("image") MultipartFile file) {
        try {
            String fileName   = java.util.UUID.randomUUID() + "_" + sanitizeFilename(file.getOriginalFilename());
            String uploadDir  = "product-images/" + productId;

            FileUploadUtil.saveFile(uploadDir, fileName, file);

            String databaseUrl = "/uploads/" + uploadDir + "/" + fileName;
            productService.addImageToProduct(productId, databaseUrl);

            return ResponseEntity.ok("Image successfully uploaded: " + databaseUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Could not upload file: " + e.getMessage());
        }
    }

    @PostMapping("/{productId}/upload-images")
    public ResponseEntity<String> uploadProductImages(
            @PathVariable Long productId,
            @RequestParam("images") List<MultipartFile> files) {
        try {
            StringBuilder urls    = new StringBuilder();
            String        uploadDir = "product-images/" + productId;

            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;

                String fileName    = java.util.UUID.randomUUID() + "_" + sanitizeFilename(file.getOriginalFilename());
                FileUploadUtil.saveFile(uploadDir, fileName, file);

                String databaseUrl = "/uploads/" + uploadDir + "/" + fileName;
                productService.addImageToProduct(productId, databaseUrl);

                if (urls.length() > 0) urls.append(", ");
                urls.append(databaseUrl);
            }

            return ResponseEntity.ok("Images successfully uploaded: " + urls.toString());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Could not upload files: " + e.getMessage());
        }
    }
}