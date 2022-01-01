package me.alientation.customcommand.api;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;


/**
 * Class for managing CustomCommands
 */
public class CustomCommandManager {
	
	private Map<String,CustomCommand> CUSTOM_COMMAND_MAP;
	
	private JavaPlugin plugin;
	
	private CustomCommandMethods methods;
	
	public CustomCommandManager(JavaPlugin plugin, CustomCommandMethods methods) {
		this.CUSTOM_COMMAND_MAP = new HashMap<>();
		this.plugin = plugin;
		this.methods = methods;
	}
	
	
	/**
	 * Registers the command to the manager
	 * 
	 * @param command		The target command to be registered
	 */
	public void mapCommand(CustomCommand command) {
		this.CUSTOM_COMMAND_MAP.put(command.getCommandID(),command);
	}
	
	/**
	 * Registers commands to the plugin
	 * @throws Exception when the plugin has not been registered to the manager
	 */
	public void registerCommands() throws Exception {
		if (this.plugin == null)
			throw new Exception("The plugin has not yet been registered to the manager (CustomCommand)");
		this.CUSTOM_COMMAND_MAP.forEach((key,value) -> plugin.getCommand(key).setExecutor(value));
	}
	
	/**
	 * Registers the plugin to the manager
	 * 
	 * @param pl	The JavaPlugin using the manager
	 */
	public void registerPlugin(JavaPlugin pl) {
		this.plugin = pl;
	}
	
	
	public Map<String,CustomCommand> getCustomCommandMap() {
		return this.CUSTOM_COMMAND_MAP;
	}
	
	public CustomCommandMethods getMethods() {
		return this.methods;
	}
}
