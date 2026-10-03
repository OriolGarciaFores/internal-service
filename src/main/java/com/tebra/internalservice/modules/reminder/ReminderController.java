package com.tebra.internalservice.modules.reminder;

import com.tebra.internalservice.infraestructure.scheduler.ReminderScheduler;
import com.tebra.internalservice.modules.reminder.dto.ReminderCreateRequest;
import com.tebra.internalservice.modules.reminder.dto.ReminderResponse;
import com.tebra.internalservice.modules.reminder.service.ReminderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {

    private final ReminderService reminderService;

    private final ReminderScheduler reminderScheduler;

    public ReminderController(ReminderService reminderService, ReminderScheduler reminderScheduler) {
        this.reminderService = reminderService;
        this.reminderScheduler = reminderScheduler;
    }

    @PostMapping
    public ReminderResponse create(@RequestBody ReminderCreateRequest request) {
        ReminderResponse reminder = reminderService.create(request);
        reminderScheduler.scheduleIfToday(reminder);
        return reminder;
    }

    @GetMapping
    public List<ReminderResponse> findAll() {
        return reminderService.findAll();
    }
}
