package demo.controller;

import demo.model.ObjectBoundary;
import demo.service.ObjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/parking")
public class ParkingController {

    private final ObjectService objectService;

    public ParkingController(ObjectService objectService) {
        this.objectService = objectService;
    }

    @GetMapping("/search")
    public String showSearchPage() {
        return "parking/search";
    }

//    @GetMapping("/search/byLocation")
//    public String searchByLocation(@RequestParam double lat,
//                                    @RequestParam double lng,
//                                    @RequestParam double distance,
//                                    @RequestParam(defaultValue = "10") int size,
//                                    Model model) {
//        try {
//            // מחליף את הפרטים בערכי משתמש מחובר
//            String userSystemID = "2025a.Shir.Falach"; 
//            String userEmail = "enduser@example.com";
//
//            List<ObjectBoundary> objects = objectService.getObjectsByLocation(
//                userSystemID, userEmail, lat, lng, distance, "KM", 0, size);
//            model.addAttribute("objects", objects);
//        } catch (Exception e) {
//            model.addAttribute("error", e.getMessage());
//        }
//        return "parking/searchResults";
//    }

    @GetMapping("/search/byType")
    public String searchByType(@RequestParam String type,
                                @RequestParam(defaultValue = "10") int size,
                                Model model) {
        try {
            String userSystemID = "2025a.Shir.Falach";
            String userEmail = "enduser@example.com";

            List<ObjectBoundary> objects = objectService.getObjectsByType(
                userSystemID, userEmail, type, 0, size);
            model.addAttribute("objects", objects);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "parking/searchResults";
    }

    @GetMapping("/search/byAliasPattern")
    public String searchByAliasPattern(@RequestParam String pattern,
                                        @RequestParam(defaultValue = "10") int size,
                                        Model model) {
        try {
            String userSystemID = "2025a.Shir.Falach";
            String userEmail = "enduser@example.com";

            List<ObjectBoundary> objects = objectService.getObjectsByAliasPattern(
                userSystemID, userEmail, pattern, 0, size);
            model.addAttribute("objects", objects);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "parking/searchResults";
    }
}
