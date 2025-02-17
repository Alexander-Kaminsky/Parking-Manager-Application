package demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import demo.model.NewUserBoundary;
import demo.model.UserBoundary;

@Service
public class UserService {
	private final WebClient webClient;
	
	public UserService(@Value("${backend.url}") String backendUrl) {
        this.webClient = WebClient.create(backendUrl);
    }
	
	public UserBoundary createUser(NewUserBoundary newUser) {
        return webClient.post()
                .uri("/aii/users")
                .bodyValue(newUser)
                .retrieve()
                .bodyToMono(UserBoundary.class)
                .block();
    }
    
    public UserBoundary loginUser(String systemID, String userEmail) {
        return webClient.get()
                .uri("/aii/users/login/{systemID}/{userEmail}", systemID, userEmail)
                .retrieve()
                .bodyToMono(UserBoundary.class)
                .block();
    }
    
    public void updateUser(String systemID, String userEmail, UserBoundary updatedUser) {
        webClient.put()
                .uri("/aii/users/{systemID}/{userEmail}", systemID, userEmail)
                .bodyValue(updatedUser)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
