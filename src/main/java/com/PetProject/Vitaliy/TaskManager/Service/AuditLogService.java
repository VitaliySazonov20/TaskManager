package com.PetProject.Vitaliy.TaskManager.Service;

import com.PetProject.Vitaliy.TaskManager.Aspect.AuditLogAspect;
import com.PetProject.Vitaliy.TaskManager.Repository.AuditLogRepository;
import com.PetProject.Vitaliy.TaskManager.Repository.TaskRepository;
import com.PetProject.Vitaliy.TaskManager.entity.AuditLog;
import com.PetProject.Vitaliy.TaskManager.entity.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

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

    private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);

    @Transactional
    public void save(AuditLog log) {
        auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public Optional<Task> getLatestTaskByUserEmail(String email) {
        return taskRepository.findTopByCreatedByEmailOrderByIdDesc(email);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getAllLogs(Pageable pageable) {
        return auditLogRepository.findAllPaginated(pageable);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public Page<AuditLog> getFilteredLogs(String action,
                                          String username,
                                          LocalDateTime startDate,
                                          LocalDateTime endDate,
                                          Pageable pageable) {

        action = StringUtils.hasText(action) ? action : null;
        username = StringUtils.hasText(username) ? username : null;
        return auditLogRepository.findFiltered(action, username, startDate, endDate, pageable);
    }

    @Transactional
    public List<AuditLog> getAllLogsAsList() {
        return auditLogRepository.findAllLogsAsList();
    }

    public List<AuditLog> getAllFilteredLogsAsList(String action,
                                                   String username,
                                                   LocalDateTime startDate,
                                                   LocalDateTime endDate) {

        action = StringUtils.hasText(action) ? action : null;
        username = StringUtils.hasText(username) ? username : null;
        return auditLogRepository.findFilteredAsList(action, username, startDate, endDate);
    }
}
