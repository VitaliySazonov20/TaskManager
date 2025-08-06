package com.PetProject.Vitaliy.TaskManager.Controller.REST;


import com.PetProject.Vitaliy.TaskManager.Service.LogExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Tag(name = "Audit Log management")
@RestController
@RequestMapping("/api/logs")
public class AuditLogController {

    @Autowired
    private LogExportService logExportService;

    @Operation(
            summary = "Export audit logs",
            description = "Generates JSON file of filtered audit logs"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful export - Returns JSON file",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "string", format = "binary")
            ),
            headers = @Header(
                    name = "Content-Disposition",
                    description = "Contains filename for download",
                    schema = @Schema(type = "string", example = "attachment; filename=audit-logs-2025-07-15T14:30:00.json")
            )),

            @ApiResponse(responseCode = "400", description = "Invalid parameters",
            content = @Content(
                    mediaType = "text/plain",
                    examples = @ExampleObject("Invalid date range")
            )),

            @ApiResponse(responseCode = "500", description = "Server error during export",
            content = @Content(
                    mediaType = "text/plain",
                    examples = @ExampleObject("Export failed")

            )),
    })
    @GetMapping(value = "/export",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> exportLogs(
            @Parameter(name = "Action",description = "Action type filter", example = "LOGIN")
            @RequestParam(required = false) String action,
            @Parameter(name = "Username",description = "Username filter", example = "Email@email.com")
            @RequestParam(required = false) String username,
            @Parameter(name = "Starting date",description = "Start date (inclusive)", example = "2025-07-01T00:00")
            @RequestParam(required = false) LocalDateTime startDate,
            @Parameter(name = "Ending date",description = "End date filter (inclusive)", example = "2025-07-01T23:59")
            @RequestParam(required = false) LocalDateTime endDate){

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(new ByteArrayResource("Invalid date range".getBytes(StandardCharsets.UTF_8)));
        }
        try {

            Resource resource = logExportService.exportToJson(action,username,startDate,endDate);
            return ResponseEntity.ok().
                    header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=audit-logs-"+ LocalDateTime.now()+ ".json")
                    .contentType(MediaType.APPLICATION_JSON)
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(new ByteArrayResource("Export failed".getBytes()));
        }
    }
}
