package com.PetProject.Vitaliy.TaskManager.Service;

import com.PetProject.Vitaliy.TaskManager.Repository.TaskRepository;
import com.PetProject.Vitaliy.TaskManager.entity.Enum.TaskStatus;
import com.PetProject.Vitaliy.TaskManager.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OverDueTaskUpdater {

    @Autowired
    private TaskRepository taskRepository;

    @Scheduled(cron = "0 0 * * * *")
    public void checkOverDueTasks(){
        List<Task> tasks = taskRepository.findByStatusNotAndDueDateBefore(TaskStatus.OVERDUE, LocalDateTime.now());
        tasks.forEach(task -> {
            task.setStatus(TaskStatus.OVERDUE);
            task.setUpdateDate(LocalDateTime.now());
            taskRepository.save(task);
        });
    }
}
