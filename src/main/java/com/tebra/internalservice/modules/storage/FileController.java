package com.tebra.internalservice.modules.storage;

import com.tebra.internalservice.infraestructure.storage.FileStorage;
import com.tebra.internalservice.modules.storage.dto.FileFilterRequest;
import com.tebra.internalservice.modules.storage.dto.FileResponse;
import com.tebra.internalservice.modules.storage.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;
    private final FileStorage fileStorage;

    public FileController(FileService fileService, FileStorage fileStorage) {
        this.fileService = fileService;
        this.fileStorage = fileStorage;
    }

    @PostMapping
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        fileStorage.save(file);
        fileService.register(file);
        return ResponseEntity.ok("Archivo subido correctamente");
    }

    @GetMapping
    public List<FileResponse> getFiles(FileFilterRequest request) {
        return fileService.findWithFilters(request.title(), request.type());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable("id") Long id) {
        FileResponse file = fileService.findById(id);
        String fileName = (file.getType() != null && !file.getType().isBlank())
                ? file.getTitle() + "." + file.getType().replaceFirst("^\\.", "")
                : file.getTitle();

        fileStorage.delete(fileName);
        fileService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable("id") Long id) {
        FileResponse file = fileService.findById(id);
        String fileName = (file.getType() != null && !file.getType().isBlank())
                ? file.getTitle() + "." + file.getType().replaceFirst("^\\.", "")
                : file.getTitle();

        String contentType = URLConnection.guessContentTypeFromName(fileName);
        MediaType mediaType = (contentType != null)
                ? MediaType.parseMediaType(contentType)
                : MediaType.APPLICATION_OCTET_STREAM;

        ContentDisposition contentDisposition = ContentDisposition.attachment().filename(fileName, StandardCharsets.UTF_8).build();

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(fileStorage.load(fileName));
    }

}
