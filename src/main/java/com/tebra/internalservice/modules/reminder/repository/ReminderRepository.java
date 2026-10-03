package com.tebra.internalservice.modules.reminder.repository;

import com.tebra.internalservice.modules.reminder.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findByActiveTrueAndNextExecutionLessThan(LocalDateTime date);

    Optional<Reminder> findByOwnerDiscordIdAndTitle(String ownerDiscordId, String title);
}
