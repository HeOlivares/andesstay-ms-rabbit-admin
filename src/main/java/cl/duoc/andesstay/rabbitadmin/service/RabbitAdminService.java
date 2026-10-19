package cl.duoc.andesstay.rabbitadmin.service;

import cl.duoc.andesstay.rabbitadmin.web.CreateBindingRequest;
import cl.duoc.andesstay.rabbitadmin.web.CreateExchangeRequest;
import cl.duoc.andesstay.rabbitadmin.web.CreateQueueRequest;
import cl.duoc.andesstay.rabbitadmin.web.DeleteBindingRequest;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Service;

@Service
public class RabbitAdminService {

	private static final Set<String> EXCHANGE_TYPES = Set.of("direct", "topic", "fanout", "headers");

	private final RabbitAdmin rabbitAdmin;

	public RabbitAdminService(RabbitAdmin rabbitAdmin) {
		this.rabbitAdmin = rabbitAdmin;
	}

	public Map<String, Object> createQueue(CreateQueueRequest request) {
		String name = requireName(request.name(), "queue name");
		Queue queue = new Queue(name, request.durable(), request.exclusive(), request.autoDelete());
		rabbitAdmin.declareQueue(queue);
		return Map.of(
				"name", name,
				"durable", request.durable(),
				"exclusive", request.exclusive(),
				"autoDelete", request.autoDelete());
	}

	public void deleteQueue(String name) {
		String queue = requireName(name, "queue name");
		boolean removed = Boolean.TRUE.equals(rabbitAdmin.deleteQueue(queue));
		if (!removed) {
			throw new IllegalArgumentException("Queue not found or could not be deleted: " + queue);
		}
	}

	public Map<String, Object> createExchange(CreateExchangeRequest request) {
		String name = requireName(request.name(), "exchange name");
		String type = requireExchangeType(request.type());
		Exchange exchange = switch (type) {
			case "direct" -> new DirectExchange(name, request.durable(), request.autoDelete());
			case "topic" -> new TopicExchange(name, request.durable(), request.autoDelete());
			case "fanout" -> new FanoutExchange(name, request.durable(), request.autoDelete());
			case "headers" -> new HeadersExchange(name, request.durable(), request.autoDelete());
			default -> throw new IllegalArgumentException("Unsupported exchange type: " + type);
		};
		rabbitAdmin.declareExchange(exchange);
		return Map.of(
				"name", name,
				"type", type,
				"durable", request.durable(),
				"autoDelete", request.autoDelete());
	}

	public void deleteExchange(String name) {
		String exchange = requireName(name, "exchange name");
		boolean removed = rabbitAdmin.deleteExchange(exchange);
		if (!removed) {
			throw new IllegalArgumentException("Exchange not found or could not be deleted: " + exchange);
		}
	}

	public Map<String, Object> createBinding(CreateBindingRequest request) {
		String queueName = requireName(request.queue(), "queue");
		String exchangeName = requireName(request.exchange(), "exchange");
		String routingKey = requireName(request.routingKey(), "routingKey");
		rabbitAdmin.declareBinding(binding(queueName, exchangeName, routingKey));
		return Map.of(
				"queue", queueName,
				"exchange", exchangeName,
				"routingKey", routingKey);
	}

	public void deleteBinding(DeleteBindingRequest request) {
		String queueName = requireName(request.queue(), "queue");
		String exchangeName = requireName(request.exchange(), "exchange");
		String routingKey = requireName(request.routingKey(), "routingKey");
		rabbitAdmin.removeBinding(binding(queueName, exchangeName, routingKey));
	}

	private static Binding binding(String queueName, String exchangeName, String routingKey) {
		return new Binding(queueName, Binding.DestinationType.QUEUE, exchangeName, routingKey, null);
	}

	private static String requireName(String value, String field) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException(field + " must not be blank");
		}
		return value.trim();
	}

	private static String requireExchangeType(String type) {
		if (type == null || type.isBlank()) {
			throw new IllegalArgumentException("exchange type must not be blank");
		}
		String normalized = type.trim().toLowerCase(Locale.ROOT);
		if (!EXCHANGE_TYPES.contains(normalized)) {
			throw new IllegalArgumentException("exchange type must be one of " + EXCHANGE_TYPES);
		}
		return normalized;
	}
}
