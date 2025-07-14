package com.PetProject.Vitaliy.TaskManager.Controller.REST;


import com.PetProject.Vitaliy.TaskManager.Service.LogExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/logs")
public class AuditLogController {

    @Autowired
    private LogExportService logExportService;

    @GetMapping(value = "/export",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> exportLogs(@RequestParam(required = false) String action,
                                               @RequestParam(required = false) String username,
                                               @RequestParam(required = false) LocalDateTime startDate,
                                               @RequestParam(required = false) LocalDateTime endDate){

        try {
            Resource resource = logExportService.exportToJson(action,username,startDate,endDate);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=audit-logs-"+ LocalDateTime.now()+ ".json")
                    .contentType(MediaType.APPLICATION_JSON)
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ByteArrayResource("Export failed".getBytes()));
        }
    }
}
