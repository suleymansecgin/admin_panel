package com.suleymansecgin.admin_panel.handler;

import com.suleymansecgin.admin_panel.exception.BaseException;
import com.suleymansecgin.admin_panel.exception.ErrorMessage;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<com.suleymansecgin.admin_panel.handler.Exception<ErrorMessage>> handleBaseException(
            BaseException ex, jakarta.servlet.http.HttpServletRequest request) {
        ErrorMessage errorMessage = extractErrorMessage(ex);
        com.suleymansecgin.admin_panel.handler.Exception<ErrorMessage> exceptionResponse = createExceptionResponse(request, errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<com.suleymansecgin.admin_panel.handler.Exception<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex, jakarta.servlet.http.HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        com.suleymansecgin.admin_panel.handler.Exception<Map<String, String>> exceptionResponse = createExceptionResponse(request, errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<com.suleymansecgin.admin_panel.handler.Exception<Map<String, String>>> handleConstraintViolationException(
            ConstraintViolationException ex, jakarta.servlet.http.HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        });
        
        com.suleymansecgin.admin_panel.handler.Exception<Map<String, String>> exceptionResponse = createExceptionResponse(request, errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }
    
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handleNoHandlerFoundException(
            NoHandlerFoundException ex, jakarta.servlet.http.HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        
        // API route'ları için 404 döndür
        if (requestPath != null && requestPath.startsWith("/api/")) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setMessageType(com.suleymansecgin.admin_panel.exception.MessageType.GENERAL_EXCEPTION);
            errorMessage.setOfStatic("API endpoint bulunamadı: " + requestPath);
            
            com.suleymansecgin.admin_panel.handler.Exception<ErrorMessage> exceptionResponse = createExceptionResponse(request, errorMessage);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
        }
        
        // Static resource istekleri için index.html'e yönlendir (SPA için)
        // Bu durumda frontend routing handle edecek
        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", "text/html")
                .body(getIndexHtmlContent());
    }
    
    private String getIndexHtmlContent() {
        // index.html içeriğini döndür
        // Eğer classpath'te yoksa basit bir HTML döndür
        try {
            java.io.InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("static/index.html");
            if (inputStream != null) {
                return new String(inputStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            }
        } catch (java.io.IOException e) {
            // Fallback HTML
        }
        
        // Fallback: Basit HTML döndür
        return "<!DOCTYPE html><html><head><title>Admin Panel</title></head><body><div id=\"root\"></div></body></html>";
    }
    
    @ExceptionHandler(java.lang.Exception.class)
    public ResponseEntity<com.suleymansecgin.admin_panel.handler.Exception<ErrorMessage>> handleGenericException(
            java.lang.Exception ex, jakarta.servlet.http.HttpServletRequest request) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setMessageType(com.suleymansecgin.admin_panel.exception.MessageType.GENERAL_EXCEPTION);
        errorMessage.setOfStatic(ex.getMessage());
        
        com.suleymansecgin.admin_panel.handler.Exception<ErrorMessage> exceptionResponse = createExceptionResponse(request, errorMessage);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
    }
    
    private ErrorMessage extractErrorMessage(BaseException ex) {
        return ex.getErrorMessage();
    }
    
    private <T> com.suleymansecgin.admin_panel.handler.Exception<T> createExceptionResponse(
            jakarta.servlet.http.HttpServletRequest request, T message) {
        com.suleymansecgin.admin_panel.handler.Exception<T> exceptionResponse = new com.suleymansecgin.admin_panel.handler.Exception<>();
        exceptionResponse.setPath(request.getRequestURI());
        exceptionResponse.setCreateTime(LocalDateTime.now());
        try {
            exceptionResponse.setHostName(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            exceptionResponse.setHostName("unknown");
        }
        exceptionResponse.setMessage(message);
        return exceptionResponse;
    }
}

