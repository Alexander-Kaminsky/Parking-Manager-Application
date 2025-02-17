package demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.model.CommandBoundary;
import demo.model.ObjectId;
import demo.model.UserBoundary.UserId;
import demo.service.CommandService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/commands")
public class CommandViewController {

	private final CommandService commandService;
	private final ObjectMapper objectMapper;

	public CommandViewController(CommandService commandService) {
		this.commandService = commandService;
		this.objectMapper = new ObjectMapper();
	}

	@GetMapping("/invoke")
	public String showCommandForm(Model model) {
	    CommandBoundary command = new CommandBoundary();
	    command.setTargetObject(new CommandBoundary.TargetObject());
	    command.getTargetObject().setObjectId(new ObjectId());
	    command.setInvokedBy(new CommandBoundary.InvokedBy());
	    command.getInvokedBy().setUserId(new UserId());
	    model.addAttribute("command", command);
	    return "commands/invoke";
	}

	@PostMapping("/invoke")
	public String invokeCommand(@ModelAttribute CommandBoundary command, @RequestParam String commandAttributesJson,
			Model model) {
		try {
			if (commandAttributesJson != null && !commandAttributesJson.isEmpty()) {
				command.setCommandAttributes(objectMapper.readValue(commandAttributesJson, Map.class));
			}

			List<Object> result = commandService.invokeCommand(command);
			model.addAttribute("result", result);
			return "commands/result";
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
			return "error";
		}
	}
}
