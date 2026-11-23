package com.tebra.internalservice.infraestructure.scheduler;

import com.tebra.internalservice.infraestructure.storage.FileStorage;
import com.tebra.internalservice.modules.storage.service.FileService;
import com.tebra.internalservice.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
public class FileScheduler {

    private final TaskScheduler taskScheduler;
    private final Map<Long, ScheduledFuture<?>> scheduleTasks = new ConcurrentHashMap<>();
    private final FileStorage fileStorage;
    private final FileService fileService;

    public FileScheduler(TaskScheduler taskScheduler, FileStorage fileStorage, FileService fileService) {
        this.taskScheduler = taskScheduler;
        this.fileStorage = fileStorage;
        this.fileService = fileService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initialize() {
        log.info("Initializing file scheduler");
    }

    @Scheduled(cron = Constants.TIME_RELOAD_DAILY)
    private void cleanFiles() {
        log.info("Cleaning up all files old");
    }


}
