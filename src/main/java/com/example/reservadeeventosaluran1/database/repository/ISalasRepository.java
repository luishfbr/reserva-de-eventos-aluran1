package com.example.reservadeeventosaluran1.database.repository;

import com.example.reservadeeventosaluran1.database.model.SalasEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ISalasRepository extends JpaRepository<SalasEntity, UUID> {

    List<SalasEntity> findAllBySlug(String slug);
}
