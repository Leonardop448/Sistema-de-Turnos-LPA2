package com.turnos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsesorRequest {
    private String asesorId;
    private String nombre;
    private boolean disponible;
}
