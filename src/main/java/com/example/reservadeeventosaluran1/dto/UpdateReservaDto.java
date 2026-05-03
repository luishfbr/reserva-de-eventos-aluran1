package com.example.reservadeeventosaluran1.dto;

import com.example.reservadeeventosaluran1.database.enums.StatusReserva;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateReservaDto {
    private UUID usuario_id;
    private UUID sala_id;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private StatusReserva status;
}

