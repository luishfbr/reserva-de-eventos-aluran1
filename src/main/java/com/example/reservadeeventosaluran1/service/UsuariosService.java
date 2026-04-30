package com.example.reservadeeventosaluran1.service;

import com.example.reservadeeventosaluran1.database.model.UsuariosEntity;
import com.example.reservadeeventosaluran1.database.repository.IUsuariosRepository;
import com.example.reservadeeventosaluran1.dto.UsuarioDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuariosService {

    private final IUsuariosRepository usuariosRepository;

    public List<UsuariosEntity> findAll() {
        return usuariosRepository.findAll();
    }

    public List<UsuariosEntity> findAllByEmail(String email) {
        return usuariosRepository.findAllByEmail(email);
    }

    public void insert(UsuarioDto usuarioDto) {
        usuariosRepository.save(UsuariosEntity.builder()
                        .nome(usuarioDto.getNome())
                        .email(usuarioDto.getEmail())
                .build());
    }

    public void update(UsuarioDto usuarioDto, UUID id) {
        UsuariosEntity usuario = usuariosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        usuario.setNome(usuarioDto.getNome());
        usuario.setEmail(usuarioDto.getEmail());

        usuariosRepository.save(usuario);
    }

    public void delete(UUID id) {
        usuariosRepository.deleteById(id);
    }

}
