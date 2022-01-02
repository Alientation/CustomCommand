package me.alientation.customcommand.api;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

public class BaseCommand extends BukkitCommand {
	
	
	private boolean exists;
	private CustomCommand commandExecutor;
	
	protected BaseCommand(String name, String description, String usageMessage, List<String> aliases, CustomCommand commandExecutor) {
		super(name,description,usageMessage,aliases);
		this.commandExecutor = commandExecutor;
		this.exists = true;
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if (this.exists)
			return this.commandExecutor.onCommand(sender, this, alias, args);
		
		System.out.println("Broken link between BaseCommand and CustomCommand");
		return false;
	}
	
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
		if (this.exists)
			return this.commandExecutor.onTabComplete(sender, this, alias, args);
		
		System.out.println("Broken link between BaseCommand and CustomCommand");
		return null;
	}
	
	public void setExecutor(CustomCommand commandExecutor) {
		this.commandExecutor = commandExecutor;
	}
	
	public void kill() {
		this.exists = false;
	}
}