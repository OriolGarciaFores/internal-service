package com.tebra.internalservice.modules.storage.repository;

import com.tebra.internalservice.modules.storage.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {

    Optional<File> findByTitleAndType(String title, String type);

    Optional<List<File>> findAllByLastUpdateLessThan(LocalDateTime lastUpdated);

    @Query("SELECT f FROM File f WHERE " +
            "(:title IS NULL OR LOWER(f.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND" +
            "(:type IS NULL OR LOWER(f.type) LIKE LOWER(CONCAT('%', :type, '%')))")
    List<File> findWithFilters(String title, String type);
}
