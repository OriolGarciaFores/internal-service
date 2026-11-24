package com.tebra.internalservice.modules.storage.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FileResponse {

    private Long id;
    private String title;
    private String type;
    private LocalDateTime createDate;
    private LocalDateTime lastUpdate;
}
