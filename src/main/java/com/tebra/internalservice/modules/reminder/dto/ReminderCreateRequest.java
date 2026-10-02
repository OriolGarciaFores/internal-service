package com.tebra.internalservice.modules.reminder.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReminderCreateRequest {

    private String message;
    private String nextExecution;
    private String ownerDiscordId;
    private boolean recurrence;
}
