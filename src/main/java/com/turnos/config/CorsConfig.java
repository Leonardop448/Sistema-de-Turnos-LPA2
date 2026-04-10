package com.turnos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración global de CORS (Cross-Origin Resource Sharing).
 * Permite que el frontend (cualquier origen durante desarrollo) pueda
 * consumir los endpoints de esta API sin ser bloqueado por el navegador.
 *
 * NOTA: En producción se recomienda reemplazar "*" por el dominio real del frontend.
 */
@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")       // Aplica a todos los endpoints bajo /api
                        .allowedOrigins("*")          // Permite cualquier origen (desarrollo)
                        .allowedMethods(              // Métodos HTTP permitidos
                                "GET", "POST", "PUT", "DELETE", "OPTIONS"
                        )
                        .allowedHeaders("*");         // Permite todos los headers
            }
        };
    }
}
