package com.turnos.repository;

import com.turnos.enums.EstadoTurno;
import com.turnos.model.Turno;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de acceso a datos para la colección "turnos" en MongoDB.
 * Spring Data genera la implementación automáticamente a partir de los nombres de los métodos.
 */
@Repository
public interface TurnoRepository extends MongoRepository<Turno, String> {

    /**
     * Busca todos los turnos que se encuentren en un estado específico.
     *
     * @param estado Estado del turno (EN_ESPERA, EN_ATENCION, ATENDIDO, CANCELADO)
     * @return Lista de turnos con el estado indicado
     */
    List<Turno> findByEstado(EstadoTurno estado);

    /**
     * Busca todos los turnos asociados a un número de documento de cliente.
     *
     * @param numeroDocumento Número de documento del cliente
     * @return Lista de turnos del cliente
     */
    List<Turno> findByClienteNumeroDocumento(String numeroDocumento);

    /**
     * Busca todos los turnos asignados a un asesor específico.
     *
     * @param asesorId Identificador del asesor en el microservicio externo
     * @return Lista de turnos del asesor
     */
    List<Turno> findByAsesorAsesorId(String asesorId);

    /**
     * Busca todos los turnos pertenecientes a un área específica.
     *
     * @param areaId Identificador del área en el microservicio externo
     * @return Lista de turnos del área
     */
    List<Turno> findByAreaAreaId(String areaId);

    /**
     * Obtiene el último turno registrado ordenado por numeroTurno de forma descendente.
     * Se usa para calcular el siguiente número de turno a asignar.
     *
     * @return Optional con el turno de número más alto, o vacío si no hay ninguno
     */
    Optional<Turno> findTopByOrderByNumeroTurnoDesc();
}
