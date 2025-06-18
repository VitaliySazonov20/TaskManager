package com.PetProject.Vitaliy.TaskManager.Service;


import com.PetProject.Vitaliy.TaskManager.Repository.TaskRepository;
import com.PetProject.Vitaliy.TaskManager.Repository.UserRepository;
import com.PetProject.Vitaliy.TaskManager.entity.Task;
import com.PetProject.Vitaliy.TaskManager.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
