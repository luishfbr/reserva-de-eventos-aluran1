package com.example.reservadeeventosaluran1.dto;

import com.example.reservadeeventosaluran1.database.enums.StatusReserva;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservaDto {
    @NotNull
    private UUID usuario_id;
    @NotNull
    private UUID sala_id;

    @NotNull
    private LocalDateTime dataInicio;
    @NotNull
    private LocalDateTime dataFim;
    private StatusReserva status;
}

