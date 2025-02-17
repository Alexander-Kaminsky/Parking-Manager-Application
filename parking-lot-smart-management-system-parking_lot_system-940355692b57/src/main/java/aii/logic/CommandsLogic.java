package aii.logic;

import java.util.List;

import aii.boundary.CommandBoundary;
import aii.boundary.ObjectBoundary;

public interface CommandsLogic {
	
	public List<CommandBoundary> getAllCommands(int page, int size);
	
	public void deleteAllCommands();
	
	public List<Object> invokeCommand(CommandBoundary commandBoundary);
}
