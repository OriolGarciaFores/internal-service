package com.tebra.internalservice.modules.reminder.entity;

import com.tebra.internalservice.modules.reminder.domain.RecurrenceType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "reminder")
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private LocalDateTime nextExecution;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "owner_discord_id", nullable = false, length = 100)
    private String ownerDiscordId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RecurrenceType recurrence;

    @Column(nullable = false)
    private LocalDateTime lastUpdate;

    @PrePersist
    public void onCreate() {
        lastUpdate = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        lastUpdate = LocalDateTime.now();
    }
}
