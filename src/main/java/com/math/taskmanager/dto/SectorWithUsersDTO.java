package com.math.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SectorWithUsersDTO {

    private String name;
    private List<UserDTO> users;
}