package com.condominios.acceso.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.carpeta-imagenes}")
    private String carpetaImagenes;

    // las fotos guardadas en la carpeta se pueden ver en /imagenes/nombre.jpg
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String ruta = Paths.get(carpetaImagenes).toAbsolutePath().normalize().toUri().toString();
        if (!ruta.endsWith("/")) {
            ruta = ruta + "/";
        }
        registry.addResourceHandler("/imagenes/**").addResourceLocations(ruta);
    }

    // deja que el frontend (que puede estar en otro dominio) llame al API
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }

    // titulo que sale en Swagger
    @Bean
    public OpenAPI documentacionApi() {
        return new OpenAPI().info(new Info()
                .title("Control de acceso con QR - Condominios")
                .version("v1")
                .description("API para registrar residentes, trabajadores y visitas, generar pases QR y validar entradas y salidas."));
    }
}
