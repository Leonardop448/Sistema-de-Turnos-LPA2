package com.turnos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.turnos.enums.EstadoTurno;
import com.turnos.enums.PrioridadTurno;
import com.turnos.enums.TipoDocumento;
import com.turnos.model.AreaRef;
import com.turnos.model.Cliente;
import com.turnos.model.Turno;
import com.turnos.repository.TurnoRepository;

@ExtendWith(MockitoExtension.class)

public class TurnoServiceTest {

    @Mock
    private TurnoRepository turnoRepository;

    @InjectMocks
    private TurnoService turnoService;

    @Test
    void crearTurnoConEstadoEnEspera() {
        Cliente cliente = new Cliente(TipoDocumento.CC, "123456", "Juan David");
        AreaRef area = new AreaRef("area-1", "Caja");

        when(turnoRepository.findTopByOrderByNumeroTurnoDesc())
                .thenReturn(Optional.empty());
        when(turnoRepository.save(any(Turno.class)))
                .thenAnswer(i -> i.getArgument(0));

        Turno resultado = turnoService.crearTurno(cliente, area, PrioridadTurno.NORMAL, null);

        assertEquals(EstadoTurno.EN_ESPERA, resultado.getEstado());
        assertNull(resultado.getAsesor());
        assertNotNull(resultado.getFechaCreacion());
        assertEquals("T-0001", resultado.getNumeroTurno());

    }

    @Test
    void siguienteTurnoPreferencialAntesQueNormal() {

        Turno turnoNormal = Turno.builder()
                .id("1")
                .estado(EstadoTurno.EN_ESPERA)
                .prioridad(PrioridadTurno.NORMAL)
                .fechaCreacion(LocalDateTime.now().minusMinutes(10))
                .build();

        Turno turnoPreferencial = Turno.builder()
                .id("2")
                .estado(EstadoTurno.EN_ESPERA)
                .prioridad(PrioridadTurno.PREFERENCIAL)
                .fechaCreacion(LocalDateTime.now().minusMinutes(5))
                .build();

        when(turnoRepository.findByEstado(EstadoTurno.EN_ESPERA))
                .thenReturn(List.of(turnoNormal, turnoPreferencial));

        when(turnoRepository.save(any(Turno.class)))
                .thenAnswer(i -> i.getArgument(0));

        Turno resultado = turnoService.siguienteTurno("asesor-1", "Pedro");

        assertEquals("2", resultado.getId());
        assertEquals(EstadoTurno.EN_ATENCION, resultado.getEstado());
        assertNotNull(resultado.getAsesor());

    }

    @Test
    void siguienteTurnoSinTurnosEnEsperaDebelanzarExcepcion() {
        when(turnoRepository.findByEstado(EstadoTurno.EN_ESPERA))
                .thenReturn(List.of());

        assertThrows(RuntimeException.class, () -> {
            turnoService.siguienteTurno("asesor-1", "Leo");
        });

    }

    @Test
    void atenderTurnoDebeCambiarEstadoAtendido() {

        Turno turno = Turno.builder()
                .id("1")
                .estado(EstadoTurno.EN_ATENCION)
                .build();

        when(turnoRepository.findById("1"))
                .thenReturn(Optional.of(turno));
        when(turnoRepository.save(any(Turno.class)))
                .thenAnswer(i -> i.getArgument(0));

        Turno resultado = turnoService.atenderTurno("1");

        assertEquals(EstadoTurno.ATENDIDO, resultado.getEstado());
        assertNotNull(resultado.getFechaAtencion());
    }

    @Test
    void atenderTurnoSinEstarEnAtencionDebeLanzarExcepcion() {

        Turno turno = Turno.builder()
                .id("1")
                .estado(EstadoTurno.EN_ESPERA)
                .build();

        when(turnoRepository.findById("1"))
                .thenReturn(Optional.of(turno));

        assertThrows(RuntimeException.class, () -> {
            turnoService.atenderTurno("1");
        });

    }

    @Test
    void cancelarTurnoDebeCambiarEstadoACancelado() {

        Turno turno = Turno.builder()
                .id("1")
                .estado(EstadoTurno.EN_ESPERA)
                .build();

        when(turnoRepository.findById("1"))
                .thenReturn(Optional.of(turno));
        when(turnoRepository.save(any(Turno.class)))
                .thenAnswer(i -> i.getArgument(0));

        Turno resultado = turnoService.cancelarTurno("1");

        assertEquals(EstadoTurno.CANCELADO, resultado.getEstado());
        assertNotNull(resultado.getFechaAtencion());

    }

    @Test
    void cancelarTurnoYaAtendidoDebeLanzarExcepcion() {

        Turno turno = Turno.builder()
                .id("1")
                .estado(EstadoTurno.ATENDIDO)
                .build();

        when(turnoRepository.findById("1"))
                .thenReturn(Optional.of(turno));

        assertThrows(RuntimeException.class, () -> {
            turnoService.cancelarTurno("1");
        });

    }

}
