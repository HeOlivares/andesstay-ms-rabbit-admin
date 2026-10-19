package cl.duoc.andesstay.rabbitadmin.web;

import cl.duoc.andesstay.rabbitadmin.dto.CreateBindingRequest;
import cl.duoc.andesstay.rabbitadmin.dto.DeleteBindingRequest;
import cl.duoc.andesstay.rabbitadmin.service.RabbitAdminService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rabbit/bindings")
public class BindingController {

	private final RabbitAdminService rabbitAdminService;

	public BindingController(RabbitAdminService rabbitAdminService) {
		this.rabbitAdminService = rabbitAdminService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Map<String, Object> create(@Valid @RequestBody CreateBindingRequest request) {
		return rabbitAdminService.createBinding(request);
	}

	@PutMapping
	public Map<String, Object> update(@Valid @RequestBody CreateBindingRequest request) {
		return rabbitAdminService.updateBinding(request);
	}

	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@Valid @RequestBody DeleteBindingRequest request) {
		rabbitAdminService.deleteBinding(request);
	}
}
