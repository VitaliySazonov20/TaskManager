package com.PetProject.Vitaliy.TaskManager.Repository;

import com.PetProject.Vitaliy.TaskManager.entity.Task;
import com.PetProject.Vitaliy.TaskManager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long > {



//    "SELECT uc FROM UserCredentials uc JOIN FETCH uc.user u WHERE u.email = :email"

//    @Query("SELECT t FROM Task t WHERE t.assignedTo = :user")
    List<Task> findByAssignedTo(User user);
//    @Query("SELECT t FROM Task t WHERE t.createdBy = :user")
    List<Task> findByCreatedBy(User user);
}
