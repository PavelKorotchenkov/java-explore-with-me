package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.StatsDtoRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class StatsClient extends BaseClient {

	@Autowired
	public StatsClient(@Value("${stats-service.url}") String serverUrl, RestTemplateBuilder builder) {
		super(
				builder
						.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
						.requestFactory(HttpComponentsClientHttpRequestFactory::new)
						.build()
		);
	}

	public ResponseEntity<Object> saveStats(StatsDtoRequest requestDto) {
		return post("/hit", requestDto);
	}

	public ResponseEntity<Object> getStats(String start, String end, List<String> uris, Boolean unique) {
		String encodedStart = URLEncoder.encode(start, StandardCharsets.UTF_8);
		String encodedEnd = URLEncoder.encode(end, StandardCharsets.UTF_8);
		Map<String, Object> parameters = Map.of(
				"start", encodedStart,
				"end", encodedEnd
		);

		parameters.put("unique", unique);

		if (uris != null) {
			parameters.put("uris", uris);
			return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
		} else {
			return get("/stats?start={start}&end={end}&unique={unique}", parameters);
		}
	}
}
