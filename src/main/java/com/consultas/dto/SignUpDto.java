package com.consultas.dto;

import com.consultas.entity.Cargo;

public record SignUpDto (String firstName, String lastName, String login, char[] password, Cargo cargo) {

}
