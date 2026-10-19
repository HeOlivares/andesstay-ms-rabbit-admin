# andesstay-ms-rabbit-admin

Microservicio administrador de RabbitMQ para AndesStay (tarjeta H2).

Spring Boot 3.4 / Java 21. Expone una API REST para crear y eliminar colas, exchanges y bindings.

## Arranque local

```bash
docker compose up -d rabbitmq
.\mvnw.cmd spring-boot:run
```

Health (incluye prueba de conexión AMQP):

```bash
curl http://localhost:8085/api/rabbit/health
```

Respuesta esperada cuando RabbitMQ local está arriba:

```json
{
  "status": "UP",
  "service": "andesstay-ms-rabbit-admin",
  "rabbitmq": "CONNECTED"
}
```

## API

| Método | Ruta | Descripción |
| --- | --- | --- |
| `GET` | `/api/rabbit/health` | Estado del servicio y conexión a RabbitMQ |
| `POST` | `/api/rabbit/queues` | Crear cola |
| `DELETE` | `/api/rabbit/queues/{name}` | Eliminar cola |
| `POST` | `/api/rabbit/exchanges` | Crear exchange (`direct`, `topic`, `fanout`, `headers`) |
| `DELETE` | `/api/rabbit/exchanges/{name}` | Eliminar exchange |
| `POST` | `/api/rabbit/bindings` | Crear binding cola ↔ exchange |
| `DELETE` | `/api/rabbit/bindings` | Eliminar binding (body JSON) |

Ejemplos:

```bash
curl -X POST http://localhost:8085/api/rabbit/queues ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"q.demo\",\"durable\":true,\"exclusive\":false,\"autoDelete\":false}"

curl -X POST http://localhost:8085/api/rabbit/exchanges ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"cmd.demo\",\"type\":\"direct\",\"durable\":true,\"autoDelete\":false}"

curl -X POST http://localhost:8085/api/rabbit/bindings ^
  -H "Content-Type: application/json" ^
  -d "{\"queue\":\"q.demo\",\"exchange\":\"cmd.demo\",\"routingKey\":\"demo.key\"}"
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
