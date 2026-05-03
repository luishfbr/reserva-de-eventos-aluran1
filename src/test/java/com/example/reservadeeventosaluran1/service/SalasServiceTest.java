package com.example.reservadeeventosaluran1.service;

import com.example.reservadeeventosaluran1.database.model.SalasEntity;
import com.example.reservadeeventosaluran1.database.repository.ISalasRepository;
import com.example.reservadeeventosaluran1.dto.SalaDto;
import com.example.reservadeeventosaluran1.exception.BadRequestException;
import com.example.reservadeeventosaluran1.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalasServiceTest {

    @Mock ISalasRepository salasRepository;
    @InjectMocks SalasService service;

    private final UUID SALA_ID = UUID.randomUUID();

    private SalaDto salaDto;
    private SalasEntity salaEntity;

    @BeforeEach
    void setUp() {
        salaDto = SalaDto.builder()
                .nome("Sala de Reunião").capacidade(20).ativo(true).build();

        salaEntity = SalasEntity.builder()
                .id(SALA_ID).nome("Sala de Reunião").slug("sala-de-reuniao")
                .capacidade(20).ativo(true).build();
    }

    // ─── E: insert() ────────────────────────────────────────────────────────

    @Test
    void insert_deveSalvar_quandoSlugNaoExiste() {
        when(salasRepository.findBySlug("sala-de-reuniao")).thenReturn(null);

        assertThatNoException().isThrownBy(() -> service.insert(salaDto));
        verify(salasRepository).save(argThat(e ->
                e.getSlug().equals("sala-de-reuniao") && e.getNome().equals("Sala de Reunião")));
    }

    @Test
    void insert_deveLancarBadRequestException_quandoSlugJaExiste() {
        when(salasRepository.findBySlug("sala-de-reuniao")).thenReturn(salaEntity);

        assertThatThrownBy(() -> service.insert(salaDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Sala já cadastrada.");
        verify(salasRepository, never()).save(any());
    }

    @Test
    void insert_deveGerarSlugCorreto_quandoNomePossuiAcentos() {
        var dto = SalaDto.builder().nome("Auditório Principal").capacidade(50).ativo(true).build();
        when(salasRepository.findBySlug("auditorio-principal")).thenReturn(null);

        assertThatNoException().isThrownBy(() -> service.insert(dto));
        verify(salasRepository).save(argThat(e -> e.getSlug().equals("auditorio-principal")));
    }

    @Test
    void insert_deveGerarSlugCorreto_quandoNomePossuiEspacosMultiplos() {
        var dto = SalaDto.builder().nome("Sala   de   Eventos").capacidade(30).ativo(true).build();
        when(salasRepository.findBySlug("sala-de-eventos")).thenReturn(null);

        assertThatNoException().isThrownBy(() -> service.insert(dto));
        verify(salasRepository).save(argThat(e -> e.getSlug().equals("sala-de-eventos")));
    }

    // ─── F: update() ────────────────────────────────────────────────────────

    @Test
    void update_deveSalvar_quandoSalaEncontrada() {
        when(salasRepository.findById(SALA_ID)).thenReturn(Optional.of(salaEntity));

        assertThatNoException().isThrownBy(() -> service.update(salaDto, SALA_ID));
        verify(salasRepository).save(argThat(e ->
                e.getNome().equals("Sala de Reunião") && e.getCapacidade().equals(20)));
    }

    @Test
    void update_deveLancarNotFoundException_quandoSalaNaoEncontrada() {
        when(salasRepository.findById(SALA_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(salaDto, SALA_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Sala não encontrada.");
        verify(salasRepository, never()).save(any());
    }

    @Test
    void update_deveUsarSlugDerividoDoNomeExistente_naoDoDto() {
        // update() chama SlugUtil.gerarSlug(sala.getNome()) com o nome da entidade existente,
        // não com salaDto.getNome(). O slug não reflete o novo nome — comportamento atual documentado.
        SalasEntity entidadeComNomeAntigo = SalasEntity.builder()
                .id(SALA_ID).nome("Sala A").slug("sala-a").capacidade(10).ativo(true).build();
        SalaDto dtoComNovoNome = SalaDto.builder()
                .nome("Sala B").capacidade(10).ativo(true).build();

        when(salasRepository.findById(SALA_ID)).thenReturn(Optional.of(entidadeComNomeAntigo));

        assertThatNoException().isThrownBy(() -> service.update(dtoComNovoNome, SALA_ID));
        verify(salasRepository).save(argThat(e ->
                e.getNome().equals("Sala B") && e.getSlug().equals("sala-a")));
    }

    // ─── G: delete() ────────────────────────────────────────────────────────

    @Test
    void delete_deveDeletar_quandoSalaEncontrada() {
        when(salasRepository.findById(SALA_ID)).thenReturn(Optional.of(salaEntity));

        assertThatNoException().isThrownBy(() -> service.delete(SALA_ID));
        verify(salasRepository).deleteById(SALA_ID);
    }

    @Test
    void delete_deveLancarNotFoundException_quandoSalaNaoEncontrada() {
        when(salasRepository.findById(SALA_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(SALA_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Sala não encontrada.");
        verify(salasRepository, never()).deleteById(any());
    }
}
