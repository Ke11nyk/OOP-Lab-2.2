package com.lowcost.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="users")
public class User {
    @jakarta.persistence.Id
    @Column(name = "id")
    private int Id;
    @Column(name = "login")
    private String login;
    @Column(name = "email")
    private String email;
    @Column(name = "role")
    private String role;
    @Column(name = "password")
    private String password;
    @Column(name = "salt")
    private String salt;


    public User(int id, String login, String email, String role) {
        this.Id = id;
        this.email = email;
        this.login = login;
        this.role = role;
    }
}
