package com.math.taskmanager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "sectors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sector { 
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true; // 🔥 nunca será null

    @JsonIgnore
    @OneToMany(mappedBy = "sector", fetch = FetchType.LAZY)
    private List<User> users;
}