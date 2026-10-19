package cl.duoc.andesstay.rabbitadmin.dto;

public record UpdateQueueRequest(
		boolean durable,
		boolean exclusive,
		boolean autoDelete) {
}
