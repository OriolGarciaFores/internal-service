package com.tebra.internalservice.modules.storage.service;

import com.tebra.internalservice.modules.storage.dto.FileResponse;
import com.tebra.internalservice.modules.storage.entity.File;
import com.tebra.internalservice.modules.storage.exception.FileNotFoundException;
import com.tebra.internalservice.modules.storage.repository.FileRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FileService {

    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public void register(MultipartFile file) {
        File entity = new File();
        String originalName = file.getOriginalFilename();
        String baseName = (originalName != null)
                ? Path.of(originalName).getFileName().toString()
                : "";
        int indexExt = baseName.lastIndexOf(".");

        entity.setTitle((indexExt > 0) ? baseName.substring(0, indexExt) : baseName);
        entity.setType(indexExt == -1 ? "none" : baseName.substring(indexExt + 1).toLowerCase());

        Optional<File> optional = fileRepository.findByTitleAndType(entity.getTitle(), entity.getType());

        if (optional.isPresent()) {
            entity = optional.get();
        } else {
            entity.setCreateDate(LocalDateTime.now());
        }

        entity.setLastUpdate(LocalDateTime.now());

        fileRepository.save(entity);
    }

    public Optional<List<File>> findAll() {
        return Optional.of(fileRepository.findAll());
    }

    public List<FileResponse> findWithFilters(String title, String type) {
        return fileRepository.findWithFilters(title, type)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public List<FileResponse> findExpiredCandidates() {
        return fileRepository.findAllByLastUpdateLessThan(LocalDateTime.now().minusDays(20))
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public FileResponse findById(Long id) {
        File file = fileRepository.findById(id).orElseThrow(() -> new FileNotFoundException(id));
        return convertToResponse(file);
    }

    public void deleteById(Long id) {
        fileRepository.deleteById(id);
    }

    private FileResponse convertToResponse(File file) {
        FileResponse response = new FileResponse();
        response.setId(file.getId());
        response.setTitle(file.getTitle());
        response.setType(file.getType());
        response.setCreateDate(file.getCreateDate());
        response.setLastUpdate(file.getLastUpdate());
        return response;
    }
}
