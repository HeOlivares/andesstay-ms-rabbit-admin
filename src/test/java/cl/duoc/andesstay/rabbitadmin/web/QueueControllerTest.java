package cl.duoc.andesstay.rabbitadmin.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.duoc.andesstay.rabbitadmin.dto.CreateQueueRequest;
import cl.duoc.andesstay.rabbitadmin.dto.UpdateQueueRequest;
import cl.duoc.andesstay.rabbitadmin.service.RabbitAdminService;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = QueueController.class)
class QueueControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private RabbitAdminService rabbitAdminService;

	@Test
	void createDelegatesToServiceOnly() throws Exception {
		when(rabbitAdminService.createQueue(any(CreateQueueRequest.class)))
				.thenReturn(Map.of("name", "q.demo", "durable", true, "exclusive", false, "autoDelete", false));

		mockMvc.perform(post("/api/queues")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"name":"q.demo","durable":true,"exclusive":false,"autoDelete":false}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value("q.demo"));

		verify(rabbitAdminService).createQueue(any(CreateQueueRequest.class));
	}

	@Test
	void updateDelegatesToServiceOnly() throws Exception {
		when(rabbitAdminService.updateQueue(eq("q.demo"), any(UpdateQueueRequest.class)))
				.thenReturn(Map.of("name", "q.demo", "durable", true, "exclusive", false, "autoDelete", false));

		mockMvc.perform(put("/api/queues/q.demo")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"durable":true,"exclusive":false,"autoDelete":false}
								"""))
				.andExpect(status().isOk());

		verify(rabbitAdminService).updateQueue(eq("q.demo"), any(UpdateQueueRequest.class));
	}

	@Test
	void deleteDelegatesToServiceOnly() throws Exception {
		mockMvc.perform(delete("/api/queues/q.demo"))
				.andExpect(status().isNoContent());

		verify(rabbitAdminService).deleteQueue("q.demo");
	}
}
