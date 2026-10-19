package cl.duoc.andesstay.rabbitadmin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.duoc.andesstay.rabbitadmin.web.CreateExchangeRequest;
import cl.duoc.andesstay.rabbitadmin.web.CreateQueueRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

class RabbitAdminServiceTest {

	private RabbitAdmin rabbitAdmin;
	private RabbitAdminService service;

	@BeforeEach
	void setUp() {
		rabbitAdmin = mock(RabbitAdmin.class);
		service = new RabbitAdminService(rabbitAdmin);
	}

	@Test
	void createQueueDeclaresDurableQueue() {
		when(rabbitAdmin.declareQueue(any(Queue.class))).thenReturn("q.demo");

		var created = service.createQueue(new CreateQueueRequest("q.demo", true, false, false));

		assertEquals("q.demo", created.get("name"));
		verify(rabbitAdmin).declareQueue(any(Queue.class));
	}

	@Test
	void blankQueueNameIsRejected() {
		assertThrows(IllegalArgumentException.class,
				() -> service.createQueue(new CreateQueueRequest("  ", true, false, false)));
	}

	@Test
	void unsupportedExchangeTypeIsRejected() {
		assertThrows(IllegalArgumentException.class,
				() -> service.createExchange(new CreateExchangeRequest("ex.demo", "stream", true, false)));
	}
}
