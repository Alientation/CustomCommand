package me.alientation.customcommand.api;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import me.alientation.customcommand.exception.PluginNotFoundException;


/**
 * Class for managing CustomCommands
 */
public class CustomCommandManager {
	
	private Map<String,CustomCommand> CUSTOM_COMMAND_MAP;
	
	private JavaPlugin plugin;
	
	/**
	 * Constructor that initiates Java Reflection to map each command to a method
	 * 
	 * @param plugin		Plugin	
	 * @param methods		A reference to the class that contains the methods the commands uses
	 * @throws Exception
	 */
	public CustomCommandManager(JavaPlugin plugin) {
		this.CUSTOM_COMMAND_MAP = new HashMap<>();
		this.plugin = plugin;
	}
	
	
	/**
	 * Registers the command to the manager
	 * 
	 * @param command		The target command to be registered
	 * @throws Exception 
	 */
	public void mapCommand(CustomCommand command) {
		this.CUSTOM_COMMAND_MAP.put(command.getCommandID(),command);
		String[] parts = command.getCommandID().split("\\.");
		System.out.println(command.getCommandID() + " \n" + Arrays.toString(parts));
		String cmdPath = parts[0];
		
		CustomCommand temp1 = this.CUSTOM_COMMAND_MAP.get(cmdPath);
		if (temp1 == null) {
			temp1 = new CustomCommand(cmdPath,cmdPath,new ArrayList<String>(),this);
			this.CUSTOM_COMMAND_MAP.put(temp1.getCommandID(), temp1);
		}
		CustomCommand temp2;
		for (int i = 1; i < parts.length; i++) {
			temp2 = this.CUSTOM_COMMAND_MAP.get(cmdPath + "." + parts[i]);
			if (temp2 == null) {
				temp2 = new CustomCommand(cmdPath + "." + parts[i],parts[i],new ArrayList<String>(),this);
				this.CUSTOM_COMMAND_MAP.put(temp2.getCommandID(), temp2);
			}
			cmdPath += "." + parts[i];
			temp1.addChildCommand(temp2);
			temp2.setParent(temp1);
			temp1 = temp2;
		}
	}
	
	/**
	 * Sets up command executors for the plugin
	 * 
	 * @param commands	An object to the class that contains the command methods
	 * @throws Exception 
	 */
	public void loadCommand(CustomCommandAPI commands) {
		commands.registerManager(this);
		commands.registerMethods();
	}
	
	
	public void registerCommand() {
		try {
			if (this.plugin == null)
				throw new PluginNotFoundException("The plugin has not yet been registered to the manager (CustomCommand)");
		
			this.CUSTOM_COMMAND_MAP.forEach((key,value) -> {
				if (value.isParent()) {
					System.out.println("COMMAND >>> " + value.getCommandName());
					Field bukkitCommandMap;
					try {
						bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
						bukkitCommandMap.setAccessible(true);
						CommandMap commandMap = ((CommandMap) bukkitCommandMap.get(Bukkit.getServer()));
						if (commandMap.getCommand(key) instanceof BaseCommand) {
							((BaseCommand) commandMap.getCommand(key)).setExecutor(value);
						} else {
							System.out.println(key + " is an invalid command as it isn't an instance of BaseCommand");
						}
					} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (PluginNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	public void registerPlugin(JavaPlugin pl) {
		this.plugin = pl;
	}
	
	
	public Map<String,CustomCommand> getCustomCommandMap() {
		return this.CUSTOM_COMMAND_MAP;
	}
	
	public JavaPlugin getPlugin() {
		return this.plugin;
	}
}
