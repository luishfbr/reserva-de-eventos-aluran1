package com.example.reservadeeventosaluran1.database.repository;

import com.example.reservadeeventosaluran1.database.model.ReservasEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IReservasRepository extends JpaRepository<ReservasEntity, UUID> {
}
