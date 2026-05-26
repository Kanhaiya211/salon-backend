package com.salon.salon_backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.salon.salon_backend.service.CustomUserDetailsService;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private CustomUserDetailsService userDetailsService;
    

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // SKIP AUTH ENDPOINTS
    	// SKIP AUTH ENDPOINTS
    	String uri = request.getRequestURI();

    	System.out.println("REQUEST URI: " + uri);

    	if (
    	        uri.contains("/auth/login") ||
    	        uri.contains("/auth/signup")
    	) {

    	    filterChain.doFilter(request, response);

    	    return;
    	}

        String authHeader = request.getHeader("Authorization");

        String token = null;
        String email = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            token = authHeader.substring(7);

            if (JwtProvider.validateToken(token)) {
                email = JwtProvider.getEmailFromToken(token);
            }
        }

        if (email != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

        	UserDetails userDetails =
        	        userDetailsService.loadUserByUsername(email);

        	System.out.println(userDetails.getAuthorities());

        	UsernamePasswordAuthenticationToken authentication =
        	        new UsernamePasswordAuthenticationToken(
        	                userDetails,
        	                null,
        	                userDetails.getAuthorities()
        	        );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}