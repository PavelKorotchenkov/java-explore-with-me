package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.ClientRequestDto;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
public class StatsClient extends BaseClient implements Client {

	@Autowired
	public StatsClient(@Value("${stats-service.url}") String serverUrl, RestTemplateBuilder builder) {
		super(
				builder
						.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
						.requestFactory(HttpComponentsClientHttpRequestFactory::new)
						.build()
		);
	}

	public void saveStats(StatsRequestDto requestDto) {
		post("/hit", requestDto);
	}

	public List<StatsResponseDto> getStats(ClientRequestDto requestDto) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		Map<String, Object> parameters = Map.of(
				"start", requestDto.getStart().format(formatter),
				"end", requestDto.getEnd().format(formatter),
				"uris", String.join(",", requestDto.getUris()),
				"unique", requestDto.isUnique()
		);

		String url = parameters.get("uris") != null ?
				"/stats?start={start}&end={end}&uris={uris}&unique={unique}" :
				"/stats?start={start}&end={end}&unique={unique}";

		return get(url, parameters);
	}
}
