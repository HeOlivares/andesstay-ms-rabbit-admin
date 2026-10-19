package cl.duoc.andesstay.rabbitadmin.web;

import jakarta.validation.constraints.NotBlank;

public record CreateExchangeRequest(
		@NotBlank String name,
		@NotBlank String type,
		boolean durable,
		boolean autoDelete) {
}
