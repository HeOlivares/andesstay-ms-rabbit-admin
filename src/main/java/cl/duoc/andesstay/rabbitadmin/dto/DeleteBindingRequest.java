package cl.duoc.andesstay.rabbitadmin.dto;

import jakarta.validation.constraints.NotBlank;

public record DeleteBindingRequest(
		@NotBlank String queue,
		@NotBlank String exchange,
		@NotBlank String routingKey) {
}
