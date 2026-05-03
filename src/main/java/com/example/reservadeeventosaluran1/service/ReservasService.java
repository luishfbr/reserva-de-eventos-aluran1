package com.example.reservadeeventosaluran1.service;

import com.example.reservadeeventosaluran1.database.model.ReservasEntity;
import com.example.reservadeeventosaluran1.database.model.SalasEntity;
import com.example.reservadeeventosaluran1.database.model.UsuariosEntity;
import com.example.reservadeeventosaluran1.database.repository.IReservasRepository;
import com.example.reservadeeventosaluran1.database.repository.ISalasRepository;
import com.example.reservadeeventosaluran1.database.repository.IUsuariosRepository;
import com.example.reservadeeventosaluran1.database.enums.StatusReserva;
import com.example.reservadeeventosaluran1.dto.ReservaDto;
import com.example.reservadeeventosaluran1.dto.UpdateReservaDto;
import com.example.reservadeeventosaluran1.exception.BadRequestException;
import com.example.reservadeeventosaluran1.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservasService {

    private final IReservasRepository reservasRepository;
    private final ISalasRepository salasRepository;
    private final IUsuariosRepository usuariosRepository;

    public List<ReservasEntity> findAll() {
        return reservasRepository.findAll();
    }

    public List<ReservasEntity> findBySalaId(UUID salaId) throws NotFoundException {
        salasRepository.findById(salaId)
                .orElseThrow(() -> new NotFoundException("Sala não encontrada."));

        return reservasRepository.findAllBySalaId(salaId);
    }

    public ReservasEntity findById(UUID id) throws NotFoundException {
        return reservasRepository.findById(id).orElseThrow(() -> new NotFoundException("Reserva não encontrada."));
    }

    // SERIALIZABLE garante que findConflitos + save sejam atômicos: sem isso duas requisições simultâneas passam na checagem e gravam o double-booking.
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void insert(ReservaDto reservaDto) throws NotFoundException, BadRequestException {

        UsuariosEntity usuario = usuariosRepository.findById(reservaDto.getUsuario_id())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

        SalasEntity sala = salasRepository.findById(reservaDto.getSala_id())
                .orElseThrow(() -> new NotFoundException("Sala não encontrada."));

        if (!sala.getAtivo()) {
            throw new BadRequestException("A sala informada não se encontra ativa.");
        }

        if (reservaDto.getDataFim().isBefore(reservaDto.getDataInicio())) {
            throw new BadRequestException("Data de fim não pode ser antes da data de inicio.");
        }

        List<ReservasEntity> conflitos = reservasRepository.findConflitos(
                sala.getId(),
                reservaDto.getDataInicio(),
                reservaDto.getDataFim()
        );

        if (!conflitos.isEmpty()) {
            throw new BadRequestException("Já existe uma reserva ativa nesse horário para a sala informada.");
        }

        reservasRepository.save(
                ReservasEntity.builder()
                        .dataInicio(reservaDto.getDataInicio())
                        .dataFim(reservaDto.getDataFim())
                        .usuario(usuario)
                        .sala(sala)
                        .status(reservaDto.getStatus() != null ? reservaDto.getStatus() : StatusReserva.ATIVA)
                        .build()
        );

    }

    // Mesma razão do insert: leitura de conflito + save devem ser atômicos contra modificações concorrentes.
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void update(UpdateReservaDto reservaDto, UUID id) throws NotFoundException, BadRequestException {
        ReservasEntity reserva = reservasRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reserva não encontrada"));

        if (reservaDto.getStatus() == StatusReserva.CANCELADA
                && reserva.getStatus() == StatusReserva.CANCELADA) {
            throw new BadRequestException("Falha ao atualizar a reserva, a mesma se encontra CANCELADA.");
        }

        if (reservaDto.getUsuario_id() != null) {
            UsuariosEntity usuario = usuariosRepository.findById(reservaDto.getUsuario_id())
                    .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
            reserva.setUsuario(usuario);
        }

        if (reservaDto.getSala_id() != null) {
            SalasEntity sala = salasRepository.findById(reservaDto.getSala_id())
                    .orElseThrow(() -> new NotFoundException("Sala não encontrada."));
            if (!sala.getAtivo()) {
                throw new BadRequestException("A sala informada não se encontra ativa.");
            }
            reserva.setSala(sala);
        }

        LocalDateTime effectiveInicio = reservaDto.getDataInicio() != null ? reservaDto.getDataInicio() : reserva.getDataInicio();
        LocalDateTime effectiveFim    = reservaDto.getDataFim()    != null ? reservaDto.getDataFim()    : reserva.getDataFim();

        if (!effectiveFim.isAfter(effectiveInicio)) {
            throw new BadRequestException("Data de fim não pode ser antes da data de inicio.");
        }

        if (reservaDto.getDataInicio() != null) reserva.setDataInicio(reservaDto.getDataInicio());
        if (reservaDto.getDataFim()    != null) reserva.setDataFim(reservaDto.getDataFim());

        List<ReservasEntity> conflitos = reservasRepository.findConflitos(
                reserva.getSala().getId(),
                effectiveInicio,
                effectiveFim
        ).stream().filter(c -> !c.getId().equals(id)).toList();

        if (!conflitos.isEmpty()) {
            throw new BadRequestException("Já existe uma reserva ativa nesse horário para a sala informada.");
        }

        if (reservaDto.getStatus() != null) {
            reserva.setStatus(reservaDto.getStatus());
        }

        reservasRepository.save(reserva);
    }

    @Transactional
    public void delete(UUID id) throws NotFoundException {
        reservasRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reserva não encontrada."));

        reservasRepository.deleteById(id);
    }

    public Page<ReservasEntity> findAllPaginado(int page, int size) {
        return reservasRepository.findAll(PageRequest.of(page, size));
    }

    public Page<ReservasEntity> findBySalaIdPaginado(UUID salaId, int page, int size)
            throws NotFoundException {
        salasRepository.findById(salaId)
                .orElseThrow(() -> new NotFoundException("Sala não encontrada."));
        return reservasRepository.findAllBySalaId(salaId, PageRequest.of(page, size));
    }

    public Page<ReservasEntity> findBySalaIdAndInterval(UUID salaId,
            LocalDateTime dataInicio, LocalDateTime dataFim, int page, int size)
            throws NotFoundException, BadRequestException {
        salasRepository.findById(salaId)
                .orElseThrow(() -> new NotFoundException("Sala não encontrada."));
        if (!dataFim.isAfter(dataInicio))
            throw new BadRequestException("Data de fim não pode ser antes da data de inicio.");
        return reservasRepository.findBySalaIdAndInterval(salaId, dataInicio, dataFim, PageRequest.of(page, size));
    }

    public Page<ReservasEntity> findByInterval(LocalDateTime dataInicio,
            LocalDateTime dataFim, int page, int size) throws BadRequestException {
        if (!dataFim.isAfter(dataInicio))
            throw new BadRequestException("Data de fim não pode ser antes da data de inicio.");
        return reservasRepository.findByInterval(dataInicio, dataFim, PageRequest.of(page, size));
    }

}
