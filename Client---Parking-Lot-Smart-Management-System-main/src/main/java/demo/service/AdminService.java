package demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import demo.model.UserBoundary;
import demo.model.CommandBoundary;
import java.util.List;

@Service
public class AdminService {
	private final WebClient webClient;

	public AdminService(@Value("${backend.url}") String backendUrl) {
		this.webClient = WebClient.create(backendUrl);
	}

	public void deleteAllUsers(String userSystemID, String userEmail) {
		webClient.delete()
		.uri(uriBuilder -> uriBuilder
				.path("/aii/admin/users")
				.queryParam("userSystemID", userSystemID)
				.queryParam("userEmail", userEmail)
				.build())
		.retrieve()
		.toBodilessEntity()
		.block();
	}

	public void deleteAllObjects(String userSystemID, String userEmail) {
		webClient.delete()
		.uri(uriBuilder -> uriBuilder
				.path("/aii/admin/objects")
				.queryParam("userSystemID", userSystemID)
				.queryParam("userEmail", userEmail)
				.build())
		.retrieve()
		.toBodilessEntity()
		.block();
	}

	public void deleteAllCommands(String userSystemID, String userEmail) {
		webClient.delete()
		.uri(uriBuilder -> uriBuilder
				.path("/aii/admin/commands")
				.queryParam("userSystemID", userSystemID)
				.queryParam("userEmail", userEmail)
				.build())
		.retrieve()
		.toBodilessEntity()
		.block();
	}

	public List<UserBoundary> getAllUsers(String userSystemID, String userEmail, int page, int size) {
		return webClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/aii/admin/users")
						.queryParam("userSystemID", userSystemID)
						.queryParam("userEmail", userEmail)
						.queryParam("page", page)
						.queryParam("size", size)
						.build())
				.retrieve()
				.bodyToFlux(UserBoundary.class)
				.collectList()
				.block();
	}

	public List<CommandBoundary> getAllCommands(String userSystemID, String userEmail, int page, int size) {
		return webClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/aii/admin/commands")
						.queryParam("userSystemID", userSystemID)
						.queryParam("userEmail", userEmail)
						.queryParam("page", page)
						.queryParam("size", size)
						.build())
				.retrieve()
				.bodyToFlux(CommandBoundary.class)
				.collectList()
				.block();
	}
}
