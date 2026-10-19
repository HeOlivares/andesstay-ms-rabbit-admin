package cl.duoc.andesstay.rabbitadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateExchangeRequest(
		@NotBlank(message = "must not be blank")
		@Pattern(
				regexp = "(?i)direct|topic|fanout|headers",
				message = "must be one of: direct, topic, fanout, headers")
		String type,
		boolean durable,
		boolean autoDelete) {
}
