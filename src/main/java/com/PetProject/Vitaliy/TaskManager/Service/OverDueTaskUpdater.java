package com.PetProject.Vitaliy.TaskManager.Service;

import com.PetProject.Vitaliy.TaskManager.Controller.GeneralViewController;
import com.PetProject.Vitaliy.TaskManager.Repository.TaskRepository;
import com.PetProject.Vitaliy.TaskManager.entity.Enum.TaskStatus;
import com.PetProject.Vitaliy.TaskManager.entity.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OverDueTaskUpdater {

    @Autowired
    private TaskRepository taskRepository;

    private static final Logger log = LoggerFactory.getLogger(OverDueTaskUpdater.class);

    @Transactional
    @Scheduled(cron = "0 0 * * * *")  //Runs at the start of every hour
    public void checkOverDueTasks() {
        log.info("Starting overdue tasks check");
        try {

            List<Task> tasks = taskRepository.findByStatusNotAndDueDateBefore(
                    TaskStatus.OVERDUE, LocalDateTime.now());
            if (tasks.isEmpty()) {
                log.info("Found 0 overdue tasks to update");
                return;
            }
            tasks.forEach(task -> {
                task.setStatus(TaskStatus.OVERDUE);
                task.setUpdateDate(LocalDateTime.now());
                taskRepository.save(task);
            });
        } catch (Exception e){
            log.error("Failed to process overdue tasks", e);
        } finally {
            log.info("Completed overdue tasks check");
        }
    }
}
