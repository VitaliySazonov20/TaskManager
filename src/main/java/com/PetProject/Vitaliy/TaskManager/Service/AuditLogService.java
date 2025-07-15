package com.PetProject.Vitaliy.TaskManager.Service;

import com.PetProject.Vitaliy.TaskManager.Repository.AuditLogRepository;
import com.PetProject.Vitaliy.TaskManager.Repository.TaskRepository;
import com.PetProject.Vitaliy.TaskManager.entity.AuditLog;
import com.PetProject.Vitaliy.TaskManager.entity.Task;
import jakarta.transaction.Transactional;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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
        Objects.requireNonNull(email, "Email cannot be null");
        try {
            return taskRepository.findTopByCreatedByEmailOrderByIdDesc(email);
        }  catch (DataAccessException e){
            throw new DataAccessException("Failed to retrieve task. Please try again later"){};
        }
    }

    @Transactional
    public Page<AuditLog> getAllLogs(Pageable pageable){
        Objects.requireNonNull(pageable, "Pageable parameter must not be null");
        try {
            return auditLogRepository.findAllPaginated(pageable);
        } catch (DataAccessException e){
            throw new ServiceException("Failed to retrieve audit logs",e);
        }
    }

    @Transactional
    public Page<AuditLog> getFilteredLogs(String action,
                                          String username,
                                          LocalDateTime startDate,
                                          LocalDateTime endDate,
                                          Pageable pageable) {

        action = StringUtils.hasText(action)? action : null;
        username = StringUtils.hasText(username)? username : null;
        Objects.requireNonNull(pageable,"Pageable parameter must not be null");
        if(startDate !=null && endDate != null && startDate.isAfter(endDate)){
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        try {
            return auditLogRepository.findFiltered(action, username, startDate, endDate, pageable);
        } catch (DataAccessException e){
            throw new ServiceException("Failed to retrieve filtered audit logs", e);
        } catch (IllegalArgumentException e){
            throw new ServiceException("Invalid filter parameters: "+ e.getMessage(),e);
        }
    }

    @Transactional
    public List<AuditLog> getAllLogsAsList(){
        try {
            return auditLogRepository.findAllLogsAsList();
        } catch (DataAccessException e){
            throw new ServiceException("Failed to retrieve audit logs", e);
        }
    }

    public List<AuditLog> getAllFilteredLogsAsList(String action,
                                                   String username,
                                                   LocalDateTime startDate,
                                                   LocalDateTime endDate){

        action = StringUtils.hasText(action)? action : null;
        username = StringUtils.hasText(username)? username : null;
        if(startDate !=null && endDate != null && startDate.isAfter(endDate)){
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        try {
            return auditLogRepository.findFilteredAsList(action, username, startDate, endDate);
        } catch (DataAccessException e){
            throw new ServiceException("Failed to retrieve filtered audit logs", e);
        } catch (IllegalArgumentException e){
            throw new ServiceException("Invalid filter parameters: "+ e.getMessage(),e);
        }
    }
}
