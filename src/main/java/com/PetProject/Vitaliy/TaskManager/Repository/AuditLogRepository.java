package com.PetProject.Vitaliy.TaskManager.Repository;

import com.PetProject.Vitaliy.TaskManager.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {


    @Query("SELECT a FROM AuditLog a ORDER BY a.timestamp DESC")
    Page<AuditLog> findAllPaginated(Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE " +
            "(:action IS NULL OR a.action = :action) AND " +
            "(:username IS NULL OR a.username LIKE %:username%) AND " +
            "(COALESCE(:startDate, '') = '' OR a.timestamp >= :startDate) AND " +
            "(COALESCE(:endDate, '') = '' OR a.timestamp <= :endDate) " +
            "ORDER BY a.timestamp DESC" )
    Page<AuditLog> findFiltered(@Param("action") String action,
                                @Param("username") String username,
                                @Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate,
                                Pageable pageable);

    @Query("SELECT a FROM AuditLog a ORDER BY a.timestamp DESC")
    List<AuditLog> findAllLogsAsList();

    @Query("SELECT a FROM AuditLog a WHERE " +
            "(:action IS NULL OR a.action = :action) AND " +
            "(:username IS NULL OR a.username LIKE %:username%) AND " +
            "(COALESCE(:startDate, '') = '' OR a.timestamp >= :startDate) AND " +
            "(COALESCE(:endDate, '') = '' OR a.timestamp <= :endDate) " +
            "ORDER BY a.timestamp DESC" )
    List<AuditLog> findFilteredAsList(@Param("action") String action,
                                @Param("username") String username,
                                @Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate);

}
