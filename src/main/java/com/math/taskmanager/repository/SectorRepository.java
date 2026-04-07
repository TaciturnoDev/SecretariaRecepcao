package com.math.taskmanager.repository;

import com.math.taskmanager.entity.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SectorRepository extends JpaRepository<Sector, Long> {

	/* buscar setores ativos */
    List<Sector> findByActiveTrue();
}