package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import ru.practicum.client.Client;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.ClientRequestDto;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

@SpringBootApplication
public class ExploreWithMe {
	public static void main(String[] args) {
		SpringApplication.run(ExploreWithMe.class, args);

		/*Client client = new StatsClient("http://localhost:9090", new RestTemplateBuilder());

		StatsRequestDto statsRequestDto = new StatsRequestDto(
				"ewm-main-service",
				"/events/2",
				"192.163.0.1",
				LocalDateTime.of(2024, Month.MAY, 23, 23, 0, 0));
		client.saveStats(statsRequestDto);

		ClientRequestDto requestDto = new ClientRequestDto(
				LocalDateTime.of(2020, Month.MAY, 5, 0, 0, 0),
				LocalDateTime.of(2035, Month.MAY, 5, 0, 0, 0),
				List.of("/events/2"),
				false
		);
		List<StatsResponseDto> response = client.getStats(requestDto);

		if (!response.isEmpty()) {
			System.out.println(response.get(0));
		}*/
	}
}