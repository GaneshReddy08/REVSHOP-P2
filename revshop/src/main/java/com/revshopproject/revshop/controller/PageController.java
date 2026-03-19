package com.revshopproject.revshop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.revshopproject.revshop.dto.ProductResponseDTO;
import com.revshopproject.revshop.entity.Product;
import com.revshopproject.revshop.repository.SecurityQuestionRepository;
import com.revshopproject.revshop.service.ProductService;

@Controller
public class PageController {

    @Autowired
    private ProductService productService;

    @Autowired
    private SecurityQuestionRepository securityQuestionRepository;

    @GetMapping("/")
    public String showIndexPage(Model model) {
        // 1. Fetch the raw Entities from the service
        List<Product> products = productService.getAllProducts();

        // 2. Map List<Product> to List<ProductResponseDTO>
        List<ProductResponseDTO> productDTOs = products.stream()
                .map(ProductResponseDTO::fromEntity)
                .toList(); 

        // 3. Add the DTO list to the model
        model.addAttribute("products", productDTOs);
        return "index";
    }
    @GetMapping("/login")
    public String renderLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String renderRegisterPage(Model model) {
        model.addAttribute("securityQuestions", securityQuestionRepository.findAll());
        return "register";
    }

    @GetMapping("/forgot-password")
    public String renderForgotPasswordPage(Model model) {
        model.addAttribute("securityQuestions", securityQuestionRepository.findAll());
        return "forgot-password";
    }

    @GetMapping("/seller/dashboard")
    public String renderSellerDashboard() {
        return "seller/dashboard";
    }

    @GetMapping("/cart")
    public String renderCartPage() {
        return "cart";
    }

    @GetMapping("/orders")
    public String renderOrdersPage() {
        return "orders";
    }

    @GetMapping("/notifications")
    public String renderNotificationsPage() {
        return "notifications";
    }

    @GetMapping("/favorites")
    public String renderFavoritesPage() {
        return "favorites";
    }

    @GetMapping("/product/{id}")
    public String renderProductDetailPage() {
        return "product-detail";
    }
}