package cl.duoc.andesstay.rabbitadmin.web;

import cl.duoc.andesstay.rabbitadmin.dto.CreateExchangeRequest;
import cl.duoc.andesstay.rabbitadmin.dto.UpdateExchangeRequest;
import cl.duoc.andesstay.rabbitadmin.service.RabbitAdminService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exchanges")
public class ExchangeController {

	private final RabbitAdminService rabbitAdminService;

	public ExchangeController(RabbitAdminService rabbitAdminService) {
		this.rabbitAdminService = rabbitAdminService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Map<String, Object> create(@Valid @RequestBody CreateExchangeRequest request) {
		return rabbitAdminService.createExchange(request);
	}

	@PutMapping("/{name}")
	public Map<String, Object> update(@PathVariable String name, @Valid @RequestBody UpdateExchangeRequest request) {
		return rabbitAdminService.updateExchange(name, request);
	}

	@DeleteMapping("/{name}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable String name) {
		rabbitAdminService.deleteExchange(name);
	}
}
