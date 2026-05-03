package com.example.reservadeeventosaluran1.service;

import com.example.reservadeeventosaluran1.database.model.UsuariosEntity;
import com.example.reservadeeventosaluran1.database.repository.IUsuariosRepository;
import com.example.reservadeeventosaluran1.dto.UsuarioDto;
import com.example.reservadeeventosaluran1.exception.BadRequestException;
import com.example.reservadeeventosaluran1.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.NotFound;
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

    public UsuariosEntity findByEmail(String email) {
        return usuariosRepository.findByEmail(email);
    }

    public void insert(UsuarioDto usuarioDto) throws BadRequestException {
        UsuariosEntity usuario = usuariosRepository.findByEmail(usuarioDto.getEmail());

        if (usuario != null) {
            throw new BadRequestException("Usuario já registrado.");
        }

        usuariosRepository.save(UsuariosEntity.builder()
                        .nome(usuarioDto.getNome())
                        .email(usuarioDto.getEmail())
                .build());
    }

    public void update(UsuarioDto usuarioDto, UUID id) throws NotFoundException {
        UsuariosEntity usuario = usuariosRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

        usuario.setNome(usuarioDto.getNome());
        usuario.setEmail(usuarioDto.getEmail());

        usuariosRepository.save(usuario);
    }

    public void delete(UUID id) throws NotFoundException {
        UsuariosEntity usuarios = usuariosRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

        usuariosRepository.deleteById(id);
    }

}
