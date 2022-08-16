package com.polarbookshop.edgeservice.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class User {

    private String username;

    private String firstName;

    private String lastName;

    private List<String> roles;
}
