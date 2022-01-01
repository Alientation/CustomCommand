package me.alientation.customcommand.test;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.alientation.customcommand.annotation.CommandAnnotation;
import me.alientation.customcommand.annotation.PermissionAnnotation;
import me.alientation.customcommand.api.CustomCommandMethods;

public class TestCustomCommand extends CustomCommandMethods{
	
	
	@CommandAnnotation(commandID = "help", commandName = "help")
	@PermissionAnnotation(permission = "help", required = false)
	public boolean helpCommand(CommandSender sender, Command cmd, String label, String[] args) {
		sender.sendMessage("No help for you loser");
		return true;
	}
	
	
	@CommandAnnotation(commandID = "help.list", commandName = "list")
	@PermissionAnnotation(permission = "help.list", required = false)
	public boolean helpListCommand(CommandSender sender, Command cmd, String label, String[] args) {
		sender.sendMessage("Help List\n-----------------------\n1) nothing\n2) helps\n------------------------");
		return true;
	}
	
	
	@CommandAnnotation(commandID = "help.add", commandName = "add")
	@PermissionAnnotation(permission = "help.add", required = true)
	@PermissionAnnotation(permission = "admin", required = false)
	public boolean helpAddCommand(CommandSender sender, Command cmd, String label, String[] args) {
		sender.sendMessage("oink");
		return true;
	}
	
}
