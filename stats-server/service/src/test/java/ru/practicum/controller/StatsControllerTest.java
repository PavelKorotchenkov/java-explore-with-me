package ru.practicum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.StatsDtoRequest;
import ru.practicum.dto.StatsDtoResponse;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatsController.class)
class StatsControllerTest {

	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	StatsService service;

	@Test
	void saveStats() throws Exception {
		StatsDtoRequest request = new StatsDtoRequest();
		request.setApp("ewm-main-service");
		request.setUri("/events/1");
		request.setIp("192.163.0.1");
		request.setTimestamp("2024-05-19 11:00:23");

		mockMvc.perform(post("/hit")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated());
		verify(service, times(1)).saveStats(any());
	}

	@Test
	void getStats_whenCorrectParams_thenReturnStatsWithStatusOk() throws Exception {
		String start = "2024-04-10 10:10:10";
		String end = "2024-05-10 20:20:20";
		List<String> uris = List.of("/events/1");
		boolean unique = false;

		StatsDtoResponse response = new StatsDtoResponse();
		response.setApp("ewm-main-service");
		response.setUri("/events/1");
		response.setHits(1);

		when(service.getStats(any(LocalDateTime.class), any(LocalDateTime.class), anyList(), anyBoolean())).thenReturn(List.of(response));

		mockMvc.perform(get("/stats")
						.param("start", start)
						.param("end", end)
						.param("uris", String.valueOf(uris))
						.param("unique", String.valueOf(unique))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].app").value("ewm-main-service"))
				.andExpect(jsonPath("$[0].uri").value("/events/1"))
				.andExpect(jsonPath("$[0].hits").value(1));
	}

	@Test
	void getStats_whenEndBeforeStart_thenReturnStatusBadRequest() throws Exception {
		String start = "2024-08-10 10:10:10";
		String end = "2024-05-10 20:20:20";
		List<String> uris = List.of("/events/1");
		boolean unique = false;

		StatsDtoResponse response = new StatsDtoResponse();
		response.setApp("ewm-main-service");
		response.setUri("/events/1");
		response.setHits(1);

		mockMvc.perform(get("/stats")
						.param("start", start)
						.param("end", end)
						.param("uris", String.valueOf(uris))
						.param("unique", String.valueOf(unique))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}
}
