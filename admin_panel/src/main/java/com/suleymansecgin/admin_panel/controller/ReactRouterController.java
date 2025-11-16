package com.suleymansecgin.admin_panel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ReactRouterController {
    
    /**
     * React Router için fallback controller.
     * Tüm frontend route'ları index.html'e yönlendirir.
     * Bu sayede sayfa yenilendiğinde (F5) React Router client-side routing yapabilir.
     * API route'ları (/api/**) hariç tutulur.
     */
    @GetMapping(value = {
        "/",
        "/login",
        "/register",
        "/dashboard",
        "/products",
        "/settings",
        "/reports",
        "/statistics"
    })
    public String forwardToIndex() {
        return "forward:/index.html";
    }
    
    /**
     * Diğer frontend route'ları için catch-all.
     * API route'larını (/api/**) hariç tutar.
     */
    @GetMapping(value = "/{path:[^\\.]*}")
    public String forwardToIndexExcludingApi(@PathVariable String path) {
        // API route'larını hariç tut
        if (path != null && path.startsWith("api")) {
            return null; // Spring'in diğer handler'larına bırak
        }
        return "forward:/index.html";
    }
}

