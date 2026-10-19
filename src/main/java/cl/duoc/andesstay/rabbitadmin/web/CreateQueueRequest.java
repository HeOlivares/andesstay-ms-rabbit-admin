package cl.duoc.andesstay.rabbitadmin.web;

import jakarta.validation.constraints.NotBlank;

public record CreateQueueRequest(
		@NotBlank String name,
		boolean durable,
		boolean exclusive,
		boolean autoDelete) {
}
