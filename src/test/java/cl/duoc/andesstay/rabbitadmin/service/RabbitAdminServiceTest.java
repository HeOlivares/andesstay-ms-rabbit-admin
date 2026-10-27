package cl.duoc.andesstay.rabbitadmin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.duoc.andesstay.rabbitadmin.dto.CreateBindingRequest;
import cl.duoc.andesstay.rabbitadmin.dto.CreateExchangeRequest;
import cl.duoc.andesstay.rabbitadmin.dto.CreateQueueRequest;
import cl.duoc.andesstay.rabbitadmin.dto.DeleteBindingRequest;
import cl.duoc.andesstay.rabbitadmin.dto.UpdateExchangeRequest;
import cl.duoc.andesstay.rabbitadmin.dto.UpdateQueueRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpIOException;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

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
	void updateQueueDeletesAndRedeclares() {
		when(rabbitAdmin.deleteQueue("q.demo")).thenReturn(true);
		when(rabbitAdmin.declareQueue(any(Queue.class))).thenReturn("q.demo");

		var updated = service.updateQueue("q.demo", new UpdateQueueRequest(true, false, false));

		assertEquals("q.demo", updated.get("name"));
		verify(rabbitAdmin).deleteQueue("q.demo");
		verify(rabbitAdmin).declareQueue(any(Queue.class));
	}

	@Test
	void deleteQueueFailsWhenMissing() {
		when(rabbitAdmin.getQueueProperties("missing")).thenReturn(null);

		assertThrows(ResourceNotFoundException.class, () -> service.deleteQueue("missing"));
	}

	@Test
	void deleteQueueRemovesExistingQueue() {
		when(rabbitAdmin.getQueueProperties("q.demo")).thenReturn(new java.util.Properties());
		when(rabbitAdmin.deleteQueue("q.demo")).thenReturn(true);

		service.deleteQueue("q.demo");

		verify(rabbitAdmin).deleteQueue("q.demo");
	}

	@Test
	void deleteExchangeFailsWhenMissing() {
		RabbitTemplate template = mock(RabbitTemplate.class);
		when(rabbitAdmin.getRabbitTemplate()).thenReturn(template);
		when(template.execute(any())).thenThrow(new AmqpIOException(new java.io.IOException("NOT_FOUND")));

		assertThrows(ResourceNotFoundException.class, () -> service.deleteExchange("missing"));
	}

	@Test
	void createExchangeDeclaresTopicExchange() {
		var created = service.createExchange(new CreateExchangeRequest("ex.demo", "topic", true, false));

		assertEquals("topic", created.get("type"));
		verify(rabbitAdmin).declareExchange(any(Exchange.class));
	}

	@Test
	void updateExchangeDeletesAndRedeclares() {
		when(rabbitAdmin.deleteExchange("ex.demo")).thenReturn(true);

		var updated = service.updateExchange("ex.demo", new UpdateExchangeRequest("direct", true, false));

		assertEquals("direct", updated.get("type"));
		verify(rabbitAdmin).deleteExchange("ex.demo");
		verify(rabbitAdmin).declareExchange(any(Exchange.class));
	}

	@Test
	void createAndDeleteBindingDelegateToRabbitAdmin() {
		service.createBinding(new CreateBindingRequest("q.demo", "ex.demo", "rk.demo"));
		service.deleteBinding(new DeleteBindingRequest("q.demo", "ex.demo", "rk.demo"));

		verify(rabbitAdmin).declareBinding(any(Binding.class));
		verify(rabbitAdmin).removeBinding(any(Binding.class));
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
