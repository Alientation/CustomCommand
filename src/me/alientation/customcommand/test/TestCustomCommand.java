package me.alientation.customcommand.test;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.alientation.customcommand.annotation.CommandAliasAnnotation;
import me.alientation.customcommand.annotation.CommandAnnotation;
import me.alientation.customcommand.annotation.CommandDescriptionAnnotation;
import me.alientation.customcommand.annotation.PermissionAnnotation;
import me.alientation.customcommand.annotation.CommandTabAnnotation;
import me.alientation.customcommand.api.CustomCommandAPI;

/**
 * A test class to show the functionality of the api
 *
 */
public class TestCustomCommand extends CustomCommandAPI{
	
	@CommandAnnotation(commandID = "help", commandName = "help")
	@CommandDescriptionAnnotation("Help Command")
	@CommandAliasAnnotation("helpme")
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
	
	
	@CommandAnnotation(commandID = "help.list.add", commandName = "add")
	@PermissionAnnotation(permission = "help.add", required = true)
	@PermissionAnnotation(permission = "admin", required = false)
	public boolean helpAddCommand(CommandSender sender, Command cmd, String label, String[] args) {
		sender.sendMessage("oink");
		return true;
	}
	
	
	@CommandAnnotation(commandID = "help.hello", commandName = "hello")
	@CommandAliasAnnotation("hi")
	@PermissionAnnotation(permission = "help",required=false)
	public boolean helpHelloCommand(CommandSender sender, Command cmd, String label, String[] args) {
		sender.sendMessage("hi");
		return true;
	}
	
	
	@CommandTabAnnotation(commandID = "help.hello", commandName = "hello")
	public List<String> helpHelloTab(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = new ArrayList<String>();
		list.add("SIRI");
		return list;
	}
}
