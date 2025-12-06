package com.terracafe.terracafe_backend.filter;

import com.terracafe.terracafe_backend.service.CustomUserDetailsService;
import com.terracafe.terracafe_backend.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Extract Authorization header
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Check if Authorization header exists and starts with "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // Remove "Bearer " prefix
            try {
                username = jwtUtil.extractUsername(jwt);
                logger.info("JWT Token extracted successfully. Username: " + username);
            } catch (Exception e) {
                logger.error("JWT Token extraction failed: " + e.getMessage(), e);
            }
        } else {
            logger.debug("No Bearer token found in Authorization header");
        }

        // Validate token and set authentication
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                logger.info("UserDetails loaded. Username: " + userDetails.getUsername() + ", Authorities: " + userDetails.getAuthorities());

                // Validate token dengan userDetails
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set authentication ke SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    logger.info("Authentication set successfully for user: " + username);
                } else {
                    logger.warn("JWT Token validation failed for user: " + username);
                }
            } catch (Exception e) {
                logger.error("Error during authentication: " + e.getMessage(), e);
            }
        }

        filterChain.doFilter(request, response);
    }
}
