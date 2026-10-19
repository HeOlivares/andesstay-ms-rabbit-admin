package cl.duoc.andesstay.rabbitadmin.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateExchangeRequest(
		@NotBlank String type,
		boolean durable,
		boolean autoDelete) {
}
