package com.PetProject.Vitaliy.TaskManager.Service;


import com.PetProject.Vitaliy.TaskManager.Repository.TaskRepository;
import com.PetProject.Vitaliy.TaskManager.entity.Task;
import com.PetProject.Vitaliy.TaskManager.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;
import java.security.Principal;

@Service("taskSecurity")
public class TaskSecurityService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private SecurityContextService securityContextService;

    @Transactional(readOnly = true)
    public boolean isCreator(BigInteger taskId) {
        User user = securityContextService.getCurrentUser();
        Task task = taskRepository.findById(taskId).orElseThrow(() ->
                new EntityNotFoundException("Task not found with ID: " + taskId));
        if (task.getCreatedBy() == null || user.getEmail() == null) {
            return false;
        }
        return task.getCreatedBy().getEmail().equals(user.getEmail());
    }

    @Transactional(readOnly = true)
    public boolean isAssignee(BigInteger taskId) {
        User user = securityContextService.getCurrentUser();
        Task task = taskRepository.findById(taskId).orElseThrow(() ->
                new EntityNotFoundException("Task not found with ID: " + taskId));
        return (task.getAssignedTo() != null)
                && user.getEmail() != null
                && task.getAssignedTo().getEmail() != null
                && task.getAssignedTo().getEmail().equalsIgnoreCase(user.getEmail());

    }

    @Transactional(readOnly = true)
    public boolean assigneeIsNull(BigInteger taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() ->
                new EntityNotFoundException("Task not found with ID: " + taskId));
        return task.getAssignedTo() == null;
    }

}
