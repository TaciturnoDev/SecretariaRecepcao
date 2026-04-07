package com.math.taskmanager.service;

import com.math.taskmanager.entity.Sector;
import com.math.taskmanager.exception.ResourceNotFoundException;
import com.math.taskmanager.repository.SectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SectorService {

    private final SectorRepository sectorRepository;

    public Sector findById(Long id) {
        return sectorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Setor não encontrado"));
    }

    //  (necessário pro fluxo)
    public List<Sector> findAll() {
        return sectorRepository.findAll();
    }
}