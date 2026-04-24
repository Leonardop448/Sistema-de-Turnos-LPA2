# Sistema de Turnos

## ¿Qué hace este proyecto?
Es un microservicio encargado de gestionar el ciclo de vida de los turnos de atención al cliente. Permite a los clientes solicitar un turno y a los asesores ir tomando el siguiente turno de la cola, organizando todo de forma automática según el nivel de prioridad y el tiempo de espera.

## Tecnologías usadas
- Java
- Spring Boot
- MongoDB Atlas
- Lombok
- Maven

## Estructura del proyecto
- **config**: Contiene configuraciones globales del sistema, como la política de CORS.
- **controller**: Define los endpoints de la API REST y maneja las respuestas y errores globales.
- **service**: Contiene la lógica de negocio, reglas de asignación y manejo de estados.
- **model**: Define las estructuras de datos y entidades que se guardan en la base de datos.
- **repository**: Se encarga de la comunicación directa con MongoDB.
- **enums**: Define listas de valores fijos permitidos como prioridades, estados y tipos de documento.

## Modelos

**Turno**
- `id` (String): Identificador único del turno en base de datos.
- `numeroTurno` (String): Número visible y secuencial del turno (ej. T-0001).
- `cliente` (Cliente): Información del usuario que pidió el turno.
- `asesor` (AsesorRef): Datos del asesor que atiende el turno.
- `area` (AreaRef): Área a la que corresponde el turno.
- `estado` (EstadoTurno): Estado actual del turno en el flujo.
- `prioridad` (PrioridadTurno): Nivel de urgencia del turno.
- `motivoPrioridad` (String): Razón de la urgencia si aplica.
- `fechaCreacion` (LocalDateTime): Momento exacto en el que se pidió el turno.
- `fechaAtencion` (LocalDateTime): Momento exacto en el que se atendió o canceló.

**Cliente**
- `tipoDocumento` (TipoDocumento): Tipo de identificación.
- `numeroDocumento` (String): Número de identificación.
- `nombre` (String): Nombre completo del cliente.

**AreaRef**
- `areaId` (String): ID del área en el sistema externo.
- `nombre` (String): Nombre del área.

**AsesorRef**
- `asesorId` (String): ID del asesor en el sistema externo.
- `nombre` (String): Nombre del asesor.
- `disponible` (boolean): Indica si el asesor está activo.

**EstadoTurno (Enum)**
Valores: `EN_ESPERA`, `EN_ATENCION`, `ATENDIDO`, `CANCELADO`

**PrioridadTurno (Enum)**
Valores: `PREFERENCIAL`, `NORMAL`

**TipoDocumento (Enum)**
Valores: `CC`, `TI`, `CE`, `PASAPORTE`

**CrearTurnoRequest (DTO)**
- `cliente` (Cliente): Información del cliente.
- `area` (AreaRef): Área donde se pide el turno.
- `prioridad` (PrioridadTurno): Prioridad solicitada.
- `motivoPrioridad` (String): Razón opcional.

**SiguienteTurnoRequest (DTO)**
- `asesorId` (String): ID del asesor.
- `nombre` (String): Nombre del asesor.

## Endpoints

| Método | Ruta | Qué hace | Body requerido | Respuesta |
|---|---|---|---|---|
| POST | `/api/turnos` | Crea un nuevo turno | `{"cliente":{"tipoDocumento":"CC","numeroDocumento":"123","nombre":"Juan"},"area":{"areaId":"A1","nombre":"Caja"},"prioridad":"NORMAL"}` | `{"id":"...","numeroTurno":"T-0001","estado":"EN_ESPERA",...}` |
| PUT | `/api/turnos/siguiente-turno` | Asigna el turno más prioritario en espera a un asesor | `{"asesorId":"AS1","nombre":"Pedro"}` | `{"id":"...","asesor":{"asesorId":"AS1","nombre":"Pedro","disponible":true},"estado":"EN_ATENCION",...}` |
| PUT | `/api/turnos/{id}/asignar-asesor` | Asigna manualmente un asesor a un turno específico | `{"asesorId":"AS1","nombre":"Pedro","disponible":true}` | `{"id":"...","estado":"EN_ATENCION",...}` |
| PUT | `/api/turnos/{id}/atender` | Marca un turno en atención como atendido | No aplica | `{"id":"...","estado":"ATENDIDO",...}` |
| PUT | `/api/turnos/{id}/cancelar` | Cancela un turno antes de que termine | No aplica | `{"id":"...","estado":"CANCELADO",...}` |
| PUT | `/api/turnos/{id}/cambiar-asesor` | Cambia el asesor de un turno ya asignado | `{"asesorId":"AS2","nombre":"Maria","disponible":true}` | `{"id":"...","asesor":{...},...}` |
| GET | `/api/turnos` | Retorna todos los turnos ordenados por prioridad | No aplica | `[{...}, {...}]` |
| GET | `/api/turnos/{id}` | Busca un turno específico por ID | No aplica | `{"id":"...","numeroTurno":"T-0001",...}` |
| GET | `/api/turnos/estado/{estado}` | Filtra turnos por estado | No aplica | `[{...}, {...}]` |
| GET | `/api/turnos/cliente/{numeroDocumento}` | Busca todos los turnos que ha pedido un cliente | No aplica | `[{...}, {...}]` |
| GET | `/api/turnos/asesor/{asesorId}` | Retorna turnos en espera o en atención de un asesor | No aplica | `[{...}, {...}]` |

## Flujo principal
1. El cliente pide un turno ingresando sus datos, a qué área va y qué prioridad tiene. El turno entra al sistema y queda en estado `EN_ESPERA`.
2. Un asesor pide el siguiente turno disponible. El sistema busca al que sigue en la cola y se lo asigna, cambiando el estado a `EN_ATENCION`.
3. El asesor hace su trabajo y al finalizar atiende el turno (pasa a `ATENDIDO`) o, si ocurre algún inconveniente antes, lo puede cancelar (pasa a `CANCELADO`).

## Lógica de prioridad
La cola de espera funciona ordenando a los clientes primero por el tipo de prioridad: los turnos de categoría `PREFERENCIAL` siempre pasan antes que los de categoría `NORMAL`. Si hay varias personas con la misma prioridad, el sistema desempata revisando su fecha de creación, para atender primero al que lleva más tiempo esperando.

## Cómo correr el proyecto
1. Clonar el repo.
2. Configurar la variable de entorno `MONGODB_URI` con la URI de MongoDB Atlas.
3. Correr con `mvn spring-boot:run` (o usar tu IDE de preferencia).
4. La API queda disponible en `http://localhost:8080`.

## Manejo de errores
- **404 Not Found**: Retorna esto si buscas un turno que no existe o al intentar una acción que rompa una regla de negocio (por ejemplo, querer cancelar un turno que ya está atendido, intentar atender sin estar en atención, o si no hay turnos en espera al pedir uno nuevo).
- **500 Internal Server Error**: Retorna `{"mensaje": "Error en el servidor"}` si ocurre algún error general que el programa no pudo manejar.
