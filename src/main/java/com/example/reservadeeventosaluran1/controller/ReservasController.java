package com.example.reservadeeventosaluran1.controller;

import com.example.reservadeeventosaluran1.database.model.ReservasEntity;
import com.example.reservadeeventosaluran1.dto.ReservaDto;
import com.example.reservadeeventosaluran1.dto.UpdateReservaDto;
import com.example.reservadeeventosaluran1.exception.BadRequestException;
import com.example.reservadeeventosaluran1.exception.NotFoundException;
import com.example.reservadeeventosaluran1.service.ReservasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/reservas")
@RequiredArgsConstructor
@Validated
public class ReservasController {

    private final ReservasService reservasService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ReservasEntity> findAll() {
        return reservasService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ReservasEntity findById(@PathVariable UUID id) throws NotFoundException {
        return reservasService.findById(id);
    }

    @GetMapping("/sala/{salaId}")
    @ResponseStatus(HttpStatus.OK)
    public List<ReservasEntity> findBySalaId(@PathVariable UUID salaId) throws NotFoundException {
        return reservasService.findBySalaId(salaId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void insert(@Valid @RequestBody ReservaDto reservaDto) throws NotFoundException, BadRequestException {
        reservasService.insert(reservaDto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@Valid @RequestBody UpdateReservaDto reservaDto, @PathVariable UUID id) throws NotFoundException, BadRequestException {
        reservasService.update(reservaDto, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) throws NotFoundException {
        reservasService.delete(id);
    }

    @GetMapping("/paginado")
    @ResponseStatus(HttpStatus.OK)
    public Page<ReservasEntity> findAllPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return reservasService.findAllPaginado(page, size);
    }

    @GetMapping("/sala/{salaId}/paginado")
    @ResponseStatus(HttpStatus.OK)
    public Page<ReservasEntity> findBySalaIdPaginado(
            @PathVariable UUID salaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws NotFoundException {
        return reservasService.findBySalaIdPaginado(salaId, page, size);
    }

    @GetMapping("/sala/{salaId}/intervalo")
    @ResponseStatus(HttpStatus.OK)
    public Page<ReservasEntity> findBySalaIdAndInterval(
            @PathVariable UUID salaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws NotFoundException, BadRequestException {
        return reservasService.findBySalaIdAndInterval(salaId, dataInicio, dataFim, page, size);
    }

    @GetMapping("/intervalo")
    @ResponseStatus(HttpStatus.OK)
    public Page<ReservasEntity> findByInterval(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) throws BadRequestException {
        return reservasService.findByInterval(dataInicio, dataFim, page, size);
    }

}