package com.example.reservadeeventosaluran1.controller;

import com.example.reservadeeventosaluran1.database.model.UsuariosEntity;
import com.example.reservadeeventosaluran1.dto.UsuarioDto;
import com.example.reservadeeventosaluran1.service.UsuariosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/usuarios")
@RequiredArgsConstructor
@Validated
public class UsuariosController {

    private final UsuariosService usuariosService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UsuariosEntity> findAll() {
        return usuariosService.findAll();
    }

    @GetMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    public List<UsuariosEntity> findAllByEmail(@PathVariable String email) {
        return usuariosService.findAllByEmail(email);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void  insert(@Valid @RequestBody UsuarioDto usuarioDto) {
        usuariosService.insert(usuarioDto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void  update(@Valid @RequestBody UsuarioDto usuarioDto, @PathVariable UUID id) {
        usuariosService.update(usuarioDto, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        usuariosService.delete(id);
    }
}
