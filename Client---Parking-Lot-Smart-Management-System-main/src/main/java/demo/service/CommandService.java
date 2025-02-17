package demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import demo.model.CommandBoundary;
import java.util.List;

@Service
public class CommandService {
	private final WebClient webClient;

	public CommandService(@Value("${backend.url}") String backendUrl) {
		this.webClient = WebClient.create(backendUrl);
	}

	public List<Object> invokeCommand(CommandBoundary command) {
		return webClient.post().uri("/aii/commands").bodyValue(command).retrieve().bodyToFlux(Object.class)
				.collectList().block();
	}
}
