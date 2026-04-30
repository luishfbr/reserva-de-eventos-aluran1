package com.example.reservadeeventosaluran1.database.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "salas")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SalasEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, unique = true)
    private String nome;
    @Column(nullable = false, unique = true)
    private String slug;
    @Column(nullable = false)
    private Integer capacidade;

    @OneToMany(mappedBy = "sala")
    private Set<ReservasEntity> reservas = new HashSet<>();
}
