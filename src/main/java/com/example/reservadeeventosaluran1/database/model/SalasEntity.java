package com.example.reservadeeventosaluran1.database.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(nullable = false, columnDefinition = "boolean default true")
    @Builder.Default
    private Boolean ativo = true;


    // Lazy para que não seja carregado campos em excesso nas buscas por salas
    @JsonIgnore
    @OneToMany(mappedBy = "sala",fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<ReservasEntity> reservas = new HashSet<>();
}
