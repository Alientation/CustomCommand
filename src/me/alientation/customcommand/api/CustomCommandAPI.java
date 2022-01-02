package me.alientation.customcommand.api;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

import me.alientation.customcommand.annotation.AliasesAnnotation;
import me.alientation.customcommand.annotation.CommandAnnotation;
import me.alientation.customcommand.annotation.DescriptionAnnotation;
import me.alientation.customcommand.annotation.PermissionAnnotation;
import me.alientation.customcommand.annotation.TabAnnotation;
import me.alientation.customcommand.annotation.UsageAnnotation;

public class CustomCommandAPI {
	
	private Map<String,Method> methodMap;
	private CustomCommandManager manager;
	
	public CustomCommandAPI() {
		this.methodMap = new HashMap<>();
	}
	
	public CustomCommandAPI(CustomCommandManager manager) {
		this.methodMap = new HashMap<>();
		registerManager(manager);
	}
	
	public void registerManager(CustomCommandManager manager) {
		this.manager = manager;
	}
	
	public void registerMethods() {
		for (Method method : this.getClass().getDeclaredMethods()) {
			if (method.isAnnotationPresent(CommandAnnotation.class)) {
				CommandAnnotation commandAnnotation = method.getAnnotation(CommandAnnotation.class);
				AliasesAnnotation[] aliasesAnnotations = method.getAnnotationsByType(AliasesAnnotation.class);
				DescriptionAnnotation descriptionAnnotation = method.getAnnotation(DescriptionAnnotation.class);
				PermissionAnnotation[] permissionAnnotations = method.getAnnotationsByType(PermissionAnnotation.class);
				UsageAnnotation usageAnnotation = method.getAnnotation(UsageAnnotation.class);
				
				String commandName = commandAnnotation != null ? commandAnnotation.commandName() : null;
				String commandID = commandAnnotation != null ? commandAnnotation.commandID() : null;
				
				List<String> commandAliases = new ArrayList<>();
				for (int i = 0; i < aliasesAnnotations.length; i++)
					commandAliases.add(aliasesAnnotations[i].value());
				
				String commandDescription = descriptionAnnotation != null ? descriptionAnnotation.value() : null;
				
				List<String> commandPermissions = new ArrayList<>();
				List<Boolean> commandRequiredPermissions = new ArrayList<>();
				for (int i = 0; i < permissionAnnotations.length; i++) {
					commandPermissions.add(permissionAnnotations[i].permission());
					commandRequiredPermissions.add(permissionAnnotations[i].required());
				}
				
				String commandUsage = usageAnnotation != null ? usageAnnotation.value() : null;
				
				
				this.methodMap.put("@commandAnnotation@" + commandAnnotation.commandID(), method);
				
				CustomCommand command = this.getCommand(commandID, commandName);
				
				command.addAliases(commandAliases);
				
				for (int i = 0; i < commandPermissions.size(); i++)
					command.addPermission(commandPermissions.get(i),commandRequiredPermissions.get(i));
				
				if (command.isParent()) {
					Field bukkitCommandMap;
					try {
						bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
						bukkitCommandMap.setAccessible(true);
						CommandMap commandMap = ((CommandMap) bukkitCommandMap.get(Bukkit.getServer()));
						commandMap.register(command.getCommandName(), new BaseCommand(commandName,commandDescription, commandUsage, commandAliases, command));
						if (this.manager.getPlugin().getCommand(commandName) != null) {
							this.manager.getPlugin().getCommand(commandName).unregister(commandMap);
						}
					} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				
				command.validateCommandMethod(method,this);
			} else if (method.isAnnotationPresent(TabAnnotation.class)) {
				TabAnnotation tabAnnotation = method.getAnnotation(TabAnnotation.class);
				this.methodMap.put("@tabAnnotation@" + tabAnnotation.commandID(), method);
				CustomCommand command = this.getCommand(tabAnnotation.commandID(), tabAnnotation.commandName());
				command.validateTabMethod(method, this);
			}
		}
	}
	
	private CustomCommand getCommand(String commandID, String commandName) {
		CustomCommand command = this.manager.getCustomCommandMap().get(commandID);
		if (command == null) {
			command = new CustomCommand(commandID,commandName,new ArrayList<String>());
			this.manager.mapCommand(command);
		}
		return command;
	}
	
	public Map<String,Method> getMethodMap() {
		return this.methodMap;
	}
}