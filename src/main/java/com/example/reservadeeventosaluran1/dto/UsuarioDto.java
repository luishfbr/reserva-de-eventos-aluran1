package com.example.reservadeeventosaluran1.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class UsuarioDto {

    @NotBlank
    private String nome;
    @NotBlank
    private String email;
}
