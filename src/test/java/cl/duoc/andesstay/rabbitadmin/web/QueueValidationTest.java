package cl.duoc.andesstay.rabbitadmin.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.duoc.andesstay.rabbitadmin.dto.CreateQueueRequest;
import cl.duoc.andesstay.rabbitadmin.service.RabbitAdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = QueueController.class)
@Import(ApiExceptionHandler.class)
class QueueValidationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private RabbitAdminService rabbitAdminService;

	@Test
	void emptyQueueNameReturns400WithClearMessage() throws Exception {
		mockMvc.perform(post("/api/queues")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"name":"","durable":true,"exclusive":false,"autoDelete":false}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("name")));

		verify(rabbitAdminService, never()).createQueue(any(CreateQueueRequest.class));
	}

	@Test
	void blankQueueNameReturns400() throws Exception {
		mockMvc.perform(post("/api/queues")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"name":"   ","durable":true,"exclusive":false,"autoDelete":false}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").exists());

		verify(rabbitAdminService, never()).createQueue(any(CreateQueueRequest.class));
	}

	@Test
	void missingQueueNameReturns400() throws Exception {
		mockMvc.perform(post("/api/queues")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"durable":true,"exclusive":false,"autoDelete":false}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").exists());

		verify(rabbitAdminService, never()).createQueue(any(CreateQueueRequest.class));
	}
}
