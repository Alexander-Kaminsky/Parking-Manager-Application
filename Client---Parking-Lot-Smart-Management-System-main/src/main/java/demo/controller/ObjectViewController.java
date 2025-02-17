package demo.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import demo.model.CreatedBy;
import demo.model.Location;
import demo.model.ObjectBoundary;
import demo.model.UserBoundary.UserId;
import demo.service.ObjectService;


@Controller
@RequestMapping("/objects")
public class ObjectViewController {
    private final ObjectService objectService;


    public ObjectViewController(ObjectService objectService) {
        this.objectService = objectService;
    }
    
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        ObjectBoundary object = new ObjectBoundary();
        object.setLocation(new Location());
        object.setCreatedBy(new CreatedBy());
        object.getCreatedBy().setUserId(new UserId());
        model.addAttribute("object", object);
        return "objects/create";
    }
    
    
    @PostMapping("/create")
    public String createObject(@ModelAttribute ObjectBoundary object, Model model) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            if (object.getObjectDetailsJson() != null && !object.getObjectDetailsJson().isEmpty()) {
                TypeReference<Map<String, Object>> typeRef = new TypeReference<>() {};
                object.setObjectDetails(mapper.readValue(object.getObjectDetailsJson(), typeRef));
            }
            
            ObjectBoundary created = objectService.createObject(object);
            model.addAttribute("message", "Object created successfully!");
            model.addAttribute("object", created);
            return "objects/details";
        } catch (ForbiddenAccessException e) {       	
            model.addAttribute("error", e.getMessage());
            return "error";
        } catch (Exception e) {
            if (e.getMessage().contains("END_USER") || e.getMessage().contains("ADMIN")) {
                model.addAttribute("error", "Only OPERATOR can create objects");
            } else {
                model.addAttribute("error", "Error creating object: " + e.getMessage());
            }
        }
            return "error";
    }

    @GetMapping("/view")
    public String viewObject(@RequestParam String systemID, 
                            @RequestParam String id,
                            @RequestParam String userSystemID,
                            @RequestParam String userEmail,
                            Model model) {
        try {
            ObjectBoundary object = objectService.getObject(systemID, id, userSystemID, userEmail);
            
            if (!object.isActive()) {
                model.addAttribute("error", "The requested parking spot is not active.");
                return "error";
            }
            model.addAttribute("object", object);
            return "objects/details";
        } catch (Exception e) {
            model.addAttribute("error", "Parking spot not found. It might not exist or is inactive.");
            return "error";
        } 
    }
    
    @GetMapping("/manage")
    public String showManagePage() {
        return "objects/manage";
    }
    
    @GetMapping("/search")
    public String showSearchPage(Model model) {
        model.addAttribute("searchTypes", Arrays.asList("type", "alias", "location", "typeAndStatus"));
        return "objects/search";
    }

    @GetMapping("/search/byType")
    public String searchByType(@RequestParam String type, String userSystemID, String userEmail, 
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size, Model model) {
        try {
            List<ObjectBoundary> objects = objectService.getObjectsByType(userSystemID, userEmail, type, page, size);
            model.addAttribute("objects", objects);
            model.addAttribute("searchType", "type");
            model.addAttribute("searchValue", type);
            return "objects/list";
        } catch (Exception e) {
            model.addAttribute("objects", List.of());
            model.addAttribute("searchType", "type");
            model.addAttribute("searchValue", type);
            return "objects/list";
        }
    }

    @GetMapping("/search/byTypeAndStatus")
    public String searchByTypeAndStatus(@RequestParam String type,
                                      @RequestParam String status,
                                      @RequestParam String userSystemID,
                                      @RequestParam String userEmail,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      Model model) {
        try {
            List<ObjectBoundary> objects = objectService.getObjectsByTypeAndStatus(
                userSystemID, userEmail, type, status, page, size);
            model.addAttribute("objects", objects);
            return "objects/list";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "objects/list";
        }
    }

    @GetMapping("/search/byAlias")  // Changed from "/search/byAlias/{alias}"
    public String searchByAlias(@RequestParam String alias,  // Changed from @PathVariable
                             @RequestParam String userSystemID,
                             @RequestParam String userEmail,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             Model model) {
        try {
            List<ObjectBoundary> objects = objectService.getObjectsByAlias(userSystemID, userEmail, alias, page, size);
            model.addAttribute("objects", objects);
            return "objects/list";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "objects/list";
        }
    }

    @GetMapping("/search/byAliasPattern") // Changed from "/search/byAliasPattern/{pattern}"
    public String searchByAliasPattern(@RequestParam String pattern, // Changed from @PathVariable
                                    @RequestParam String userSystemID,
                                    @RequestParam String userEmail,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    Model model) {
        try {
            List<ObjectBoundary> objects = objectService.getObjectsByAliasPattern(userSystemID, userEmail, pattern, page, size);
            model.addAttribute("objects", objects);
            return "objects/list";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "objects/search";
        }
    }
    
    @GetMapping("/search/byLocation")
    public String searchByLocation(@RequestParam double lat,
                                 @RequestParam double lng,
                                 @RequestParam double distance,
                                 @RequestParam(defaultValue = "NEUTRAL") String units,
                                 @RequestParam(defaultValue = "false") boolean useCircle,
                                 @RequestParam String userSystemID,
                                 @RequestParam String userEmail,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 Model model) {
        try {
            List<ObjectBoundary> objects = objectService.getObjectsByLocation(
                userSystemID, userEmail, lat, lng, distance, units, useCircle, page, size);
            model.addAttribute("objects", objects);
            return "objects/list";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "objects/list";
        }
    }
    
    @ControllerAdvice
    public class GlobalExceptionHandler {
        
        @ExceptionHandler(NotFoundException.class)
        public String handleNotFoundException(NotFoundException ex, Model model) {
            model.addAttribute("error", "Object not found. You might not have permissions to view this object or it might not exist.");
            return "error";
        }
        
        @ExceptionHandler(Exception.class)
        public String handleGeneralException(Exception ex, Model model) {
            model.addAttribute("error", "An error occurred: " + ex.getMessage());
            return "error";
        }
    }
    
    @GetMapping("/list")
    public String listObjects(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam String userSystemID,
                             @RequestParam String userEmail,
                             Model model) {
        try {
            List<ObjectBoundary> objects = objectService.getAllObjects(userSystemID, userEmail, page, size);
            model.addAttribute("objects", objects);
            return "objects/list";
        } catch (Exception e) {
            model.addAttribute("objects", List.of());
            return "objects/list";
        }
    }
    
    @GetMapping("/update/{systemID}/{id}")
    public String showUpdateForm(@PathVariable String systemID,
                               @PathVariable String id,
                               @RequestParam String userSystemID,
                               @RequestParam String userEmail,
                               Model model) {
        try {
            ObjectBoundary object = objectService.getObject(systemID, id, userSystemID, userEmail);
            model.addAttribute("object", object);
            return "objects/update";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
    
    @GetMapping("/update")
    public String showUpdateInitialForm(Model model) {
        ObjectBoundary object = new ObjectBoundary();
        object.setLocation(new Location());
        object.setCreatedBy(new CreatedBy());
        object.getCreatedBy().setUserId(new UserId());
        model.addAttribute("object", object);
        return "objects/update";
    }

    @PostMapping("/update")
    public String updateObject(@ModelAttribute ObjectBoundary object, Model model) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            if (object.getObjectDetailsJson() != null && !object.getObjectDetailsJson().isEmpty()) {
                TypeReference<Map<String, Object>> typeRef = new TypeReference<>() {};
                object.setObjectDetails(mapper.readValue(object.getObjectDetailsJson(), typeRef));
            }
            
            objectService.updateObject(
                object.getObjectId().getSystemID(),
                object.getObjectId().getId(),
                object.getCreatedBy().getUserId().getSystemID(),
                object.getCreatedBy().getUserId().getEmail(),
                object
            );
            
            model.addAttribute("message", "Object updated successfully!");
            model.addAttribute("object", object);
            return "objects/details";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
    

        
}