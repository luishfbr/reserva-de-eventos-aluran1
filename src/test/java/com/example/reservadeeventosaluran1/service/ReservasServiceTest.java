package com.example.reservadeeventosaluran1.service;

import com.example.reservadeeventosaluran1.database.enums.StatusReserva;
import com.example.reservadeeventosaluran1.database.model.ReservasEntity;
import com.example.reservadeeventosaluran1.database.model.SalasEntity;
import com.example.reservadeeventosaluran1.database.model.UsuariosEntity;
import com.example.reservadeeventosaluran1.database.repository.IReservasRepository;
import com.example.reservadeeventosaluran1.database.repository.ISalasRepository;
import com.example.reservadeeventosaluran1.database.repository.IUsuariosRepository;
import com.example.reservadeeventosaluran1.dto.ReservaDto;
import com.example.reservadeeventosaluran1.dto.UpdateReservaDto;
import com.example.reservadeeventosaluran1.exception.BadRequestException;
import com.example.reservadeeventosaluran1.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservasServiceTest {

    @Mock IReservasRepository reservasRepository;
    @Mock ISalasRepository    salasRepository;
    @Mock IUsuariosRepository usuariosRepository;

    @InjectMocks ReservasService service;

    private final UUID RESERVA_ID = UUID.randomUUID();
    private final UUID SALA_ID    = UUID.randomUUID();
    private final UUID USUARIO_ID = UUID.randomUUID();

    // T0 < T1 < T2 < T3 para montar intervalos distintos e adjacentes
    private final LocalDateTime T0 = LocalDateTime.of(2026, 6, 1,  8, 0);
    private final LocalDateTime T1 = LocalDateTime.of(2026, 6, 1, 10, 0);
    private final LocalDateTime T2 = LocalDateTime.of(2026, 6, 1, 12, 0);
    private final LocalDateTime T3 = LocalDateTime.of(2026, 6, 1, 14, 0);

    private UsuariosEntity usuario;
    private SalasEntity    salaAtiva;
    private SalasEntity    salaInativa;
    private ReservasEntity reservaAtiva;
    private ReservasEntity reservaCancelada;

    @BeforeEach
    void setUp() {
        usuario = UsuariosEntity.builder()
                .id(USUARIO_ID).nome("Ana").email("ana@x.com").build();

        salaAtiva = SalasEntity.builder()
                .id(SALA_ID).nome("Sala A").slug("sala-a").capacidade(10).ativo(true).build();

        salaInativa = SalasEntity.builder()
                .id(SALA_ID).nome("Sala A").slug("sala-a").capacidade(10).ativo(false).build();

        reservaAtiva = ReservasEntity.builder()
                .id(RESERVA_ID).sala(salaAtiva).usuario(usuario)
                .dataInicio(T1).dataFim(T2).status(StatusReserva.ATIVA).build();

        reservaCancelada = ReservasEntity.builder()
                .id(RESERVA_ID).sala(salaAtiva).usuario(usuario)
                .dataInicio(T1).dataFim(T2).status(StatusReserva.CANCELADA).build();
    }

    // ─── A: insert() ────────────────────────────────────────────────────────

    @Test
    void insert_deveSalvar_quandoDadosValidos() {
        var dto = ReservaDto.builder()
                .usuario_id(USUARIO_ID).sala_id(SALA_ID).dataInicio(T1).dataFim(T2).build();

        when(usuariosRepository.findById(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(salasRepository.findById(SALA_ID)).thenReturn(Optional.of(salaAtiva));
        when(reservasRepository.findConflitos(SALA_ID, T1, T2)).thenReturn(List.of());

        assertThatNoException().isThrownBy(() -> service.insert(dto));
        verify(reservasRepository).save(argThat(e -> e.getStatus() == StatusReserva.ATIVA));
    }

    @Test
    void insert_deveSalvarComStatusExplicito_quandoStatusFornecido() {
        var dto = ReservaDto.builder()
                .usuario_id(USUARIO_ID).sala_id(SALA_ID)
                .dataInicio(T1).dataFim(T2).status(StatusReserva.CANCELADA).build();

        when(usuariosRepository.findById(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(salasRepository.findById(SALA_ID)).thenReturn(Optional.of(salaAtiva));
        when(reservasRepository.findConflitos(SALA_ID, T1, T2)).thenReturn(List.of());

        assertThatNoException().isThrownBy(() -> service.insert(dto));
        verify(reservasRepository).save(argThat(e -> e.getStatus() == StatusReserva.CANCELADA));
    }

    @Test
    void insert_deveLancarNotFoundException_quandoUsuarioNaoEncontrado() {
        var dto = ReservaDto.builder()
                .usuario_id(USUARIO_ID).sala_id(SALA_ID).dataInicio(T1).dataFim(T2).build();

        when(usuariosRepository.findById(USUARIO_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.insert(dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Usuário não encontrado.");
        verifyNoInteractions(salasRepository, reservasRepository);
    }

    @Test
    void insert_deveLancarNotFoundException_quandoSalaNaoEncontrada() {
        var dto = ReservaDto.builder()
                .usuario_id(USUARIO_ID).sala_id(SALA_ID).dataInicio(T1).dataFim(T2).build();

        when(usuariosRepository.findById(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(salasRepository.findById(SALA_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.insert(dto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Sala não encontrada.");
        verify(reservasRepository, never()).save(any());
    }

    @Test
    void insert_deveLancarBadRequestException_quandoSalaInativa() {
        var dto = ReservaDto.builder()
                .usuario_id(USUARIO_ID).sala_id(SALA_ID).dataInicio(T1).dataFim(T2).build();

        when(usuariosRepository.findById(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(salasRepository.findById(SALA_ID)).thenReturn(Optional.of(salaInativa));

        assertThatThrownBy(() -> service.insert(dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("A sala informada não se encontra ativa.");
    }

    @Test
    void insert_deveLancarBadRequestException_quandoDataFimAntesDeDataInicio() {
        // dataFim = T1, dataInicio = T2 → T1.isBefore(T2) = true → exception
        var dto = ReservaDto.builder()
                .usuario_id(USUARIO_ID).sala_id(SALA_ID).dataInicio(T2).dataFim(T1).build();

        when(usuariosRepository.findById(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(salasRepository.findById(SALA_ID)).thenReturn(Optional.of(salaAtiva));

        assertThatThrownBy(() -> service.insert(dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Data de fim não pode ser antes da data de inicio.");
        verify(reservasRepository, never()).findConflitos(any(), any(), any());
    }

    @Test
    void insert_naoDeveLancarExcecao_quandoDataFimIgualADataInicio() {
        // isBefore(T1, T1) = false → guard não dispara; findConflitos é chamado normalmente
        var dto = ReservaDto.builder()
                .usuario_id(USUARIO_ID).sala_id(SALA_ID).dataInicio(T1).dataFim(T1).build();

        when(usuariosRepository.findById(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(salasRepository.findById(SALA_ID)).thenReturn(Optional.of(salaAtiva));
        when(reservasRepository.findConflitos(SALA_ID, T1, T1)).thenReturn(List.of());

        assertThatNoException().isThrownBy(() -> service.insert(dto));
        verify(reservasRepository).save(any());
    }

    @Test
    void insert_deveLancarBadRequestException_quandoConflitoExiste() {
        var dto = ReservaDto.builder()
                .usuario_id(USUARIO_ID).sala_id(SALA_ID).dataInicio(T1).dataFim(T2).build();

        when(usuariosRepository.findById(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(salasRepository.findById(SALA_ID)).thenReturn(Optional.of(salaAtiva));
        when(reservasRepository.findConflitos(SALA_ID, T1, T2)).thenReturn(List.of(reservaAtiva));

        assertThatThrownBy(() -> service.insert(dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Já existe uma reserva ativa nesse horário para a sala informada.");
        verify(reservasRepository, never()).save(any());
    }

    @Test
    void insert_naoDeveConflitar_quandoReservaAdjacenteTerminaNoInicioDaNova() {
        // Reserva existente: [T1, T2]; nova: [T2, T3] — fronteira exata não é conflito
        // A JPQL exige dataFim > dataInicio, então T2 > T2 = false → sem conflito
        var dto = ReservaDto.builder()
                .usuario_id(USUARIO_ID).sala_id(SALA_ID).dataInicio(T2).dataFim(T3).build();

        when(usuariosRepository.findById(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(salasRepository.findById(SALA_ID)).thenReturn(Optional.of(salaAtiva));
        when(reservasRepository.findConflitos(SALA_ID, T2, T3)).thenReturn(List.of());

        assertThatNoException().isThrownBy(() -> service.insert(dto));
        verify(reservasRepository).save(any());
    }

    @Test
    void insert_naoDeveConflitar_quandoNovaReservaTerminaNoInicioDeOutra() {
        // Nova: [T0, T1]; reserva existente começa em T1 — fronteira exata não é conflito
        var dto = ReservaDto.builder()
                .usuario_id(USUARIO_ID).sala_id(SALA_ID).dataInicio(T0).dataFim(T1).build();

        when(usuariosRepository.findById(USUARIO_ID)).thenReturn(Optional.of(usuario));
        when(salasRepository.findById(SALA_ID)).thenReturn(Optional.of(salaAtiva));
        when(reservasRepository.findConflitos(SALA_ID, T0, T1)).thenReturn(List.of());

        assertThatNoException().isThrownBy(() -> service.insert(dto));
        verify(reservasRepository).save(any());
    }

    // ─── B: update() ────────────────────────────────────────────────────────

    @Test
    void update_deveSalvar_quandoStatusNuloEReservaAtiva() {
        // Cenário: caller quer alterar só dataFim, sem mudar status
        var dto = UpdateReservaDto.builder().dataFim(T3).build();

        when(reservasRepository.findById(RESERVA_ID)).thenReturn(Optional.of(reservaAtiva));
        when(reservasRepository.findConflitos(SALA_ID, T1, T3)).thenReturn(List.of());

        assertThatNoException().isThrownBy(() -> service.update(dto, RESERVA_ID));
        verify(reservasRepository).save(any());
    }

    @Test
    void update_deveLancarBadRequestException_quandoReservaJaCanceladaEStatusTambemCancelada() {
        var dto = UpdateReservaDto.builder().status(StatusReserva.CANCELADA).build();

        when(reservasRepository.findById(RESERVA_ID)).thenReturn(Optional.of(reservaCancelada));

        assertThatThrownBy(() -> service.update(dto, RESERVA_ID))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Falha ao atualizar a reserva, a mesma se encontra CANCELADA.");
    }

    @Test
    void update_devePermitirCancelamento_quandoReservaAtiva() {
        // status=CANCELADA no dto, reserva está ATIVA → guard não dispara → cancela com sucesso
        var dto = UpdateReservaDto.builder().status(StatusReserva.CANCELADA).build();

        when(reservasRepository.findById(RESERVA_ID)).thenReturn(Optional.of(reservaAtiva));
        when(reservasRepository.findConflitos(eq(SALA_ID), any(), any())).thenReturn(List.of());

        assertThatNoException().isThrownBy(() -> service.update(dto, RESERVA_ID));
        verify(reservasRepository).save(argThat(e -> e.getStatus() == StatusReserva.CANCELADA));
    }

    @Test
    void update_deveLancarNotFoundException_quandoReservaNaoEncontrada() {
        var dto = UpdateReservaDto.builder().status(StatusReserva.ATIVA).build();

        when(reservasRepository.findById(RESERVA_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(dto, RESERVA_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Reserva não encontrada");
    }

    @Test
    void update_deveLancarNotFoundException_quandoNovoUsuarioNaoEncontrado() {
        UUID outroUsuarioId = UUID.randomUUID();
        var dto = UpdateReservaDto.builder()
                .usuario_id(outroUsuarioId).status(StatusReserva.ATIVA).build();

        when(reservasRepository.findById(RESERVA_ID)).thenReturn(Optional.of(reservaAtiva));
        when(usuariosRepository.findById(outroUsuarioId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(dto, RESERVA_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Usuário não encontrado.");
    }

    @Test
    void update_deveLancarNotFoundException_quandoNovaSalaNaoEncontrada() {
        UUID outraSalaId = UUID.randomUUID();
        var dto = UpdateReservaDto.builder()
                .sala_id(outraSalaId).status(StatusReserva.ATIVA).build();

        when(reservasRepository.findById(RESERVA_ID)).thenReturn(Optional.of(reservaAtiva));
        when(salasRepository.findById(outraSalaId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(dto, RESERVA_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Sala não encontrada.");
    }

    @Test
    void update_deveLancarBadRequestException_quandoNovaSalaInativa() {
        UUID outraSalaId = UUID.randomUUID();
        SalasEntity salaInativaOutra = SalasEntity.builder()
                .id(outraSalaId).nome("Sala B").slug("sala-b").capacidade(5).ativo(false).build();
        var dto = UpdateReservaDto.builder()
                .sala_id(outraSalaId).status(StatusReserva.ATIVA).build();

        when(reservasRepository.findById(RESERVA_ID)).thenReturn(Optional.of(reservaAtiva));
        when(salasRepository.findById(outraSalaId)).thenReturn(Optional.of(salaInativaOutra));

        assertThatThrownBy(() -> service.update(dto, RESERVA_ID))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("A sala informada não se encontra ativa.");
    }

    @Test
    void update_deveLancarBadRequestException_quandoEffectiveDataFimNaoDepoisDeDataInicio() {
        // Reserva existente: dataInicio=T1, dataFim=T2. dto override dataFim=T1 → effectiveFim=T1=effectiveInicio → inválido
        var dto = UpdateReservaDto.builder().dataFim(T1).build();

        when(reservasRepository.findById(RESERVA_ID)).thenReturn(Optional.of(reservaAtiva));

        assertThatThrownBy(() -> service.update(dto, RESERVA_ID))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Data de fim não pode ser antes da data de inicio.");
    }

    @Test
    void update_deveLancarBadRequestException_quandoConflitoExisteComOutraReserva() {
        UUID outroId = UUID.randomUUID();
        ReservasEntity outraReserva = ReservasEntity.builder()
                .id(outroId).sala(salaAtiva).usuario(usuario)
                .dataInicio(T1).dataFim(T2).status(StatusReserva.ATIVA).build();

        var dto = UpdateReservaDto.builder().status(StatusReserva.ATIVA).build();

        when(reservasRepository.findById(RESERVA_ID)).thenReturn(Optional.of(reservaAtiva));
        // findConflitos retorna a própria reserva + outra → após filtrar o próprio ID, resta 1 → conflito
        when(reservasRepository.findConflitos(eq(SALA_ID), any(), any()))
                .thenReturn(List.of(reservaAtiva, outraReserva));

        assertThatThrownBy(() -> service.update(dto, RESERVA_ID))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Já existe uma reserva ativa nesse horário para a sala informada.");
    }

    @Test
    void update_naoDeveLancarConflito_quandoUnicoConflitoEAPropriaReserva() {
        // findConflitos devolve apenas a própria reserva → filtro remove → lista vazia → sem conflito
        var dto = UpdateReservaDto.builder().status(StatusReserva.ATIVA).build();

        when(reservasRepository.findById(RESERVA_ID)).thenReturn(Optional.of(reservaAtiva));
        when(reservasRepository.findConflitos(eq(SALA_ID), any(), any()))
                .thenReturn(List.of(reservaAtiva));

        assertThatNoException().isThrownBy(() -> service.update(dto, RESERVA_ID));
        verify(reservasRepository).save(any());
    }

    @Test
    void update_deveUsarEffectiveDates_quandoSomenteDataInicioFornecida() {
        // Só dataInicio muda: effectiveInicio=T0, effectiveFim continua T2 da reserva existente
        var dto = UpdateReservaDto.builder().dataInicio(T0).status(StatusReserva.ATIVA).build();

        when(reservasRepository.findById(RESERVA_ID)).thenReturn(Optional.of(reservaAtiva));
        when(reservasRepository.findConflitos(SALA_ID, T0, T2)).thenReturn(List.of());

        assertThatNoException().isThrownBy(() -> service.update(dto, RESERVA_ID));
        verify(reservasRepository).findConflitos(SALA_ID, T0, T2);
    }

    // ─── C: delete() ────────────────────────────────────────────────────────

    @Test
    void delete_deveDeletar_quandoReservaExiste() {
        when(reservasRepository.findById(RESERVA_ID)).thenReturn(Optional.of(reservaAtiva));

        assertThatNoException().isThrownBy(() -> service.delete(RESERVA_ID));
        verify(reservasRepository).deleteById(RESERVA_ID);
    }

    @Test
    void delete_deveLancarNotFoundException_quandoReservaNaoExiste() {
        when(reservasRepository.findById(RESERVA_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(RESERVA_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Reserva não encontrada.");
        verify(reservasRepository, never()).deleteById(any());
    }

    // ─── D: leituras / queries ───────────────────────────────────────────────

    @Test
    void findById_deveRetornarReserva_quandoEncontrada() throws Exception {
        when(reservasRepository.findById(RESERVA_ID)).thenReturn(Optional.of(reservaAtiva));

        assertThat(service.findById(RESERVA_ID)).isEqualTo(reservaAtiva);
    }

    @Test
    void findById_deveLancarNotFoundException_quandoNaoEncontrada() {
        when(reservasRepository.findById(RESERVA_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(RESERVA_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Reserva não encontrada.");
    }

    @Test
    void findBySalaId_deveRetornarLista_quandoSalaExiste() throws Exception {
        when(salasRepository.findById(SALA_ID)).thenReturn(Optional.of(salaAtiva));
        when(reservasRepository.findAllBySalaId(SALA_ID))
                .thenReturn(List.of(reservaAtiva, reservaCancelada));

        assertThat(service.findBySalaId(SALA_ID)).hasSize(2);
    }

    @Test
    void findBySalaId_deveLancarNotFoundException_quandoSalaNaoExiste() {
        when(salasRepository.findById(SALA_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findBySalaId(SALA_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Sala não encontrada.");
    }

    @Test
    void findBySalaIdAndInterval_deveLancarBadRequestException_quandoIntervaloInvalido() {
        when(salasRepository.findById(SALA_ID)).thenReturn(Optional.of(salaAtiva));

        // dataFim=T1 não é depois de dataInicio=T2
        assertThatThrownBy(() -> service.findBySalaIdAndInterval(SALA_ID, T2, T1, 0, 10))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Data de fim não pode ser antes da data de inicio.");
    }

    @Test
    void findBySalaIdAndInterval_deveLancarNotFoundException_quandoSalaNaoExiste() {
        when(salasRepository.findById(SALA_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findBySalaIdAndInterval(SALA_ID, T1, T2, 0, 10))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Sala não encontrada.");
    }

    @Test
    @SuppressWarnings("unchecked")
    void findBySalaIdAndInterval_deveRetornarPagina_quandoValido() throws Exception {
        Page<ReservasEntity> mockPage = mock(Page.class);
        when(salasRepository.findById(SALA_ID)).thenReturn(Optional.of(salaAtiva));
        when(reservasRepository.findBySalaIdAndInterval(eq(SALA_ID), eq(T1), eq(T2), any()))
                .thenReturn(mockPage);

        assertThat(service.findBySalaIdAndInterval(SALA_ID, T1, T2, 0, 10)).isEqualTo(mockPage);
    }

    @Test
    void findByInterval_deveLancarBadRequestException_quandoIntervaloInvalido() {
        // dataFim=T1 não é depois de dataInicio=T2
        assertThatThrownBy(() -> service.findByInterval(T2, T1, 0, 10))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Data de fim não pode ser antes da data de inicio.");
    }

    @Test
    @SuppressWarnings("unchecked")
    void findByInterval_deveRetornarPagina_quandoIntervaloValido() throws Exception {
        Page<ReservasEntity> mockPage = mock(Page.class);
        when(reservasRepository.findByInterval(eq(T1), eq(T2), any())).thenReturn(mockPage);

        assertThat(service.findByInterval(T1, T2, 0, 10)).isEqualTo(mockPage);
    }
}
