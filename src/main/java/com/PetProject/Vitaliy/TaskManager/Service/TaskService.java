package com.PetProject.Vitaliy.TaskManager.Service;


import com.PetProject.Vitaliy.TaskManager.Repository.TaskRepository;
import com.PetProject.Vitaliy.TaskManager.Repository.UserCredentialsRepository;
import com.PetProject.Vitaliy.TaskManager.Repository.UserRepository;
import com.PetProject.Vitaliy.TaskManager.entity.Enum.TaskStatus;
import com.PetProject.Vitaliy.TaskManager.entity.Task;
import com.PetProject.Vitaliy.TaskManager.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TaskService {


    @Autowired
    private TaskRepository taskRepo;

    @Autowired
    private UserCredentialsRepository userCredentialsRepository;

    @Transactional
    public void saveTask(Task task) {
        taskRepo.save(task);
    }

    @Transactional(readOnly = true)
    public List<Task> getAllTaskAssignedToUser(User user) {
        List<Task> tasks = taskRepo.findByAssignedTo(user);
        return tasks != null ? tasks : Collections.emptyList();
    }

    @Transactional(readOnly = true)
    public List<Task> getAllTasksCreatedByUser(User user) {
        List<Task> tasks = taskRepo.findByCreatedBy(user);
        return tasks != null ? tasks : Collections.emptyList();
    }

    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        return taskRepo.findAll();
    }

    @Transactional
    public void updateStatus(BigInteger id, TaskStatus status) {
        Objects.requireNonNull(id, "Task ID cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
        Task task = taskRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));;
        if (task == null) {
            throw new EntityNotFoundException("Task not found with ID: " + id);
        }
        task.setStatus(status);
        task.setUpdateDate(LocalDateTime.now());
        try {
            taskRepo.save(task);
        } catch (DataAccessException e) {
            throw new ServiceException("Status update failed", e);
        }
    }

    @Transactional
    public void updateStatus(BigInteger id, TaskStatus status, User user) {
        Task task = taskRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));;
        if (task == null) {
            throw new EntityNotFoundException("Task not found with ID: " + id);
        }
        task.setStatus(status);
        task.setUpdateDate(LocalDateTime.now());
        task.setAssignedTo(user);
        try {
            taskRepo.save(task);
        } catch (DataAccessException e) {
            throw new ServiceException("Status update failed", e);
        }
    }

    @Transactional(readOnly = true)
    public Task getTaskById(BigInteger id) {
        Objects.requireNonNull(id, "Task ID cannot be null");
        Task task = taskRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));;
        if (task == null) {
            throw new EntityNotFoundException("Task not found with ID: " + id);
        }
        return task;
    }

    @Transactional
    public void deleteTaskById(BigInteger id) {
        Objects.requireNonNull(id, "Task ID cannot be null");
        try {
            if (!taskRepo.existsById(id)) {
                throw new EntityNotFoundException("Cannot delete - Task not found with ID: " + id);
            }
            taskRepo.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ServiceException("Cannot delete task due to existing references", e);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to delete task", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Task> eagerLoadAllTasksWithTheirUsers() {
        try {
            List<Task> tasks = taskRepo.findAllTasksWithUsers();
            return tasks != null ? tasks : Collections.emptyList();
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to load tasks with user data", e);
        }
    }

    @Transactional
    public Optional<Task> findLatestTaskByEmail(String email) {
        if (StringUtils.isBlank(email)) {
            throw new IllegalArgumentException("Email must not be blank");
        }
        try {
            Optional<Task> task = taskRepo.findTopByCreatedByEmailOrderByIdDesc(email);
            return taskRepo.findTopByCreatedByEmailOrderByIdDesc(email);
        } catch (DataAccessException e) {
            throw new ServiceException("Failed to fetch latest task", e);
        }
    }
}
