package com.PetProject.Vitaliy.TaskManager.Repository;

import com.PetProject.Vitaliy.TaskManager.entity.Enum.TaskStatus;
import com.PetProject.Vitaliy.TaskManager.entity.Task;
import com.PetProject.Vitaliy.TaskManager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long > {

    List<Task> findByAssignedTo(User user);
    List<Task> findByCreatedBy(User user);
    List<Task> findByStatusNotAndDueDateBefore(TaskStatus status, LocalDateTime now);
}
