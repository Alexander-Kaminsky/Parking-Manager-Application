package demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import demo.model.NewUserBoundary;
import demo.model.UserBoundary;
import demo.service.UserService;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserViewController {

private final UserService userService;
    
    public UserViewController(UserService userService) {
        this.userService = userService;
    }
    
    // Add a root mapping to redirect to register page
    /*When user visits root URL (http://localhost:8082/)
    	Redirects them to registration page
    	*/
    @GetMapping("/")
    public String home() {
        return "home";
    }
    
    /*
     * Shows the registration form
	 * Creates empty NewUserBoundary object for the form
	 * Displays register.html template
     */
    @GetMapping("/users/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("newUser", new NewUserBoundary());
        return "register";
    }
    
    /*
     * Handles form submission from register page
     * Takes form data (@ModelAttribute) and creates NewUserBoundary
     * Calls server through userService.createUser()
     * On success: Shows userDetails page with success message
     * On error: Returns to register page with error message
     */
    @PostMapping("/users/register")
    public String registerUser(@ModelAttribute NewUserBoundary newUser, Model model) {
        try {
            UserBoundary createdUser = userService.createUser(newUser);
            model.addAttribute("message", "User created successfully!");
            model.addAttribute("user", createdUser);
            return "userDetails";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
    
    /*
     * Shows the login form page
     * Displays login.html template
     */
    @GetMapping("/users/login")
    public String showLoginForm() {
        return "login";
    }
    
    /*
     * Handles login form submission
     * Takes systemID and userEmail as parameters
     * Calls server through userService.loginUser()
     * On success: Shows userDetails page with user info
     * On error: Returns to login page with error message
     */
    @PostMapping("/users/login")
    public String loginUser(@RequestParam String systemID, 
                          @RequestParam String userEmail, 
                          Model model) {
        try {
            UserBoundary user = userService.loginUser(systemID, userEmail);
            model.addAttribute("user", user);
            return "userDetails";
        } catch (Exception e) {
            model.addAttribute("error", "User not found. Please check your System ID and Email.");
            return "login";
        }
    }
      
    /*
     * Functionality to update an existing user through the top-navigator bar
     */
    @GetMapping("/users/update")
    public String showUpdateForm(Model model) {
        model.addAttribute("user", new UserBoundary());  // Empty user for the form
        return "updateDirect";
    }

    @PostMapping("/users/update-direct")
    public String updateUserDirect(@RequestParam String systemID,
                                 @RequestParam String userEmail,
                                 @ModelAttribute UserBoundary updatedUser,
                                 Model model) {
        try {
            userService.updateUser(systemID, userEmail, updatedUser);
            model.addAttribute("message", "User updated successfully!");
            model.addAttribute("user", updatedUser);
            return "userDetails";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "updateDirect";
        }
    }
    
    /*
     * Functionality to update an existing user through the user details page
     */
    @GetMapping("/users/update/{systemID}/{userEmail}")
    public String showUpdateForm(@PathVariable String systemID,
                               @PathVariable String userEmail,
                               Model model) {
        try {
            // First get the current user data
            UserBoundary currentUser = userService.loginUser(systemID, userEmail);
            model.addAttribute("user", currentUser);
            model.addAttribute("systemID", systemID);
            model.addAttribute("userEmail", userEmail);
            return "updateUser";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @PostMapping("/users/update/{systemID}/{userEmail}")
    public String updateUser(@PathVariable String systemID,
                            @PathVariable String userEmail,
                            @ModelAttribute UserBoundary updatedUser,
                            Model model) {
        try {
            userService.updateUser(systemID, userEmail, updatedUser);
            model.addAttribute("message", "User updated successfully!");
            model.addAttribute("user", updatedUser);
            return "userDetails";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "updateUser";
        }
    }
    
    @GetMapping("/dashboard")
    public String redirectToDashboard(@RequestParam String systemID, @RequestParam String userEmail, Model model) {
        try {
            UserBoundary user = userService.loginUser(systemID, userEmail);
            switch (user.getRole()) {
                case "ADMIN":
                    return "redirect:/admin";
                case "OPERATOR":
                    return "redirect:/objects/manage";
                case "END_USER":
                    return "redirect:/parking/search";
                default:
                    throw new IllegalArgumentException("Unknown role: " + user.getRole());
            }
        } catch (Exception e) {
            model.addAttribute("error", "Failed to determine dashboard: " + e.getMessage());
            return "error";
        }
    }

    
    
}
