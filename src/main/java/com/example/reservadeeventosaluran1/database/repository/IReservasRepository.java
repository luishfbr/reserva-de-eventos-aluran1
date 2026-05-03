package com.example.reservadeeventosaluran1.database.repository;

import com.example.reservadeeventosaluran1.database.model.ReservasEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface IReservasRepository extends JpaRepository<ReservasEntity, UUID> {

    List<ReservasEntity> findAllBySalaId(UUID salaId);

    @Query("""
    SELECT r FROM ReservasEntity r
    WHERE r.sala.id = :salaId
    AND r.status = 'ATIVA'
    AND r.dataInicio < :dataFim
    AND r.dataFim > :dataInicio
    """)
    List<ReservasEntity> findConflitos(
            @Param("salaId") UUID salaId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );

    Page<ReservasEntity> findAllBySalaId(UUID salaId, Pageable pageable);

    @Query("""
    SELECT r FROM ReservasEntity r
    WHERE r.sala.id = :salaId
    AND r.dataInicio < :dataFim
    AND r.dataFim > :dataInicio
    """)
    Page<ReservasEntity> findBySalaIdAndInterval(
            @Param("salaId") UUID salaId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable
    );

    @Query("""
    SELECT r FROM ReservasEntity r
    WHERE r.dataInicio < :dataFim
    AND r.dataFim > :dataInicio
    """)
    Page<ReservasEntity> findByInterval(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            Pageable pageable
    );

}
