package com.PetProject.Vitaliy.TaskManager.Service;


import com.PetProject.Vitaliy.TaskManager.Repository.TaskRepository;
import com.PetProject.Vitaliy.TaskManager.Repository.UserRepository;
import com.PetProject.Vitaliy.TaskManager.entity.Enum.TaskStatus;
import com.PetProject.Vitaliy.TaskManager.entity.Task;
import com.PetProject.Vitaliy.TaskManager.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {


    @Autowired
    private TaskRepository taskRepo;


    @Transactional
    public void saveTask(Task task){
        taskRepo.save(task);
    }

    public List<Task> getAllTaskAssignedToUser(User user){
        return taskRepo.findByAssignedTo(user);
    }

    public List<Task> getAllTasksCreatedByUser(User user){
        return taskRepo.findByCreatedBy(user);
    }

    public List<Task> getAllTasks(){
        return taskRepo.findAll();
    }

    @Transactional
    public void updateStatus(BigInteger id, TaskStatus status){
        Task task = taskRepo.findById(id);
        task.setStatus(status);
        task.setUpdateDate(LocalDateTime.now());
        taskRepo.save(task);
    }

    @Transactional
    public void updateStatus(BigInteger id, TaskStatus status, User user){
        Task task = taskRepo.findById(id);
        task.setStatus(status);
        task.setUpdateDate(LocalDateTime.now());
        task.setAssignedTo(user);
        taskRepo.save(task);
    }

    public Task getTaskById(BigInteger id){
        return taskRepo.findById(id);
    }

    @Transactional
    public void deleteTaskById(BigInteger id){
        taskRepo.deleteById(id);
    }
}
