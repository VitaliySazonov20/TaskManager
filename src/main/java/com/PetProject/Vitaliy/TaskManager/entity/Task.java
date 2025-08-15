package com.PetProject.Vitaliy.TaskManager.entity;

import com.PetProject.Vitaliy.TaskManager.entity.Enum.Priority;
import com.PetProject.Vitaliy.TaskManager.entity.Enum.TaskStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigInteger;
import java.time.LocalDateTime;


@Entity
@Table(name= "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private BigInteger id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "task_status")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", columnDefinition = "task_priority")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private Priority priority;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime creationDate;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updateDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by",  nullable = false)
    private User createdBy;




    public Task(User createdBy, TaskStatus status, String description, String title) {
        this.createdBy = createdBy;
        this.status = status;
        this.description = description;
        this.title = title;
        this.creationDate = LocalDateTime.now();
        this.priority = Priority.NORMAL;
    }

    public Task() {
        this.creationDate = LocalDateTime.now();
        this.status = TaskStatus.TODO;
        this.priority = Priority.NORMAL;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getId() {
        return id;
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

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", assignedTo=" + assignedTo +
                '}';
    }
}
