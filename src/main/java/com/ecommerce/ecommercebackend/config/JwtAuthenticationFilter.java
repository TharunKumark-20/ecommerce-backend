package com.ecommerce.ecommercebackend.config;

import com.ecommerce.ecommercebackend.service.CustomUserDetailsService;
import com.ecommerce.ecommercebackend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger =
            LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;

    private final CustomUserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {


        String path =
                request.getServletPath();


        // Skip JWT validation for authentication endpoints
        if (path.startsWith("/auth")) {

            filterChain.doFilter(request, response);

            return;
        }


        final String authHeader =
                request.getHeader("Authorization");


        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            logger.warn(
                    "No valid Authorization header for request: {}",
                    path
            );

            filterChain.doFilter(request, response);

            return;
        }


        final String jwt =
                authHeader.substring(7);


        final String email;


        try {

            email =
                    jwtService.extractUsername(jwt);

            logger.info(
                    "JWT username extracted successfully: {}",
                    email
            );

        } catch (Exception e) {

            logger.error(
                    "JWT validation failed while extracting username for request: {}",
                    path,
                    e
            );

            filterChain.doFilter(request, response);

            return;
        }


        if (email != null &&
                SecurityContextHolder
                        .getContext()
                        .getAuthentication() == null) {


            try {

                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(email);


                logger.info(
                        "User loaded successfully: {}",
                        userDetails.getUsername()
                );


                boolean tokenValid =
                        jwtService.isTokenValid(
                                jwt,
                                userDetails
                        );


                logger.info(
                        "JWT token valid: {}",
                        tokenValid
                );


                if (tokenValid) {


                    UsernamePasswordAuthenticationToken
                            authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );


                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );


                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authToken);


                    logger.info(
                            "Authentication successfully set for user: {} with authorities: {}",
                            userDetails.getUsername(),
                            userDetails.getAuthorities()
                    );

                } else {

                    logger.warn(
                            "JWT token is invalid for user: {}",
                            email
                    );
                }


            } catch (Exception e) {

                logger.error(
                        "Error while authenticating user: {}",
                        email,
                        e
                );
            }
        }


        filterChain.doFilter(request, response);
    }
}