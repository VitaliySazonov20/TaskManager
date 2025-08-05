package com.PetProject.Vitaliy.TaskManager.Service;

import com.PetProject.Vitaliy.TaskManager.Repository.AuditLogRepository;
import com.PetProject.Vitaliy.TaskManager.entity.AuditLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogExportService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Transactional(readOnly = true)
    public Resource exportToJson(String action, String username,
                                 LocalDateTime startDate, LocalDateTime endDate) throws IOException {

        List<AuditLog> logs;

        if (action != null || username != null || startDate != null || endDate != null) {
            logs = auditLogService.getAllFilteredLogsAsList(action, username, startDate, endDate);
        } else {
            logs = auditLogService.getAllLogsAsList();
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        String json = mapper.writeValueAsString(logs);

        Path tempFile = Files.createTempFile("audit-logs-", ".json");

        try {
            Files.write(tempFile, json.getBytes(StandardCharsets.UTF_8));
            return new FileSystemResource(tempFile.toFile());
        } catch (IOException e) {
            Files.deleteIfExists(tempFile);
            throw e;
        }
    }
}
