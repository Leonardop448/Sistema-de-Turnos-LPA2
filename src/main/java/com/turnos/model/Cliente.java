package com.turnos.model;

import com.turnos.enums.TipoDocumento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;
    private String nombre;
}
