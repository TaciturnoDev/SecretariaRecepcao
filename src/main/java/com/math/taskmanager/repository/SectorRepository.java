package com.math.taskmanager.repository;

import com.math.taskmanager.entity.Sector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectorRepository extends JpaRepository<Sector, Long> {
	
}