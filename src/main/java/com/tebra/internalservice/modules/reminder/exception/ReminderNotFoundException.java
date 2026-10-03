package com.tebra.internalservice.modules.reminder.exception;

public class ReminderNotFoundException extends RuntimeException {

    public ReminderNotFoundException(Long id) {
        super("Reminder with id " + id + " not found");
    }
}
