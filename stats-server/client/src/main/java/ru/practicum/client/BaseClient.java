package ru.practicum.client;

import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.StatsResponseDto;

import java.util.List;
import java.util.Map;

public class BaseClient {
	protected final RestTemplate rest;

	public BaseClient(RestTemplate rest) {
		this.rest = rest;
	}

	protected <T> void post(String path, T body) {
		makeAndSendRequest(HttpMethod.POST, path, null, body);
	}

	protected List<StatsResponseDto> get(String path, @Nullable Map<String, Object> parameters) {
		ResponseEntity<Object> response = makeAndSendRequest(HttpMethod.GET, path, parameters, null);
		List<StatsResponseDto> stats = (List<StatsResponseDto>) response.getBody();
		return stats;
	}

	private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, @Nullable Map<String, Object> parameters, @Nullable T body) {
		HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

		ResponseEntity<Object> statsServerResponse;

		if (parameters != null) {
			statsServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
		} else {
			statsServerResponse = rest.exchange(path, method, requestEntity, Object.class);
		}

		return statsServerResponse;
	}

	private HttpHeaders defaultHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));
		return headers;
	}
}
