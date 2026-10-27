# andesstay-ms-rabbit-admin

Microservicio administrador de RabbitMQ para AndesStay.

Spring Boot 3.4 / Java 21. Expone una API REST para crear, actualizar y eliminar colas, exchanges y bindings.

## Arranque local

```bash
docker compose up -d rabbitmq
.\mvnw.cmd spring-boot:run
```

Health (incluye prueba de conexión AMQP):

```bash
curl http://localhost:8085/api/health
```

Respuesta esperada cuando RabbitMQ local está arriba:

```json
{
  "status": "UP",
  "service": "andesstay-ms-rabbit-admin",
  "rabbitmq": "CONNECTED"
}
```

## API REST

La lógica vive en `RabbitAdminService`. Los controllers solo delegan.

| Método | Ruta | Descripción |
| --- | --- | --- |
| `GET` | `/api/health` | Estado del servicio y conexión a RabbitMQ |
| `POST` | `/api/queues` | Crear cola |
| `PUT` | `/api/queues/{name}` | Actualizar cola (redeclara) |
| `DELETE` | `/api/queues/{name}` | Eliminar cola |
| `POST` | `/api/exchanges` | Crear exchange (`direct`, `topic`, `fanout`, `headers`) |
| `PUT` | `/api/exchanges/{name}` | Actualizar exchange (redeclara) |
| `DELETE` | `/api/exchanges/{name}` | Eliminar exchange |
| `POST` | `/api/bindings` | Crear binding cola ↔ exchange |
| `PUT` | `/api/bindings` | Actualizar binding (redeclara) |
| `DELETE` | `/api/bindings` | Eliminar binding (body JSON) |

### Códigos HTTP

| Código | Cuándo |
| --- | --- |
| `201` | Recurso creado (`POST`) |
| `200` | Recurso actualizado (`PUT`) |
| `204` | Eliminación OK (`DELETE`) |
| `400` | Validación / body inválido |
| `404` | Cola o exchange inexistente al borrar |

**Nota PUT:** `PUT` redeclara (borra y vuelve a crear). Los mensajes encolados en esa cola **se pierden**.

### Ejemplos curl (Windows `cmd`)

Crear cola:

```bash
curl -X POST http://localhost:8085/api/queues ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"q.demo\",\"durable\":true,\"exclusive\":false,\"autoDelete\":false}"
```

Eliminar cola:

```bash
curl -X DELETE http://localhost:8085/api/queues/q.demo
```

Crear exchange:

```bash
curl -X POST http://localhost:8085/api/exchanges ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"cmd.demo\",\"type\":\"direct\",\"durable\":true,\"autoDelete\":false}"
```

Eliminar exchange:

```bash
curl -X DELETE http://localhost:8085/api/exchanges/cmd.demo
```

Crear binding:

```bash
curl -X POST http://localhost:8085/api/bindings ^
  -H "Content-Type: application/json" ^
  -d "{\"queue\":\"q.demo\",\"exchange\":\"cmd.demo\",\"routingKey\":\"demo.key\"}"
```

Eliminar binding:

```bash
curl -X DELETE http://localhost:8085/api/bindings ^
  -H "Content-Type: application/json" ^
  -d "{\"queue\":\"q.demo\",\"exchange\":\"cmd.demo\",\"routingKey\":\"demo.key\"}"
```

### Validación (HTTP 400)

Los DTO usan `@Valid` + Bean Validation. Nombres vacíos o configs inválidas responden `400` con mensaje claro:

```bash
curl -i -X POST http://localhost:8085/api/queues ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"\",\"durable\":true,\"exclusive\":false,\"autoDelete\":false}"
```

Ejemplo de respuesta:

```http
HTTP/1.1 400
{"error":"name must not be blank"}
```

Tipo de exchange inválido:

```bash
curl -i -X POST http://localhost:8085/api/exchanges ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"ex.demo\",\"type\":\"stream\",\"durable\":true,\"autoDelete\":false}"
```

## Variables

| Variable | Default | Uso |
| --- | --- | --- |
| `SERVER_PORT` | `8085` | Puerto HTTP |
| `SPRING_RABBITMQ_HOST` | `localhost` | Host AMQP |
| `SPRING_RABBITMQ_PORT` | `5672` | Puerto AMQP |
| `SPRING_RABBITMQ_USERNAME` | `guest` | Usuario |
| `SPRING_RABBITMQ_PASSWORD` | `guest` | Clave |

## Docker

```bash
docker compose up --build
```

UI de RabbitMQ Management: `http://localhost:15672` (`guest` / `guest`).

## Convención de ramas

- `main` protegida: sin push directo; PR con al menos 1 aprobación; reviews stale se descartan; `enforce_admins`.
- Colaboradores: Héctor (owner), Rolando Lillo, Cristian Monsalve.
