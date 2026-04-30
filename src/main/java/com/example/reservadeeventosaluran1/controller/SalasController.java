package com.example.reservadeeventosaluran1.controller;

import com.example.reservadeeventosaluran1.database.model.SalasEntity;
import com.example.reservadeeventosaluran1.dto.SalaDto;
import com.example.reservadeeventosaluran1.service.SalasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/salas")
@RequiredArgsConstructor
@Validated
public class SalasController {

    private final SalasService salasService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<SalasEntity> findAll() {
        return salasService.findAll();
    }

    @GetMapping("/{slug}")
    @ResponseStatus(HttpStatus.OK)
    public List<SalasEntity> findAllBySlug(@PathVariable String slug) {
        return salasService.findAllBySlug(slug);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void  insert(@Valid @RequestBody SalaDto salaDto) {
        salasService.insert(salaDto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void  update(@Valid @RequestBody SalaDto salaDto, @PathVariable UUID id) {
        salasService.update(salaDto, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        salasService.delete(id);
    }
}
