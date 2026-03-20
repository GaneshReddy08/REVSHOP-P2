package com.revshopproject.revshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.revshopproject.revshop.security.CustomLoginSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomLoginSuccessHandler successHandler;

    public SecurityConfig(CustomLoginSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
            "/uploads/**",
            "/product-images/**",
            "/css/**",
            "/js/**",
            "/images/**",
            "/static/**",
            "/webjars/**",
            "/main.js",
            "/**.js",
            "/**.css"
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // 1. Public Pages
                .requestMatchers(
                    "/", "/login", "/register",
                    "/forgot-password", "/forgot-password/**",
                    "/product/**"
                ).permitAll()

                .requestMatchers("/cart", "/orders").authenticated()

                // 2. Public API Endpoints
                .requestMatchers(
                    "/api/users/register",
                    "/api/users/login",
                    "/api/users/forgot-password",
                    "/api/users/security-question"
                ).permitAll()

                .requestMatchers(HttpMethod.GET, "/api/products/**", "/api/categories/**").permitAll()

                // 3. BUYER Permissions
                .requestMatchers("/api/cart/**").hasRole("BUYER")
                .requestMatchers(HttpMethod.POST, "/api/orders/place").hasRole("BUYER")
                .requestMatchers(HttpMethod.GET, "/api/orders/user/**").hasRole("BUYER")
                .requestMatchers(HttpMethod.GET, "/api/orders/**").hasAnyRole("BUYER", "SELLER")
                .requestMatchers(HttpMethod.POST, "/api/reviews/**").hasRole("BUYER")

                // 4. SELLER Permissions
                .requestMatchers("/api/seller/**").hasRole("SELLER")
                .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("SELLER")
                .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("SELLER")
                .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("SELLER")
                .requestMatchers(HttpMethod.POST, "/api/categories/**").hasRole("SELLER")

                // 5. Catch-all
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/perform_login")
                .successHandler(successHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }
}
//package com.revshopproject.revshop.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import com.revshopproject.revshop.security.CustomLoginSuccessHandler;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    private final CustomLoginSuccessHandler successHandler;
//
//    public SecurityConfig(CustomLoginSuccessHandler successHandler) {
//        this.successHandler = successHandler;
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    // Completely bypass security for uploaded images
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring().requestMatchers("/uploads/**", "/product-images/**");
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf(AbstractHttpConfigurer::disable)
//            .exceptionHandling(exceptions -> exceptions
//            	    .authenticationEntryPoint((request, response, authException) -> {
//            	        if (request.getRequestURI().startsWith("/api/")) {
//            	            response.sendError(401, "Unauthorized");
//            	        } else {
//            	            String uri = request.getRequestURI();
//            	            // Allow forgot-password page through regardless of query params
//            	            if (uri.startsWith("/forgot-password")) {
//            	                response.sendRedirect(request.getRequestURI() + 
//            	                    (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
//            	            } else {
//            	                response.sendRedirect("/login");
//            	            }
//            	        }
//            	    })
//            	)
//            .authorizeHttpRequests(auth -> auth
//                // 1. Static Resources & Public Pages
//            	.requestMatchers("/", "/login", "/register", "/forgot-password", "/forgot-password/**", "/product/**", "/css/**", "/js/**", "/images/**", "/uploads/**", "/static/**", "/webjars/**","/main.js").permitAll()
//            	.requestMatchers(HttpMethod.GET, "/**.js", "/**.css").permitAll()
//                .requestMatchers("/cart", "/orders").authenticated()
//                
//                // 2. Public API Endpoints
//                .requestMatchers("/api/users/register", "/api/users/login", "/api/users/forgot-password", "/api/users/security-question").permitAll()                
//                .requestMatchers(HttpMethod.GET, "/api/products/**", "/api/categories/**").permitAll()
//
//                // 3. BUYER Permissions
//                .requestMatchers("/api/cart/**").hasRole("BUYER")
//                .requestMatchers(HttpMethod.POST, "/api/orders/place").hasRole("BUYER")
//                .requestMatchers(HttpMethod.GET, "/api/orders/user/**").hasRole("BUYER")
//                .requestMatchers(HttpMethod.POST, "/api/reviews/**").hasRole("BUYER")
//
//                // 4. SELLER Permissions
//                .requestMatchers("/api/seller/**").hasRole("SELLER")
//                .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("SELLER")
//                .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("SELLER")
//                .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("SELLER")
//                .requestMatchers(HttpMethod.POST, "/api/categories/**").hasRole("SELLER")
//
//                // 5. Catch-all for everything else
//                .anyRequest().authenticated()
//            )
//            // Enable standard Form Login for the browser
//            .formLogin(form -> form
//                .loginPage("/login")
//                .loginProcessingUrl("/perform_login")
//                .successHandler(successHandler)
//                .permitAll()
//            )
//            .logout(logout -> logout
//                .logoutSuccessUrl("/")
//                .permitAll()
//            );
//
//        return http.build();
//    }
//}