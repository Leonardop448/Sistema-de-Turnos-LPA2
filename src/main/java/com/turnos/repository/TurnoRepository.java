package com.turnos.repository;

import com.turnos.enums.EstadoTurno;
import com.turnos.model.Turno;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TurnoRepository extends MongoRepository<Turno, String> {

    List<Turno> findByEstado(EstadoTurno estado);

    List<Turno> findByClienteNumeroDocumento(String numeroDocumento);

    List<Turno> findByAsesorAsesorId(String asesorId);

    List<Turno> findByAreaAreaId(String areaId);

    Optional<Turno> findTopByOrderByNumeroTurnoDesc();
}
