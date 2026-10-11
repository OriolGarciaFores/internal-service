package com.tebra.internalservice.modules.reminder.service;

import com.tebra.internalservice.modules.reminder.domain.RecurrenceType;
import com.tebra.internalservice.modules.reminder.dto.ReminderCreateRequest;
import com.tebra.internalservice.modules.reminder.dto.ReminderResponse;
import com.tebra.internalservice.modules.reminder.dto.ReminderUpdateRequest;
import com.tebra.internalservice.modules.reminder.entity.Reminder;
import com.tebra.internalservice.modules.reminder.exception.ReminderDuplicateException;
import com.tebra.internalservice.modules.reminder.exception.ReminderNotFoundException;
import com.tebra.internalservice.modules.reminder.repository.ReminderRepository;
import com.tebra.internalservice.utils.UtilsDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReminderService {

    private final ReminderRepository reminderRepository;

    public ReminderService(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
    }

    public ReminderResponse create(ReminderCreateRequest request) {
        Optional<Reminder> optional = reminderRepository.findByOwnerDiscordIdAndTitle(request.getOwnerDiscordId(), request.getTitle());

        if (optional.isPresent()) {
            Reminder reminder = optional.get();

            if (reminder.isActive()) {
                throw new ReminderDuplicateException(reminder.getTitle());
            } else {
                reminder.setActive(true);
                reminder.setMessage(request.getMessage());
                reminder.setNextExecution(UtilsDate.parseDate(request.getNextExecution(), "dd-MM-yyyy HH:mm"));
                reminder.setRecurrence(request.getRecurrence());
                reminderRepository.save(reminder);
                return convertToResponse(reminder);
            }
        }

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

    public List<ReminderResponse> findWithFilters(String title, Boolean active) {
        return reminderRepository.findWithFilters(title, active)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public List<ReminderResponse> findAllPendingToday() {
        LocalDateTime endDay = LocalDate.now().plusDays(1).atStartOfDay();

        return reminderRepository.findByActiveTrueAndNextExecutionLessThan(endDay)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public ReminderResponse findById(Long reminderId) {
        Reminder reminder = reminderRepository.findById(reminderId).orElseThrow(() -> new ReminderNotFoundException(reminderId));
        return convertToResponse(reminder);
    }

    @Transactional
    public ReminderResponse updateNextExecutionOrFinish(Long reminderId) {
        Reminder reminder = reminderRepository.findById(reminderId).orElseThrow(() -> new ReminderNotFoundException(reminderId));

        if (reminder.getRecurrence().compareTo(RecurrenceType.NONE) == 0) {
            reminder.setActive(false);
        } else {
            reminder.setNextExecution(calculateNextExecution(reminder));
        }

        return convertToResponse(reminder);
    }

    public ReminderResponse update(Long id, ReminderUpdateRequest request) {
        Reminder reminder = reminderRepository.findById(id).orElseThrow(() -> new ReminderNotFoundException(id));

        reminder.setTitle(request.getTitle());
        reminder.setMessage(request.getMessage());
        reminder.setNextExecution(UtilsDate.parseDate(request.getNextExecution(), "dd-MM-yyyy HH:mm"));
        reminder.setRecurrence(request.getRecurrence());
        reminder.setActive(request.getActive());

        if (reminder.getNextExecution().toLocalDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de ejecución debe ser futura");
        }

        return convertToResponse(reminderRepository.save(reminder));
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

    private LocalDateTime calculateNextExecution(Reminder reminder) {
        if (reminder.getRecurrence() == RecurrenceType.NONE) {
            return null;
        }

        LocalDateTime nextExecution = reminder.getNextExecution();
        LocalDateTime now = LocalDateTime.now();

        while (!nextExecution.isAfter(now)) {
            nextExecution = switch (reminder.getRecurrence()) {
                case DAILY -> nextExecution.plusDays(1);
                case WEEKLY -> nextExecution.plusWeeks(1);
                case MONTHLY -> nextExecution.plusMonths(1);
                default -> nextExecution;
            };
        }

        return nextExecution;
    }

    public void delete(Long reminderId) {
        reminderRepository.deleteById(reminderId);
    }
}
