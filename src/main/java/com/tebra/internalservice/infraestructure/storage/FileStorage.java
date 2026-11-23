package com.tebra.internalservice.infraestructure.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Slf4j
@Component
public class FileStorage {

    private final Path baseDirectory;

    public FileStorage(@Value("${storage.dir}") String storageDirectory) {
        this.baseDirectory = Path.of(storageDirectory).toAbsolutePath().normalize();
    }

    public String save(MultipartFile file) {
        try {
            Files.createDirectories(baseDirectory);

            String storedFileName = file.getOriginalFilename();
            Path targetPath = baseDirectory.resolve(storedFileName).normalize();

            if (!targetPath.startsWith(baseDirectory)) {
                log.error("Ruta de archivo no permitida");
                throw new IllegalArgumentException("Ruta de archivo no permitida");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            return storedFileName;
        } catch (IOException e) {
            log.error("No se puedo guardar el archivo: {}", e.getMessage());
            throw  new IllegalArgumentException("No se puedo guardar el archivo", e);
        }
    }

    public boolean delete(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("El nombre del archivo es obligatorio.");
        }

        Path targetPath = baseDirectory.resolve(fileName).normalize();

        if (!targetPath.startsWith(baseDirectory)) {
            log.error("Ruta de archivo no permitida: {}", fileName);
            throw new IllegalArgumentException("Ruta de archivo no permitida");
        }

        try {
            boolean deleted = Files.deleteIfExists(targetPath);

            if (!deleted) {
                log.error("No se puedo eliminar el archivo: {}", fileName);
                throw new IllegalArgumentException("No se puedo eliminar el archivo: " + fileName);
            }

            return true;
        } catch (IOException e) {
            log.error("No se puedo eliminar el archivo: {}", e.getMessage());
            throw  new IllegalArgumentException("No se puedo eliminar el archivo", e);
        }
    }

    public Resource load(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("El nombre del archivo es obligatorio.");
        }

        Path targetPath = baseDirectory.resolve(fileName).normalize();

        if (!targetPath.startsWith(baseDirectory)) {
            log.error("Ruta de archivo no permitida: {}", fileName);
            throw new IllegalArgumentException("Ruta de archivo no permitida");
        }

        if (Files.notExists(targetPath) || !Files.isRegularFile(targetPath)) {
            log.error("El archivo {} no existe", fileName);
            throw new IllegalArgumentException("El archivo no existe");
        }

        try {
            Resource resource = new UrlResource(targetPath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                log.error("El archivo {} no se puede leer", fileName);
                throw new IllegalArgumentException("El archivo no se puede leer");
            }

            return resource;
        } catch (MalformedURLException e) {
            log.error("URI inválida para el archivo {}", fileName);
            throw new IllegalArgumentException("Error al cargar el archivo " + fileName, e);
        }
    }
}
