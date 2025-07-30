package com.PetProject.Vitaliy.TaskManager.Service;


import com.PetProject.Vitaliy.TaskManager.Repository.TaskRepository;
import com.PetProject.Vitaliy.TaskManager.entity.Task;
import com.PetProject.Vitaliy.TaskManager.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;
import java.security.Principal;

@Service
public class TaskSecurityService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private SecurityContextService securityContextService;

    public boolean isCreator(BigInteger taskId){
        try {
            User user = securityContextService.getCurrentUser();
            Task task = taskRepository.findById(taskId).orElseThrow(()->
                    new ResponseStatusException(HttpStatus.NOT_FOUND,"Task not found"));
            if(task.getCreatedBy() == null || user.getEmail() == null){
                return false;
            }
            return task.getCreatedBy().getEmail().equals(user.getEmail());
        } catch (Exception e){
            return false;
        }
    }

    public boolean isAssignee(BigInteger taskId){

        try {
            User user = securityContextService.getCurrentUser();
            Task task = taskRepository.findById(taskId).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
            return (task.getAssignedTo() != null)
                    && user.getEmail() !=null
                    && task.getAssignedTo().getEmail() != null
                    && task.getAssignedTo().getEmail().equalsIgnoreCase(user.getEmail());
        } catch (Exception e){
            return false;
        }
    }

    public boolean assigneeIsNull(BigInteger taskId){
        try{
            Task task = taskRepository.findById(taskId).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));;
            return task.getAssignedTo() == null;
        } catch (Exception e){
            return false;
        }
    }

}
