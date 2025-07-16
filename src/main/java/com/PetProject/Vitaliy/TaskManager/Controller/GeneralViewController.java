package com.PetProject.Vitaliy.TaskManager.Controller;

import com.PetProject.Vitaliy.TaskManager.Model.PasswordChange;
import com.PetProject.Vitaliy.TaskManager.Model.RegistrationForm;
import com.PetProject.Vitaliy.TaskManager.Model.TaskModel;
import com.PetProject.Vitaliy.TaskManager.Model.UserModel;
import com.PetProject.Vitaliy.TaskManager.Service.*;
import com.PetProject.Vitaliy.TaskManager.entity.AuditLog;
import com.PetProject.Vitaliy.TaskManager.entity.Comment;
import com.PetProject.Vitaliy.TaskManager.entity.Enum.Priority;
import com.PetProject.Vitaliy.TaskManager.entity.Enum.TaskStatus;
import com.PetProject.Vitaliy.TaskManager.entity.Task;
import com.PetProject.Vitaliy.TaskManager.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

@Controller
public class GeneralViewController {


    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityContextService securityContextService;

    @Autowired
    private TaskSecurityService taskSecurityService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping("/tasks")
    public String showAllTasks(Model model){
        User currentUser = securityContextService.getCurrentUser();
        List<Task> assignedTasks = taskService.getAllTaskAssignedToUser(currentUser);
        List<Task> createdTasks = taskService.getAllTasksCreatedByUser(currentUser);

        TaskModel newTask = new TaskModel();
        model.addAttribute("newTask", newTask);
        model.addAttribute("assignedTasks",
                assignedTasks != null ? assignedTasks : Collections.emptyList());
        model.addAttribute("createdTasks",
                createdTasks != null? createdTasks: Collections.emptyList());
        model.addAttribute("priorityClass",Priority.class);
        return "tasks";
    }

    @PostMapping("/tasks")
    public String addATask(Model model, @ModelAttribute("newTask") TaskModel newTask){
        User currentUser = securityContextService.getCurrentUser();
        Task task = new Task();
        task.setTitle(newTask.getTitle());
        task.setDescription(newTask.getDescription());
        task.setCreatedBy(currentUser);
        task.setDueDate(newTask.getDueDate());
        if(newTask.getUserId() ==null){
            task.setStatus(TaskStatus.BACKLOG);
        } else {
            User user = userService.getUserById(newTask.getUserId());
            task.setAssignedTo(user);
        }
        task.setPriority(newTask.getPriority());
        taskService.saveTask(task);
        return "redirect:/tasks";
    }

    @GetMapping("/dashboard")
    public String viewDashboard(Model model){
        List<Task> allTasks = taskService.eagerLoadAllTasksWithTheirUsers();
        model.addAttribute("allTasks",allTasks);
        model.addAttribute("TaskStatus", TaskStatus.class);
        model.addAttribute("TaskPriority",Priority.class);
        model.addAttribute("currentUserId", securityContextService.getCurrentUser().getId());
        return "dashboard";
    }

    @PostMapping("/tasks/{id}/accept")
    public String acceptTask(@PathVariable BigInteger id,
                             HttpServletRequest request){
        User user = securityContextService.getCurrentUser();
        taskService.updateStatus(id,TaskStatus.IN_PROGRESS, user);
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/tasks/{id}/complete")
    public String completeTask(@PathVariable BigInteger id,
                               HttpServletRequest request){
        taskService.updateStatus(id,TaskStatus.DONE);
        return "redirect:" + request.getHeader("Referer");
    }

//    @PreAuthorize("""
//            hasRole('ADMIN') or
//            @taskSecurityService.isCreator(#taskId,principal) or
//            @taskSecurityService.isAssignee(#taskId,principal)
//            """)
    @GetMapping("/tasks/{taskId}")
    public String viewTask(@PathVariable BigInteger taskId,
                           @ModelAttribute("allTasks") List<Task> allTasks,
                           Model model){
        if(!securityContextService.isAdmin() &&
                !taskSecurityService.isCreator(taskId) &&
                !taskSecurityService.isAssignee(taskId) &&
                !taskSecurityService.assigneeIsNull(taskId)){
            allTasks = taskService.getAllTasks();

            model.addAttribute("permissionDenied", true);
            model.addAttribute("allTasks", allTasks);
            model.addAttribute("TaskStatus", TaskStatus.class);
            model.addAttribute("currentUserId", securityContextService.getCurrentUser().getId());
            return "dashboard";
        }
        Task currentTask = taskService.getTaskById(taskId);
        List<Comment> allComments = commentService.getCommentsById(taskId);
        model.addAttribute("currentTask", currentTask);
        model.addAttribute("TaskStatus",TaskStatus.class);
        model.addAttribute("TaskPriority", Priority.class);
        model.addAttribute("allComments",allComments);
        model.addAttribute("currentUserId", securityContextService.getCurrentUser().getId());
        return "currentTask";
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public String viewAllUsers(Model model){
        model.addAttribute("allUsers", userService.getAllUsers());
        model.addAttribute("selectedUser", new UserModel());
        model.addAttribute("newUser", new RegistrationForm());
        return "allUsers";
    }

    @GetMapping("/user")
    public String viewCurrentUser(Model model){
        User currentUser = securityContextService.getCurrentUser();
        UserModel editedUser = new UserModel();
        editedUser.setFirstName(currentUser.getFirstName());
        editedUser.setLastName(currentUser.getLastName());
        editedUser.setEmail(currentUser.getEmail());
        PasswordChange passwordChange = new PasswordChange();
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("editedUser",editedUser);
        model.addAttribute("passwordChange", passwordChange);
        return "currentUser";
    }

    @GetMapping("/comments/fragment")
    public String getCommentsFragment(@RequestParam BigInteger taskId, Model model){
        model.addAttribute("allComments", commentService.getCommentsById(taskId));
        return "fragments/allComments :: commentsList";
    }


    @GetMapping("/badgeContainer/fragment")
    public String getBadgeContainerFragment(@RequestParam BigInteger taskId, Model model){
        model.addAttribute("currentTask", taskService.getTaskById(taskId));
        model.addAttribute("TaskStatus",TaskStatus.class);
        model.addAttribute("TaskPriority", Priority.class);
        return "fragments/task-badge-container :: badge-container";
    }

    @GetMapping("/logs")
    public String getAllLogs(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(required = false) String action,
                                     @RequestParam(required = false) String username,
                                     @RequestParam(required = false) LocalDateTime startDate,
                                     @RequestParam(required = false) LocalDateTime endDate,
                                     Model model) throws JsonProcessingException {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
            Page<AuditLog> logs;

            if (action != null || username != null || startDate != null || endDate != null) {
                logs = auditLogService.getFilteredLogs(action, username, startDate, endDate, pageable);
            } else {
                logs = auditLogService.getAllLogs(pageable);
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            String logsJson = mapper.writeValueAsString(logs.getContent());

            model.addAttribute("logs", logs);
            model.addAttribute("logsJson", logsJson);
            return "audit-logs";
//        } catch (IllegalArgumentException e){
//            return "redirect:/logs";
//        } catch (JsonProcessingException e) {
//            return "redirect:/logs";
//        } catch (ServiceException e){
//            return "redirect:/logs";
        } catch (Exception e){
            return "redirect:/logs";
        }
    }
}
