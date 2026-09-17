package pl.xwolfq.monitor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Konfiguracja Web MVC — polityka CORS dla środowiska deweloperskiego.
 * <p>Zezwala serwerom React (Vite: {@code localhost:5173}, CRA: {@code localhost:3000})
 * na wywoływanie endpointów {@code /api/v1/**} metodami GET, POST i OPTIONS
 * (preflight dla żądań z niestandardowymi nagłówkami, np. Content-Type).</p>
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/v1/**")
                .allowedOrigins("http://localhost:5173", "http://localhost:3000")
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}