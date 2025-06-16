package com.PetProject.Vitaliy.TaskManager.Repository;

import com.PetProject.Vitaliy.TaskManager.entity.Enum.Role;
import com.PetProject.Vitaliy.TaskManager.entity.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface UserCredentialsRepository extends JpaRepository<UserCredentials, Long > {
    List<UserCredentials> findByRole(Role role);

    @Query("SELECT uc FROM UserCredentials uc JOIN FETCH uc.user u WHERE u.email = :email")
    Optional<UserCredentials> findByUserEmail(String email);
}
