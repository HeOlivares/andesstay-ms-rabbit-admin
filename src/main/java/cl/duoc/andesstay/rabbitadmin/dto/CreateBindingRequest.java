package cl.duoc.andesstay.rabbitadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateBindingRequest(
		@NotBlank(message = "must not be blank")
		@Size(max = 255, message = "must be at most 255 characters")
		@Pattern(regexp = "\\S(.*\\S)?", message = "must not be blank or whitespace-only")
		String queue,
		@NotBlank(message = "must not be blank")
		@Size(max = 255, message = "must be at most 255 characters")
		@Pattern(regexp = "\\S(.*\\S)?", message = "must not be blank or whitespace-only")
		String exchange,
		@NotBlank(message = "must not be blank")
		@Size(max = 255, message = "must be at most 255 characters")
		String routingKey) {
}
