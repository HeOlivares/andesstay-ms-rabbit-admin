package cl.duoc.andesstay.rabbitadmin.service;

import cl.duoc.andesstay.rabbitadmin.dto.CreateBindingRequest;
import cl.duoc.andesstay.rabbitadmin.dto.CreateExchangeRequest;
import cl.duoc.andesstay.rabbitadmin.dto.CreateQueueRequest;
import cl.duoc.andesstay.rabbitadmin.dto.DeleteBindingRequest;
import cl.duoc.andesstay.rabbitadmin.dto.UpdateExchangeRequest;
import cl.duoc.andesstay.rabbitadmin.dto.UpdateQueueRequest;
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
		return declareQueue(request.name(), request.durable(), request.exclusive(), request.autoDelete());
	}

	public Map<String, Object> updateQueue(String name, UpdateQueueRequest request) {
		String queue = requireName(name, "queue name");
		deleteQueueIfPresent(queue);
		return declareQueue(queue, request.durable(), request.exclusive(), request.autoDelete());
	}

	public void deleteQueue(String name) {
		String queue = requireName(name, "queue name");
		if (rabbitAdmin.getQueueProperties(queue) == null) {
			throw new ResourceNotFoundException("Queue not found: " + queue);
		}
		rabbitAdmin.deleteQueue(queue);
	}

	public Map<String, Object> createExchange(CreateExchangeRequest request) {
		return declareExchange(request.name(), request.type(), request.durable(), request.autoDelete());
	}

	public Map<String, Object> updateExchange(String name, UpdateExchangeRequest request) {
		String exchange = requireName(name, "exchange name");
		deleteExchangeIfPresent(exchange);
		return declareExchange(exchange, request.type(), request.durable(), request.autoDelete());
	}

	public void deleteExchange(String name) {
		String exchange = requireName(name, "exchange name");
		if (!exchangeExists(exchange)) {
			throw new ResourceNotFoundException("Exchange not found: " + exchange);
		}
		rabbitAdmin.deleteExchange(exchange);
	}

	public Map<String, Object> createBinding(CreateBindingRequest request) {
		return declareBinding(request.queue(), request.exchange(), request.routingKey());
	}

	public Map<String, Object> updateBinding(CreateBindingRequest request) {
		String queueName = requireName(request.queue(), "queue");
		String exchangeName = requireName(request.exchange(), "exchange");
		String routingKey = requireName(request.routingKey(), "routingKey");
		rabbitAdmin.removeBinding(binding(queueName, exchangeName, routingKey));
		return declareBinding(queueName, exchangeName, routingKey);
	}

	public void deleteBinding(DeleteBindingRequest request) {
		String queueName = requireName(request.queue(), "queue");
		String exchangeName = requireName(request.exchange(), "exchange");
		String routingKey = requireName(request.routingKey(), "routingKey");
		rabbitAdmin.removeBinding(binding(queueName, exchangeName, routingKey));
	}

	private Map<String, Object> declareQueue(String rawName, boolean durable, boolean exclusive, boolean autoDelete) {
		String name = requireName(rawName, "queue name");
		Queue queue = new Queue(name, durable, exclusive, autoDelete);
		rabbitAdmin.declareQueue(queue);
		return Map.of(
				"name", name,
				"durable", durable,
				"exclusive", exclusive,
				"autoDelete", autoDelete);
	}

	private Map<String, Object> declareExchange(String rawName, String rawType, boolean durable, boolean autoDelete) {
		String name = requireName(rawName, "exchange name");
		String type = requireExchangeType(rawType);
		Exchange exchange = switch (type) {
			case "direct" -> new DirectExchange(name, durable, autoDelete);
			case "topic" -> new TopicExchange(name, durable, autoDelete);
			case "fanout" -> new FanoutExchange(name, durable, autoDelete);
			case "headers" -> new HeadersExchange(name, durable, autoDelete);
			default -> throw new IllegalArgumentException("Unsupported exchange type: " + type);
		};
		rabbitAdmin.declareExchange(exchange);
		return Map.of(
				"name", name,
				"type", type,
				"durable", durable,
				"autoDelete", autoDelete);
	}

	private Map<String, Object> declareBinding(String queue, String exchange, String routingKey) {
		String queueName = requireName(queue, "queue");
		String exchangeName = requireName(exchange, "exchange");
		String key = requireName(routingKey, "routingKey");
		rabbitAdmin.declareBinding(binding(queueName, exchangeName, key));
		return Map.of(
				"queue", queueName,
				"exchange", exchangeName,
				"routingKey", key);
	}

	private void deleteQueueIfPresent(String queue) {
		rabbitAdmin.deleteQueue(queue);
	}

	private void deleteExchangeIfPresent(String exchange) {
		rabbitAdmin.deleteExchange(exchange);
	}

	private boolean exchangeExists(String name) {
		try {
			Boolean present = rabbitAdmin.getRabbitTemplate().execute(channel -> {
				channel.exchangeDeclarePassive(name);
				return Boolean.TRUE;
			});
			return Boolean.TRUE.equals(present);
		} catch (RuntimeException missing) {
			return false;
		}
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
