package cl.duoc.andesstay.rabbitadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateExchangeRequest(
		@NotBlank(message = "must not be blank")
		@Size(max = 255, message = "must be at most 255 characters")
		@Pattern(regexp = "\\S(.*\\S)?", message = "must not be blank or whitespace-only")
		String name,
		@NotBlank(message = "must not be blank")
		@Pattern(
				regexp = "(?i)direct|topic|fanout|headers",
				message = "must be one of: direct, topic, fanout, headers")
		String type,
		boolean durable,
		boolean autoDelete) {
}
