package demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import demo.model.ObjectBoundary;
import java.util.List;

@Service
public class ObjectService {
    private final WebClient webClient;
    
    public ObjectService(@Value("${backend.url}") String backendUrl) {
        this.webClient = WebClient.create(backendUrl);
    }

    public ObjectBoundary createObject(ObjectBoundary object) {
        return webClient.post()
                .uri("/aii/objects")
                .bodyValue(object)
                .retrieve()
                .bodyToMono(ObjectBoundary.class)
                .block();
    }

    public ObjectBoundary getObject(String systemID, String id, String userSystemID, String userEmail) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/aii/objects/{systemID}/{id}")
                    .queryParam("userSystemID", userSystemID)
                    .queryParam("userEmail", userEmail)
                    .build(systemID, id))
                .retrieve()
                .bodyToMono(ObjectBoundary.class)
                .block();
    }

    public void updateObject(String systemID, String id, String userSystemID, 
                           String userEmail, ObjectBoundary object) {
        webClient.put()
                .uri(uriBuilder -> uriBuilder
                    .path("/aii/objects/{systemID}/{id}")
                    .queryParam("userSystemID", userSystemID)
                    .queryParam("userEmail", userEmail)
                    .build(systemID, id))
                .bodyValue(object)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public List<ObjectBoundary> getAllObjects(String userSystemID, String userEmail, int page, int size) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/aii/objects")
                    .queryParam("userSystemID", userSystemID)
                    .queryParam("userEmail", userEmail)
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .build())
                .retrieve()
                .bodyToFlux(ObjectBoundary.class)
                .collectList()
                .block();
    }

    public List<ObjectBoundary> getObjectsByType(String userSystemID, String userEmail, String type, int page, int size) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/aii/objects/search/byType/{type}")
                    .queryParam("userSystemID", userSystemID)
                    .queryParam("userEmail", userEmail)
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .build(type))
                .retrieve()
                .bodyToFlux(ObjectBoundary.class)
                .collectList()
                .block();
    }

    public List<ObjectBoundary> getObjectsByTypeAndStatus(String userSystemID, String userEmail, String type, 
                                                        String status, int page, int size) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/aii/objects/search/byTypeAndStatus/{type}/{status}")
                    .queryParam("userSystemID", userSystemID)
                    .queryParam("userEmail", userEmail)
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .build(type, status))
                .retrieve()
                .bodyToFlux(ObjectBoundary.class)
                .collectList()
                .block();
    }

    public List<ObjectBoundary> getObjectsByLocation(String userSystemID, String userEmail, double lat, 
                                                   double lng, double distance, String units, boolean useCircle, int page, int size) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/aii/objects/search/byLocation/{lat}/{lng}/{distance}")
                    .queryParam("userSystemID", userSystemID)
                    .queryParam("userEmail", userEmail)
                    .queryParam("units", units)
                    .queryParam("useCircle", useCircle)
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .build(lat, lng, distance))
                .retrieve()
                .bodyToFlux(ObjectBoundary.class)
                .collectList()
                .block();
    }

    public List<ObjectBoundary> getObjectsByAlias(String userSystemID, String userEmail, String alias, 
                                                int page, int size) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/aii/objects/search/byAlias/{alias}")
                    .queryParam("userSystemID", userSystemID)
                    .queryParam("userEmail", userEmail)
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .build(alias))
                .retrieve()
                .bodyToFlux(ObjectBoundary.class)
                .collectList()
                .block();
    }

    public List<ObjectBoundary> getObjectsByAliasPattern(String userSystemID, String userEmail, String pattern, 
                                                       int page, int size) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/aii/objects/search/byAliasPattern/{pattern}")
                    .queryParam("userSystemID", userSystemID)
                    .queryParam("userEmail", userEmail)
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .build(pattern))
                .retrieve()
                .bodyToFlux(ObjectBoundary.class)
                .collectList()
                .block();
    }
    
    

}