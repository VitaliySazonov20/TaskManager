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
        return taskRepo.findByAssignedTo(user);
    }

    @Transactional(readOnly = true)
    public List<Task> getAllTasksCreatedByUser(User user) {
        return taskRepo.findByCreatedBy(user);
    }

    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        return taskRepo.findAll();
    }

    @Transactional
    public void updateStatus(BigInteger id, TaskStatus status) {
        Task task = taskRepo.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Task not found with ID: " + id));
        task.setStatus(status);
        task.setUpdateDate(LocalDateTime.now());
        taskRepo.save(task);
    }

    @Transactional
    public void updateStatus(BigInteger id, TaskStatus status, User user) {
        Task task = taskRepo.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Task not found with ID: " + id));
        task.setStatus(status);
        task.setUpdateDate(LocalDateTime.now());
        task.setAssignedTo(user);
        taskRepo.save(task);
    }

    @Transactional(readOnly = true)
    public Task getTaskById(BigInteger id) {
        return taskRepo.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Task not found with ID: " + id));
    }

    @Transactional
    public void deleteTaskById(BigInteger id) {
        Task task = taskRepo.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Task not found with ID: " + id));
        taskRepo.deleteById(task.getId());
    }

    @Transactional(readOnly = true)
    public List<Task> eagerLoadAllTasksWithTheirUsers() {
        return taskRepo.findAllTasksWithUsers();
    }
//
//    @Transactional
//    public Optional<Task> findLatestTaskByEmail(String email) {
//        return taskRepo.findTopByCreatedByEmailOrderByIdDesc(email);
//    }
}
