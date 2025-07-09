package com.PetProject.Vitaliy.TaskManager.Model;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class CommentModel {
    private BigInteger taskId;
    private BigInteger userId;
    private String msg;
//    private LocalDateTime creationTimestamp;
    private BigInteger commentId;

    public CommentModel() {
    }

    public CommentModel(BigInteger taskId, BigInteger userId, BigInteger commentId, String msg) {
        this.taskId = taskId;
        this.userId = userId;
        this.commentId = commentId;
        this.msg = msg;
//        this.creationTimestamp = creationTimestamp;
    }

    public BigInteger getCommentId() {
        return commentId;
    }

    public void setCommentId(BigInteger commentId) {
        this.commentId = commentId;
    }

    public BigInteger getTaskId() {
        return taskId;
    }

    public void setTaskId(BigInteger taskId) {
        this.taskId = taskId;
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

//    public LocalDateTime getCreationTimestamp() {
//        return creationTimestamp;
//    }

//    public void setCreationTimestamp(LocalDateTime creationTimestamp) {
//        this.creationTimestamp = creationTimestamp;
//    }

    @Override
    public String toString() {
        return "CommentModel{" +
                "taskId=" + taskId +
                ", userId=" + userId +
                ", msg='" + msg + '\'' +
//                ", creationTimestamp=" + creationTimestamp +
                ", commentId=" + commentId +
                '}';
    }
}
