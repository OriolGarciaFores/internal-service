package com.tebra.internalservice.modules.reminder.dto;

import com.tebra.internalservice.modules.reminder.domain.RecurrenceType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReminderCreateRequest {

    private String title;
    private String message;
    private String nextExecution;
    private String ownerDiscordId;
    private RecurrenceType recurrence;
}
