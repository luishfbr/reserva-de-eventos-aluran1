package com.example.reservadeeventosaluran1.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SalaDto {

    @NotBlank
    private String nome;
    @NotNull
    @Min(1)
    private Integer capacidade;
}
