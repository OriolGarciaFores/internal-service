package com.tebra.internalservice.infraestructure.scheduler;

import com.tebra.internalservice.modules.reminder.dto.ReminderResponse;
import com.tebra.internalservice.modules.reminder.service.ReminderService;
import com.tebra.internalservice.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
public class ReminderScheduler {

    private final TaskScheduler taskScheduler;
    private final Map<Long, ScheduledFuture<?>> scheduleTasks = new ConcurrentHashMap<>();
    private final ReminderService reminderService;

    public ReminderScheduler(TaskScheduler taskScheduler, ReminderService reminderService) {
        this.taskScheduler = taskScheduler;
        this.reminderService = reminderService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        log.info("Initializing events");
        loadToday();
    }

    @Scheduled(cron = Constants.TIME_RELOAD_DAILY)
    private void loadReminders() {
        log.info("Loading reminders daily");
        loadToday();
    }

    private void loadToday() {
        List<ReminderResponse> reminders = reminderService.findAllPendingToday();
        LocalDateTime now = LocalDateTime.now();

        for (ReminderResponse reminder : reminders) {
            if (reminder.getNextExecution().isBefore(now)) {
                ReminderResponse reminderUpdated = reminderService.updateNextExecutionOrFinish(reminder.getId());
                if (reminderUpdated.getNextExecution().isBefore(now)) {
                    continue;
                }

                reminder = reminderUpdated;
            }

            schedule(reminder);
        }
    }

    public void schedule(ReminderResponse reminderResponse) {
        cancel(reminderResponse.getId());

        ScheduledFuture<?> future = taskScheduler.schedule(
                () -> execute(reminderResponse.getId()),
                reminderResponse.getNextExecution().atZone(ZoneId.systemDefault()).toInstant()
        );

        scheduleTasks.put(reminderResponse.getId(), future);
        log.info("Scheduled Reminder: {}", reminderResponse.getTitle());
    }

    private void cancel(Long reminderId) {
        ScheduledFuture<?> future = scheduleTasks.remove(reminderId);

        if (future == null) return;

        if (future.cancel(false)) {
           log.info("Cancelled Reminder: {}", reminderId);
        } else  {
            log.warn("Could not cancel Reminder: {}", reminderId);
        }
    }

    private void execute(Long reminderId) {
        ReminderResponse reminder = reminderService.findById(reminderId);
        //send message discord api
        reminderService.updateNextExecutionOrFinish(reminderId);
        log.info("Scheduled executed Reminder: {}", reminder.getTitle());
    }

    public void scheduleIfToday(ReminderResponse reminder) {
        if (reminder.getNextExecution().toLocalDate().equals(LocalDate.now())) {
            schedule(reminder);
        }

        if (reminder.getNextExecution().toLocalDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de ejecución debe ser futura");
        }
    }
}
