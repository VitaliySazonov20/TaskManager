package com.PetProject.Vitaliy.TaskManager.Controller.REST;

import com.PetProject.Vitaliy.TaskManager.Model.CommentModel;
import com.PetProject.Vitaliy.TaskManager.Model.TaskModel;
import com.PetProject.Vitaliy.TaskManager.Service.CommentService;
import com.PetProject.Vitaliy.TaskManager.Service.TaskService;
import com.PetProject.Vitaliy.TaskManager.entity.Comment;
import com.PetProject.Vitaliy.TaskManager.entity.Enum.Priority;
import com.PetProject.Vitaliy.TaskManager.entity.Task;
import com.PetProject.Vitaliy.TaskManager.entity.UserCredentials;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;


@Tag(name = "Task Management")
@RestController
@RequestMapping("/api/tasks")
public class TaskController {


    private static final Logger log = LoggerFactory.getLogger(TaskController.class);
    @Autowired
    private TaskService taskService;

    @Autowired
    private CommentService commentService;


    @Operation(summary = "Add a comment to task")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Comment created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommentModel.class),
                            examples = @ExampleObject(
                                    value = "{\"taskId\": 123, \"userId\":456," +
                                            " \"commentId\": 789, \"msg\":\"Sample comment\"}"
                            )
                    )),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Empty comment",
                                            value = "{\"error\": \"Comment text cannot be empty\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Invalid task ID",
                                            value = "{\"error\": \"Invalid task ID format\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - User not authorized to comment on this task"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"error\": \"Task not found\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"error\": \"Failed to save comment\"}"
                            )
                    )
            )
    })
    @PostMapping("/{taskId}/comments")
    public ResponseEntity<?> addComment(
            @Parameter(description = "Task ID", required = true, example = "1")
            @PathVariable BigInteger taskId,
            @RequestBody String message,
            @AuthenticationPrincipal UserCredentials userCredentials) {
        try{
            Comment comment = new Comment();
            comment.setTask(taskService.getTaskById(taskId));
            comment.setUser(userCredentials.getUser());
            comment.setText(message);
            commentService.saveComment(comment);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new CommentModel(
                            comment.getTask().getId(),
                            comment.getUser().getId(),
                            comment.getId(),
                            comment.getText()
                    ));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error","Failed to add comment"));
        }
    }

    @Operation(summary = "Change task priority")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Priority updated successfully",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject("Priority successfully changed")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid priority value",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"error\": \"Invalid priority value." +
                                            " Valid values: MINOR, NORMAL, CRITICAL, URGENT\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - User lacks permission to modify this task",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"error\": \"You don't have permission to modify this task\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Task not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"error\": \"Task not found\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"error\": \"Failed to update task priority\"}"
                            )
                    )
            )
    })
    @PatchMapping("/{taskId}/priority")
    public ResponseEntity<String> changePriority(
            @Parameter(description = "Task ID", required = true, example = "1")
            @PathVariable BigInteger taskId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "String format of priority",
                    required = true,
                    content = @Content(
                            mediaType = "text/plain",
                            examples = {
                                    @ExampleObject(value = "MINOR"),
                                    @ExampleObject(value = "NORMAL"),
                                    @ExampleObject(value = "CRITICAL"),
                                    @ExampleObject(value = "URGENT")
                            }
                    )
            )
            @RequestBody String priorityString) {
        try {
            if(priorityString == null || priorityString.isEmpty() || priorityString.isBlank()){
                return ResponseEntity.badRequest().body("Priority cannot be empty");
            }

            Task task = taskService.getTaskById(taskId);
            Priority priority;
            try{
                priority = Priority.valueOf(priorityString.toUpperCase());
            } catch (IllegalArgumentException e){
                log.error("Invalid priority value was used", e);
                return ResponseEntity.badRequest().body("Invalid priority value. Valid values are:" +
                        Arrays.toString(Priority.values()));
            }
            task.setPriority(priority);
            taskService.saveTask(task);
            return ResponseEntity.status(HttpStatus.OK).body("Status successfully changed");
        } catch (Exception e){
            log.error("Unexpected error occurred",e);
            return ResponseEntity.internalServerError().body("Error updating task priority");
        }
    }

    @Operation(summary = "Get task by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task found"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{taskId}")
    public ResponseEntity<?> getTaskById(
            @Parameter(description = "Task ID", required = true, example = "1")
            @PathVariable BigInteger taskId) {


        try{
            Task task = taskService.getTaskById(taskId);

            if (task == null) {
                return ResponseEntity.notFound().build();
            }

            TaskModel taskModel = new TaskModel();
            if(task.getAssignedTo() !=null){
                taskModel.setUserId(task.getAssignedTo().getId());

            }
            taskModel.setDueDate(task.getDueDate());
            taskModel.setPriority(task.getPriority());
            taskModel.setTitle(task.getTitle());
            taskModel.setDescription(task.getDescription());
            taskModel.setCreatedByUserId(task.getCreatedBy().getId());

            return ResponseEntity.ok(taskModel);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving task: " + e.getMessage());
        }
    }
}
