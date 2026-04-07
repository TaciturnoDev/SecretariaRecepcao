package com.math.taskmanager.controller;

import com.math.taskmanager.entity.Sector;
import com.math.taskmanager.repository.SectorRepository;
import com.math.taskmanager.dto.SectorWithUsersDTO;
import com.math.taskmanager.dto.UserDTO;
import com.math.taskmanager.dto.SectorDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sectors")
@RequiredArgsConstructor
public class SectorController {

    private final SectorRepository sectorRepository;

    // 🔹 LISTAR SETORES SIMPLES (CORRIGIDO)
    @GetMapping
    public List<SectorDTO> listar() {
        return sectorRepository.findAll()
                .stream()
                .map(sector -> new SectorDTO(sector.getId(), sector.getName()))
                .toList();
    }

    // 🔹 LISTAR SETORES ATIVOS (USADO NO LOGIN)
    @GetMapping("/active")
    public List<SectorDTO> listarAtivos() {
        return sectorRepository.findByActiveTrue()
                .stream()
                .map(sector -> new SectorDTO(sector.getId(), sector.getName()))
                .toList();
    }

    // 🔹 CRIAR SETOR
    @PostMapping
    public Sector criar(@RequestBody Sector sector) {
        return sectorRepository.save(sector);
    }

    // 🔥 LISTAR SETORES COM USUÁRIOS (USADO NA SIDEBAR)
    @GetMapping("/with-users")
    public List<SectorWithUsersDTO> listarComUsuarios() {

        List<Sector> sectors = sectorRepository.findAll();

        return sectors.stream().map(sector -> {

            List<UserDTO> users = sector.getUsers()
                    .stream()
                    .map(user -> new UserDTO(user.getId(), user.getName()))
                    .toList();

            return new SectorWithUsersDTO(sector.getName(), users);

        }).toList();
    }
}