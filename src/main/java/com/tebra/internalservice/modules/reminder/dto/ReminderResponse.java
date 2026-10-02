package com.tebra.internalservice.modules.reminder.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReminderResponse {

    private Long id;
    private String message;
    private LocalDateTime nextExecution;
    private boolean active;
    private String ownerDiscordId;
    private boolean recurrence;
}
