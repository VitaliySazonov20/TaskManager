package com.PetProject.Vitaliy.TaskManager.entity;

import com.PetProject.Vitaliy.TaskManager.entity.Enum.Role;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name= "user_credentials")
public class UserCredentials implements UserDetails {

    @Id
    private BigInteger userId;

    @Column(name = "password_hash")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;


    public UserCredentials(String passwordHash, Role role, User user) {
        this.passwordHash = passwordHash;
        this.role = role;
        this.user = user;
    }

    public UserCredentials() {
    }

    public BigInteger getUserId() {
        return userId;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
        return this.user.getEmail();
    }
}
