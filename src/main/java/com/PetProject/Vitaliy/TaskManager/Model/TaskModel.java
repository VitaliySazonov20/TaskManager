package com.PetProject.Vitaliy.TaskManager.Model;

import com.PetProject.Vitaliy.TaskManager.entity.Enum.Priority;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class TaskModel {
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private BigInteger userId;
    private Priority priority;
    private BigInteger createdByUserId;


    public TaskModel(String title, String description, LocalDateTime dueDate) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
    }

    public TaskModel() {
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public BigInteger getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(BigInteger createdByUserId) {
        this.createdByUserId = createdByUserId;
    }
}
