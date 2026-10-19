package cl.duoc.andesstay.rabbitadmin.web;

import java.util.Map;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rabbit")
public class HealthController {

	private final ConnectionFactory connectionFactory;

	public HealthController(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	@GetMapping("/health")
	public Map<String, Object> health() {
		boolean rabbitConnected = false;
		try (var connection = connectionFactory.createConnection()) {
			rabbitConnected = connection.isOpen();
		}
		return Map.of(
				"status", rabbitConnected ? "UP" : "DOWN",
				"service", "andesstay-ms-rabbit-admin",
				"rabbitmq", rabbitConnected ? "CONNECTED" : "DISCONNECTED");
	}
}
