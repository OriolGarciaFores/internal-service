package com.tebra.internalservice.modules.reminder.dto;

import com.tebra.internalservice.modules.reminder.domain.RecurrenceType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReminderResponse {

    private Long id;
    private String title;
    private String message;
    private LocalDateTime nextExecution;
    private boolean active;
    private String ownerDiscordId;
    private RecurrenceType recurrence;
}
