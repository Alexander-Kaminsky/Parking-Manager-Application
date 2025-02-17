package demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import demo.service.AdminService;
import demo.model.UserBoundary;
import demo.model.CommandBoundary;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminViewController {
	private final AdminService adminService;

	public AdminViewController(AdminService adminService) {
		this.adminService = adminService;
	}

	@GetMapping
	public String showAdminDashboard() {
		return "admin/dashboard";
	}

	@GetMapping("/users")
	public String getAllUsers(@RequestParam String userSystemID,
			@RequestParam String userEmail,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			Model model) {
		try {
			List<UserBoundary> users = adminService.getAllUsers(userSystemID, userEmail, page, size);
			model.addAttribute("users", users);
			return "admin/users";
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
			return "error";
		}
	}

	@GetMapping("/commands")
	public String getAllCommands(@RequestParam String userSystemID,
			@RequestParam String userEmail,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			Model model) {
		try {
			List<CommandBoundary> commands = adminService.getAllCommands(userSystemID, userEmail, page, size);
			model.addAttribute("commands", commands);
			return "admin/commands";
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
			return "error";
		}
	}

	@PostMapping("/users/delete")
	public String deleteAllUsers(@RequestParam String userSystemID,
			@RequestParam String userEmail,
			Model model) {
		try {
			adminService.deleteAllUsers(userSystemID, userEmail);
			model.addAttribute("message", "All users deleted successfully");
			return "admin/dashboard";
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
			return "error";
		}
	}

	@PostMapping("/objects/delete")
	public String deleteAllObjects(@RequestParam String userSystemID,
			@RequestParam String userEmail,
			Model model) {
		try {
			adminService.deleteAllObjects(userSystemID, userEmail);
			model.addAttribute("message", "All objects deleted successfully");
			return "admin/dashboard";
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
			return "error";
		}
	}

	@PostMapping("/commands/delete")
	public String deleteAllCommands(@RequestParam String userSystemID,
			@RequestParam String userEmail,
			Model model) {
		try {
			adminService.deleteAllCommands(userSystemID, userEmail);
			model.addAttribute("message", "All commands deleted successfully");
			return "admin/dashboard";
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
			return "error";
		}
	}
}
