package com.PetProject.Vitaliy.TaskManager.Controller;

import com.PetProject.Vitaliy.TaskManager.Model.TaskModel;
import com.PetProject.Vitaliy.TaskManager.Service.SecurityContextService;
import com.PetProject.Vitaliy.TaskManager.Service.TaskService;
import com.PetProject.Vitaliy.TaskManager.Service.UserService;
import com.PetProject.Vitaliy.TaskManager.entity.Enum.TaskStatus;
import com.PetProject.Vitaliy.TaskManager.entity.Task;
import com.PetProject.Vitaliy.TaskManager.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigInteger;
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
        task.setAssignedTo(userService.getUserById(newTask.getUserId()));
        taskService.saveTask(task);
        return "redirect:/tasks";
    }

    @GetMapping("/dashboard")
    public String viewDashboard(Model model){
        List<Task> allTasks = taskService.eagerLoadAllTasksWithTheirUsers();
        model.addAttribute("allTasks",allTasks);
        model.addAttribute("TaskStatus", TaskStatus.class);
        model.addAttribute("currentUserId", securityContextService.getCurrentUser().getId());
        return "dashboard";
    }

    @PostMapping("/tasks/{id}/accept")
    public String acceptTask(@PathVariable BigInteger id){
        User user = securityContextService.getCurrentUser();
        taskService.updateStatus(id,TaskStatus.IN_PROGRESS, user);
        return "redirect:/dashboard";
    }

    @PostMapping("/tasks/{id}/complete")
    public String completeTask(@PathVariable BigInteger id){
        taskService.updateStatus(id,TaskStatus.DONE);
        return "redirect:/dashboard";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public String viewAllUsers(Model model){
        model.addAttribute("allUsers", userService.getAllUsers());
        return "allUsers";
    }
}
