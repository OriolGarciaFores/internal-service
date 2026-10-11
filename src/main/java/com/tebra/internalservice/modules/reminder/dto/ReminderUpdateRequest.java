package com.tebra.internalservice.modules.reminder.dto;

import com.tebra.internalservice.modules.reminder.domain.RecurrenceType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReminderUpdateRequest {

    private Long id;
    private String title;
    private String message;
    private String nextExecution;
    private String ownerDiscordId;
    private RecurrenceType recurrence;
    private Boolean active;
}
