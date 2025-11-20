package com.suleymansecgin.admin_panel.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        logger.info("Incoming Request: {} {}", request.getMethod(), request.getRequestURI());
        logger.info("Remote Addr: {}", request.getRemoteAddr());

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            // Don't log full sensitive tokens
            if ("Authorization".equalsIgnoreCase(headerName) && headerValue != null && headerValue.length() > 20) {
                headerValue = headerValue.substring(0, 20) + "...";
            }
            logger.info("Header '{}': {}", headerName, headerValue);
        }

        filterChain.doFilter(request, response);
    }
}
