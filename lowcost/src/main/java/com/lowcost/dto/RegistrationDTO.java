package com.lowcost.dto;

import lombok.Data;

@Data
public class RegistrationDTO {
    private String login;
    private String email;
    private String password;
    private String role; 
}