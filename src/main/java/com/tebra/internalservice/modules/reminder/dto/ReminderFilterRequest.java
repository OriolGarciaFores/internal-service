package com.tebra.internalservice.modules.reminder.dto;

public record ReminderFilterRequest (
        String title,
        Boolean active
) {}