package com.example.reservadeeventosaluran1.database.model;

import com.example.reservadeeventosaluran1.database.enums.StatusReserva;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "reservas")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReservasEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private LocalDateTime dataInicio;
    @Column(nullable = false)
    private LocalDateTime dataFim;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatusReserva status = StatusReserva.ATIVA;

    // Legal que traga todos os valores do usuario responsável pela reserva
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private UsuariosEntity usuario;

    // Legal que traga todos os valores da sala que foi feita a reserva
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sala_id")
    private SalasEntity sala;
}
