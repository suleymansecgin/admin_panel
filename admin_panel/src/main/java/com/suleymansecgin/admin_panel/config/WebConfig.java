package com.suleymansecgin.admin_panel.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Static resource handler'ı sadece belirli path'ler için yapılandır
        // API route'larını (/api/**) hariç tutmak için sadece static dosyalar için handler ekle
        
        // index.html için (yüksek öncelik)
        registry.addResourceHandler("/index.html")
                .addResourceLocations("classpath:/static/index.html")
                .setCachePeriod(0);
        
        // Static klasörü ve assets için
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/static/")
                .setCachePeriod(3600);
        
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/")
                .setCachePeriod(3600);
        
        // Dosya uzantılı static dosyalar için (sadece root'ta, /api ile başlamayan)
        registry.addResourceHandler("/*.js", "/*.css", "/*.ico", "/*.png", "/*.jpg", "/*.svg", "/*.woff", "/*.woff2", "/*.ttf", "/*.eot")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);
        
        // Tüm diğer static dosyalar için (API route'ları hariç - en son kontrol)
        // Bu handler sadece /api ile başlamayan path'ler için çalışır
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600)
                .resourceChain(false);
    }
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // API route'larını hariç tutarak frontend route'larını index.html'e yönlendir
        // Sadece GET istekleri için çalışır, API route'ları etkilenmez
        registry.addViewController("/").setViewName("forward:/index.html");
        registry.addViewController("/login").setViewName("forward:/index.html");
        registry.addViewController("/register").setViewName("forward:/index.html");
        registry.addViewController("/dashboard").setViewName("forward:/index.html");
        registry.addViewController("/products").setViewName("forward:/index.html");
        registry.addViewController("/settings").setViewName("forward:/index.html");
        registry.addViewController("/reports").setViewName("forward:/index.html");
        registry.addViewController("/statistics").setViewName("forward:/index.html");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }
}

