package com.tebra.internalservice.modules.reminder.exception;

public class ReminderDuplicateException extends RuntimeException {

    public ReminderDuplicateException(String title) {
        super("Reminder with title " + title + " already exists");
    }
}
