package com.example.reservadeeventosaluran1.database.model;

import jakarta.persistence.*;
import lombok.*;

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
    private Date dataInicio;
    @Column(nullable = false)
    private Date dataFim;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UsuariosEntity usuario;

    @ManyToOne
    @JoinColumn(name = "sala_id")
    private SalasEntity sala;
}
