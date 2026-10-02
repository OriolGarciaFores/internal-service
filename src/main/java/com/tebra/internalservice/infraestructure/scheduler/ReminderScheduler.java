package com.tebra.internalservice.infraestructure.scheduler;

import com.tebra.internalservice.modules.reminder.service.ReminderService;
import com.tebra.internalservice.utils.Constants;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReminderScheduler {

    private final ReminderService reminderService;

    public ReminderScheduler(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @Scheduled(fixedRate = Constants.FIVE_SECONDS)
    private void process() {
        reminderService.processPendingReminders();
    }
}
