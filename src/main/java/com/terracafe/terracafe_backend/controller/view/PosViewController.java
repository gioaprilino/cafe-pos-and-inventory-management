package com.terracafe.terracafe_backend.controller.view;

import com.terracafe.terracafe_backend.model.Category;
import com.terracafe.terracafe_backend.model.Product;
import com.terracafe.terracafe_backend.model.User;
import com.terracafe.terracafe_backend.service.CategoryService;
import com.terracafe.terracafe_backend.service.ProductService;
import com.terracafe.terracafe_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/pos")
public class PosViewController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String pos(Model model) {
        // Fetch all active products
        List<Product> products = productService.getAllProducts();
        // Fetch all categories
        List<Category> categories = categoryService.getAllCategories();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            Optional<User> userOpt = userService.getUserByUsername(auth.getName());
            userOpt.ifPresent(user -> model.addAttribute("cashierId", user.getId()));
        }

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("title", "Point of Sales");
        return "pos/index";
    }
}
