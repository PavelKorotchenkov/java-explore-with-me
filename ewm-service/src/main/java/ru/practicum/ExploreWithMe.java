package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import ru.practicum.client.Client;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.StatsDtoRequest;

@SpringBootApplication
public class ExploreWithMe {
	public static void main(String[] args) {
		SpringApplication.run(ExploreWithMe.class, args);

		Client<StatsDtoRequest> client = new StatsClient("http://localhost:9090", new RestTemplateBuilder());
		StatsDtoRequest statsDtoRequest = new StatsDtoRequest(
				"ewm-main-service",
				"/events/1",
				"192.163.0.1",
				"2024-05-23 23:00:00");
		client.saveStats(statsDtoRequest);

		ResponseEntity<Object> response = client.getStats("2020-05-05 00:00:00",
				"2035-05-05 00:00:00",
				"/events/1,/events",
				false);
		System.out.println("Response: " + response.getBody());
	}
}