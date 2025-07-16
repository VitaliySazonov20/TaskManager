package com.PetProject.Vitaliy.TaskManager.Service;

import com.PetProject.Vitaliy.TaskManager.Repository.TaskRepository;
import com.PetProject.Vitaliy.TaskManager.entity.Enum.TaskStatus;
import com.PetProject.Vitaliy.TaskManager.entity.Task;
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

    @Transactional
    @Scheduled(cron = "0 0 * * * *")  //Runs at the start of every hour
    public void checkOverDueTasks() {
        try {

            List<Task> tasks = taskRepository.findByStatusNotAndDueDateBefore(
                    TaskStatus.OVERDUE, LocalDateTime.now());
            if (tasks.isEmpty()) {
                return;
            }
            tasks.forEach(task -> {
                task.setStatus(TaskStatus.OVERDUE);
                task.setUpdateDate(LocalDateTime.now());
                taskRepository.save(task);
            });
        } catch (DataAccessException e){
            throw new ServiceException("Database error while processing overdue tasks", e);
        } catch (Exception e){
            throw new ServiceException("Unexpected error processing overdue tasks", e);
        }
    }
}
