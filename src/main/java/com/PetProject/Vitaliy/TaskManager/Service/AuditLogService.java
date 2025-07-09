package com.PetProject.Vitaliy.TaskManager.Service;

import com.PetProject.Vitaliy.TaskManager.Repository.AuditLogRepository;
import com.PetProject.Vitaliy.TaskManager.Repository.TaskRepository;
import com.PetProject.Vitaliy.TaskManager.entity.AuditLog;
import com.PetProject.Vitaliy.TaskManager.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private TaskRepository taskRepository;

    public void save(AuditLog log){
        auditLogRepository.save(log);
    }

    public Optional<Task> getLatestTaskByUserEmail(String email){
        return taskRepository.findTopByCreatedByEmailOrderByIdDesc(email);
    }
}
