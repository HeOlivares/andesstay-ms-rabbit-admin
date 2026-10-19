package cl.duoc.andesstay.rabbitadmin.web;

import jakarta.validation.constraints.NotBlank;

public record CreateBindingRequest(
		@NotBlank String queue,
		@NotBlank String exchange,
		@NotBlank String routingKey) {
}
