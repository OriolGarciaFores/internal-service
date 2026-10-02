package com.tebra.internalservice.modules.reminder;

import com.tebra.internalservice.modules.reminder.dto.ReminderCreateRequest;
import com.tebra.internalservice.modules.reminder.dto.ReminderResponse;
import com.tebra.internalservice.modules.reminder.service.ReminderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {

    private final ReminderService reminderService;

    public ReminderController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @PostMapping
    public ReminderResponse create(@RequestBody ReminderCreateRequest request) {
        return reminderService.create(request);
    }

    @GetMapping
    public List<ReminderResponse> findAll() {
        return reminderService.findAll();
    }
}
