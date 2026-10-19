package cl.duoc.andesstay.rabbitadmin.web;

import cl.duoc.andesstay.rabbitadmin.dto.CreateQueueRequest;
import cl.duoc.andesstay.rabbitadmin.dto.UpdateQueueRequest;
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
@RequestMapping("/api/queues")
public class QueueController {

	private final RabbitAdminService rabbitAdminService;

	public QueueController(RabbitAdminService rabbitAdminService) {
		this.rabbitAdminService = rabbitAdminService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Map<String, Object> create(@Valid @RequestBody CreateQueueRequest request) {
		return rabbitAdminService.createQueue(request);
	}

	@PutMapping("/{name}")
	public Map<String, Object> update(@PathVariable String name, @Valid @RequestBody UpdateQueueRequest request) {
		return rabbitAdminService.updateQueue(name, request);
	}

	@DeleteMapping("/{name}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable String name) {
		rabbitAdminService.deleteQueue(name);
	}
}
