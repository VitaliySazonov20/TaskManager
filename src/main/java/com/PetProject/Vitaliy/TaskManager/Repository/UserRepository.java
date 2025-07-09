package com.PetProject.Vitaliy.TaskManager.Repository;
import com.PetProject.Vitaliy.TaskManager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface UserRepository extends JpaRepository<User, BigInteger >{

    boolean existsByEmail(String email);

    boolean existsById(BigInteger id);

    User getById(BigInteger id);

    void deleteById(BigInteger id);

    User getByEmail(String email);

}
