package com.tebra.internalservice.modules.reminder.service;

import com.tebra.internalservice.modules.reminder.domain.RecurrenceType;
import com.tebra.internalservice.modules.reminder.dto.ReminderCreateRequest;
import com.tebra.internalservice.modules.reminder.dto.ReminderResponse;
import com.tebra.internalservice.modules.reminder.entity.Reminder;
import com.tebra.internalservice.modules.reminder.repository.ReminderRepository;
import com.tebra.internalservice.utils.UtilsDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        reminder.setTitle(request.getTitle());
        reminder.setMessage(request.getMessage());
        reminder.setNextExecution(UtilsDate.parseDate(request.getNextExecution(), "dd-MM-yyyy HH:mm"));
        reminder.setActive(true);
        reminder.setOwnerDiscordId(request.getOwnerDiscordId());
        reminder.setRecurrence(request.getRecurrence());

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
        reminderResponse.setTitle(reminder.getTitle());
        reminderResponse.setMessage(reminder.getMessage());
        reminderResponse.setNextExecution(reminder.getNextExecution());
        reminderResponse.setActive(reminder.isActive());
        reminderResponse.setOwnerDiscordId(reminder.getOwnerDiscordId());
        reminderResponse.setRecurrence(reminder.getRecurrence());
        return reminderResponse;
    }

    @Transactional
    public void processPendingReminders() {
        LocalDateTime now = LocalDateTime.now();
        List<Reminder> reminders = reminderRepository.findByActiveTrueAndNextExecutionLessThanEqual(now);

        for (Reminder reminder : reminders) {
            if (reminder.getRecurrence().compareTo(RecurrenceType.NONE) == 0) {
                reminder.setActive(false);
            } else {
                reminder.setNextExecution(calculateNextExecution(reminder));
            }
        }
    }

    private LocalDateTime calculateNextExecution(Reminder reminder) {
        return switch (reminder.getRecurrence()) {
            case DAILY -> reminder.getNextExecution().plusDays(1);
            case WEEKLY -> reminder.getNextExecution().plusWeeks(1);
            case MONTHLY -> reminder.getNextExecution().plusMonths(1);
            case NONE -> null;
        };
    }
}
