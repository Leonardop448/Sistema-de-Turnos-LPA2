package com.turnos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsesorRequest {

    @NotBlank(message = "El ID del asesor es obligatorio")
    private String asesorId;

    @NotBlank(message = "El nombre del asesor es obligatorio")
    private String nombre;

    private boolean disponible;
}
