package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.StatsDtoRequest;

import java.util.Map;

@Component
public class StatsClient extends BaseClient implements Client<StatsDtoRequest> {

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

	public ResponseEntity<Object> getStats(String start, String end, String uris, Boolean unique) {
		Map<String, Object> parameters = Map.of(
				"start", start,
				"end", end,
				"unique", unique,
				"uris", uris
		);

		if (parameters.get("uris") != null) {
			return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
		} else {
			return get("/stats?start={start}&end={end}&unique={unique}", parameters);
		}
	}
}
