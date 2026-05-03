package com.example.reservadeeventosaluran1.database.repository;

import com.example.reservadeeventosaluran1.database.model.UsuariosEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IUsuariosRepository extends JpaRepository<UsuariosEntity, UUID> {
    List<UsuariosEntity> findAllById(UUID id);

    UsuariosEntity findByEmail(String email);

    List<UsuariosEntity> findAllByNomeContainingIgnoreCase(String nome);
}
