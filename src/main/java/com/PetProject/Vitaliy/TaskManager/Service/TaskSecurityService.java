package com.PetProject.Vitaliy.TaskManager.Service;


import com.PetProject.Vitaliy.TaskManager.Repository.TaskRepository;
import com.PetProject.Vitaliy.TaskManager.entity.Task;
import com.PetProject.Vitaliy.TaskManager.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.Principal;

@Service
public class TaskSecurityService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private SecurityContextService securityContextService;

    public boolean isCreator(BigInteger taskId){
        User user = securityContextService.getCurrentUser();
        Task task = taskRepository.findById(taskId);
        return task.getCreatedBy().getEmail().equals(user.getEmail());
    }

    public boolean isAssignee(BigInteger taskId){
        User user = securityContextService.getCurrentUser();
        Task task = taskRepository.findById(taskId);
        return (task.getAssignedTo() !=null) && task.getAssignedTo().getEmail().equals(user.getEmail());
    }

    public boolean assigneeIsNull(BigInteger taskId){
        Task task = taskRepository.findById(taskId);
        return task.getAssignedTo() == null;
    }

}
