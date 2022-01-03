package me.alientation.customcommand.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.alientation.customcommand.api.CustomCommand;

public class CommandCallSuccessEvent extends Event{
	
	private static final HandlerList HANDLER = new HandlerList();
	
	private CustomCommand commandCalled;
	
	public CommandCallSuccessEvent(CustomCommand command) {
		this.commandCalled = command;
	}
	
	public CustomCommand getCommand() {
		return this.commandCalled;
	}
	
	@Override
	public HandlerList getHandlers() {
		return HANDLER;
	}

}
