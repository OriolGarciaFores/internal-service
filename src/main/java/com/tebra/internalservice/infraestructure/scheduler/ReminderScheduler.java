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
        loadToday();
    }

    @Scheduled(cron = Constants.TIME_RELOAD_DAILY)
    private void loadReminders() {
        loadToday();
    }

    private void loadToday() {
        List<ReminderResponse> reminders = reminderService.findAllPendingToday();

        for (ReminderResponse reminder : reminders) {
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
    }

    private void cancel(Long reminderId) {
        ScheduledFuture<?> future = scheduleTasks.remove(reminderId);

        if (future != null) {
            future.cancel(false);
        }
    }

    private void execute(Long reminderId) {
        ReminderResponse reminder = reminderService.findById(reminderId);
        //send message discord api
        log.info("Reminder {} has been scheduled", reminder.getId());
        log.info("Reminder message: {}", reminder.getMessage());
        reminderService.updateNextExecutionOrFinish(reminderId);
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
