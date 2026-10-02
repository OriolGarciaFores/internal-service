package com.tebra.internalservice.modules.reminder.service;

import com.tebra.internalservice.modules.reminder.dto.ReminderCreateRequest;
import com.tebra.internalservice.modules.reminder.dto.ReminderResponse;
import com.tebra.internalservice.modules.reminder.entity.Reminder;
import com.tebra.internalservice.modules.reminder.repository.ReminderRepository;
import com.tebra.internalservice.utils.UtilsDate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReminderService {

    private final ReminderRepository reminderRepository;

    public ReminderService(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
    }

    public ReminderResponse create(ReminderCreateRequest request) {
        Reminder reminder = new Reminder();
        reminder.setMessage(request.getMessage());
        reminder.setNextExecution(UtilsDate.parseDate(request.getNextExecution(), "dd-MM-yyyy HH:mm"));
        reminder.setActive(true);
        reminder.setOwnerDiscordId(request.getOwnerDiscordId());
        reminder.setRecurrence(request.isRecurrence());

        Reminder reminderCreated = reminderRepository.save(reminder);

        return convertToResponse(reminderCreated);
    }

    public List<ReminderResponse> findAll() {
        return reminderRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    private ReminderResponse convertToResponse(Reminder reminder) {
        ReminderResponse reminderResponse = new ReminderResponse();
        reminderResponse.setId(reminder.getId());
        reminderResponse.setMessage(reminder.getMessage());
        reminderResponse.setNextExecution(reminder.getNextExecution());
        reminderResponse.setActive(reminder.isActive());
        reminderResponse.setOwnerDiscordId(reminder.getOwnerDiscordId());
        reminderResponse.setRecurrence(reminder.isRecurrence());
        return reminderResponse;
    }

    public void processPendingReminders() {
        LocalDateTime now = LocalDateTime.now();

        List<Reminder> reminders = reminderRepository.findByActiveTrueAndNextExecutionLessThanEqual(now);

        for (Reminder reminder : reminders) {
            System.out.println(reminder.getMessage());
        }
    }
}
