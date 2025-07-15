package com.PetProject.Vitaliy.TaskManager.Repository;

import com.PetProject.Vitaliy.TaskManager.entity.Enum.TaskStatus;
import com.PetProject.Vitaliy.TaskManager.entity.Task;
import com.PetProject.Vitaliy.TaskManager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.swing.text.html.Option;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long > {

    List<Task> findByAssignedTo(User user);
    List<Task> findByCreatedBy(User user);

    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.assignedTo")
    List<Task> findAllTasksWithUsers();

    List<Task> findByStatusNotAndDueDateBefore(TaskStatus status, LocalDateTime now);
    Task findById(BigInteger id);
    void deleteById(BigInteger id);

    boolean existsById(BigInteger id);

    Optional<Task> findTopByCreatedByEmailOrderByIdDesc(String email);

}
