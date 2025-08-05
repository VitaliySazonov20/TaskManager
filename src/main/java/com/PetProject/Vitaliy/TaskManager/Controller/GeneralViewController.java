package com.PetProject.Vitaliy.TaskManager.Controller;

import com.PetProject.Vitaliy.TaskManager.Exception.UserNotFoundException;
import com.PetProject.Vitaliy.TaskManager.Model.*;
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
import jakarta.servlet.http.HttpSession;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

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

    private static final Logger log = LoggerFactory.getLogger(GeneralViewController.class);

    @GetMapping("/")
    public String showMainPage(){
        return "main";
    }

    @GetMapping("/tasks")
    public String showAllTasks(Model model) {
        prepareBaseModelForAllTasks(model);
        try {
            User currentUser = securityContextService.getCurrentUser();
            List<Task> assignedTasks = taskService.getAllTaskAssignedToUser(currentUser);
            List<Task> createdTasks = taskService.getAllTasksCreatedByUser(currentUser);


            model.addAttribute("assignedTasks", assignedTasks);
            model.addAttribute("createdTasks", createdTasks);
        } catch (Exception e) {
            log.error("Unexpected error", e);
            model.addAttribute("error", "Unexpected error occurred. Please try again.");
        }
        return "tasks";
    }

    private void prepareBaseModelForAllTasks(Model model) {
        model.addAttribute("newTask", new TaskModel());
        model.addAttribute("assignedTasks", Collections.emptyList());
        model.addAttribute("createdTasks", Collections.emptyList());
        model.addAttribute("priorityClass", Priority.class);
    }

    @PostMapping("/tasks")
    public String addATask(@ModelAttribute("newTask") TaskModel newTask,
                           RedirectAttributes redirectAttributes) {
        try {
            User currentUser = securityContextService.getCurrentUser();
            Task task = new Task();
            task.setTitle(newTask.getTitle());
            task.setDescription(newTask.getDescription());
            task.setCreatedBy(currentUser);
            task.setDueDate(newTask.getDueDate());
            if (newTask.getUserId() == null) {
                task.setStatus(TaskStatus.BACKLOG);
            } else {
                User user = userService.getUserById(newTask.getUserId());
                task.setAssignedTo(user);
            }
            task.setPriority(newTask.getPriority());
            taskService.saveTask(task);
        } catch (Exception e) {
            log.error("Task creation failed", e);
            redirectAttributes.addFlashAttribute("error",
                    "Unexpected error occurred when creating new task");
        }
        return "redirect:/tasks";
    }

    @GetMapping("/dashboard")
    public String viewDashboard(Model model) {
        model.addAttribute("taskStatus", TaskStatus.class);
        model.addAttribute("taskPriority", Priority.class);
        model.addAttribute("currentUserId", securityContextService.getCurrentUser().getId());
        try {
            List<Task> allTasks = taskService.eagerLoadAllTasksWithTheirUsers();
            model.addAttribute("allTasks", allTasks);
        } catch (Exception e) {
            log.error("Unexpected error in dashboard", e);
            model.addAttribute("allTasks", Collections.emptyList());
            model.addAttribute("error", "An unexpected error occurred.");
        }
        return "dashboard";
    }

    @PostMapping("/tasks/{id}/accept")
    public String acceptTask(@PathVariable BigInteger id,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes) {
        User user = securityContextService.getCurrentUser();
        try {
            taskService.updateStatus(id, TaskStatus.IN_PROGRESS, user);
        } catch (Exception e) {
            log.error("Could not update task status",e);
            redirectAttributes.addFlashAttribute("error",
                    "Couldn't update task status");
        }
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/tasks/{id}/complete")
    public String completeTask(@PathVariable BigInteger id,
                               HttpServletRequest request,
                               RedirectAttributes redirectAttributes) {
        try {
            taskService.updateStatus(id, TaskStatus.DONE);
        } catch (ServiceException e) {
            log.error("Could not update task status",e);
            redirectAttributes.addFlashAttribute("error",
                    "Couldn't update task status");
        }
        return "redirect:" + request.getHeader("Referer");
    }

    //    @PreAuthorize("""
//            hasRole('ADMIN') or
//            @taskSecurityService.isCreator(#taskId,principal) or
//            @taskSecurityService.isAssignee(#taskId,principal)
//            """)
    @GetMapping("/tasks/{taskId}")
    public String viewTask(@PathVariable BigInteger taskId,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        try {
            if (!securityContextService.isAdmin() &&
                    !taskSecurityService.isCreator(taskId) &&
                    !taskSecurityService.isAssignee(taskId) &&
                    !taskSecurityService.assigneeIsNull(taskId)) {
                List<Task> allTasks = taskService.getAllTasks();

                model.addAttribute("permissionDenied", true);
                model.addAttribute("allTasks", allTasks);
                model.addAttribute("taskStatus", TaskStatus.class);
                model.addAttribute("taskPriority", Priority.class);
                model.addAttribute("currentUserId", securityContextService.getCurrentUser().getId());
                return "dashboard";
            }
            Task currentTask = taskService.getTaskById(taskId);
            List<Comment> allComments = commentService.getCommentsById(taskId);
            model.addAttribute("currentTask", currentTask);
            model.addAttribute("taskStatus", TaskStatus.class);
            model.addAttribute("taskPriority", Priority.class);
            model.addAttribute("allComments", allComments);
            model.addAttribute("currentUserId", securityContextService.getCurrentUser().getId());
            return "currentTask";
        } catch (Exception e){
            log.error("Error viewing task");
            redirectAttributes.addFlashAttribute("error", "Unexpected error." +
                    "Couldn't view the task. Please try again later");
            return "redirect:/dashboard";
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public String viewAllUsers(Model model) {
        try {
            model.addAttribute("allUsers", userService.getAllUsers());
            model.addAttribute("selectedUser", new UserModel());
            model.addAttribute("newUser", new RegistrationForm());
        } catch (Exception e){
            log.error("Couldn't view all users", e);
            model.addAttribute("allUsers", Collections.emptyList());
            model.addAttribute("selectedUser", new UserModel());
            model.addAttribute("newUser", new RegistrationForm());
        }
        return "allUsers";
    }

    @GetMapping("/user")
    public String viewCurrentUser(Model model,
                                  RedirectAttributes redirectAttributes) {
        try{
            User currentUser = securityContextService.getCurrentUser();
            UserModel editedUser = new UserModel();
            editedUser.setFirstName(currentUser.getFirstName());
            editedUser.setLastName(currentUser.getLastName());
            editedUser.setEmail(currentUser.getEmail());
            PasswordChange passwordChange = new PasswordChange();
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("editedUser", editedUser);
            model.addAttribute("passwordChange", passwordChange);
        } catch (Exception e){
            log.error("Unexpected error",e);
            redirectAttributes.addFlashAttribute("An unexpected error occurred." +
                    " Redirected to different page");
            return "redirect:/dashboard";
        }
        return "currentUser";

    }

    @GetMapping("/comments/fragment")
    public String getCommentsFragment(@RequestParam BigInteger taskId, Model model) {
        try{
            model.addAttribute("allComments", commentService.getCommentsById(taskId));
        } catch (Exception e){
            log.error("Unexpected error",e);
            model.addAttribute("allComments", Collections.emptyList());
        }
        return "fragments/allComments :: commentsList";

    }


    @GetMapping("/badgeContainer/fragment")
    public String getBadgeContainerFragment(@RequestParam BigInteger taskId, Model model) {
        try{
            model.addAttribute("currentTask", taskService.getTaskById(taskId));

        } catch (Exception e){
            log.error("Unexpected error",e);
            model.addAttribute("currentTask", new Task());
        }
        model.addAttribute("taskStatus", TaskStatus.class);
        model.addAttribute("taskPriority", Priority.class);
        return "fragments/task-badge-container :: badge-container";
    }

    @GetMapping("/logs")
    public String getAllLogs(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam(required = false) String action,
                             @RequestParam(required = false) String username,
                             @RequestParam(required = false) LocalDateTime startDate,
                             @RequestParam(required = false) LocalDateTime endDate,
                             Model model,
                             HttpSession session) throws ServiceException {
        try {
            LocalDateTime lastValidStartDate = (LocalDateTime) session.getAttribute("startDate");
            LocalDateTime lastValidEndDate = (LocalDateTime) session.getAttribute("endDate");

            if (startDate != null && endDate != null) {
                if (startDate.isAfter(endDate)) {
                    model.addAttribute("error", "Start date must be before end date");
                    if (lastValidStartDate != null && lastValidEndDate != null) {
                        startDate = lastValidStartDate;
                        endDate = lastValidEndDate;
                    } else {
                        startDate = null;
                        endDate = null;
                        page = 0;
                        size = 10;
                    }

                }
            }


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

            if (startDate != null && endDate != null) {
                session.setAttribute("startDate", startDate);
                session.setAttribute("endDate", endDate);
            }
            return "audit-logs";
        } catch (Exception e) {
            log.error("Error loading audit logs", e);
            model.addAttribute("error", "Failed to load logs: " + e.getMessage());
            model.addAttribute("logs", Page.empty());
            model.addAttribute("logsJson", "");
            return "audit-logs";
        }
    }


}
