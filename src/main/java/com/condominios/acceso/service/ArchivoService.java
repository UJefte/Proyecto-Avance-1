package com.condominios.acceso.service;

import com.condominios.acceso.exception.ReglaNegocioException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Guarda las fotos de las visitas en la carpeta "imagenes".
 * Algo sencillo: el archivo se escribe en disco y en la base solo se guarda su ruta.
 */
@Service
public class ArchivoService {

    // solo aceptamos estos tipos de imagen
    private static final Map<String, String> EXTENSIONES = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp");

    private final Path carpeta;

    public ArchivoService(@Value("${app.carpeta-imagenes}") String carpeta) throws IOException {
        this.carpeta = Paths.get(carpeta).toAbsolutePath().normalize();
        Files.createDirectories(this.carpeta);
    }

    /** Guarda la foto y regresa la ruta con la que se puede ver, por ejemplo /imagenes/abc.jpg */
    public String guardarFoto(MultipartFile foto) {
        if (foto == null || foto.isEmpty()) {
            throw new ReglaNegocioException("No se envio ninguna foto");
        }
        String extension = EXTENSIONES.get(foto.getContentType());
        if (extension == null) {
            throw new ReglaNegocioException("La foto debe ser JPG, PNG o WEBP");
        }
        // nombre al azar para que no se repitan ni se pisen
        String nombre = UUID.randomUUID() + extension;
        try (InputStream entrada = foto.getInputStream()) {
            Files.copy(entrada, carpeta.resolve(nombre), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar la foto", e);
        }
        return "/imagenes/" + nombre;
    }

    public Path getCarpeta() {
        return carpeta;
    }
}
