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
	 * @throws Exception 
	 */
	public void mapCommand(CustomCommand command) throws Exception {
		this.CUSTOM_COMMAND_MAP.put(command.getCommandID(),command);
		String[] parts = command.getCommandID().split(".");
		String cmdPath = parts[0];
		
		CustomCommand temp1 = this.CUSTOM_COMMAND_MAP.get(cmdPath);
		CustomCommand temp2;
		for (int i = 1; i < parts.length-1; i++) {
			temp2 = this.CUSTOM_COMMAND_MAP.get(cmdPath + "." + parts[i]);
			if (temp1 == null || temp2 == null) throw new Exception("Invalid Command Path " + command.getCommandID());
			temp1.addChildCommand(temp2);
			temp2.setParent(temp1);
			temp1 = temp2;
		}
	}
	
	/**
	 * Registers commands to the plugin
	 * @throws Exception when the plugin has not been registered to the manager
	 */
	public void registerCommands() throws Exception {
		if (this.plugin == null)
			throw new Exception("The plugin has not yet been registered to the manager (CustomCommand)");
		this.CUSTOM_COMMAND_MAP.forEach((key,value) -> plugin.getCommand(key).setExecutor(value));
		this.methods.registerMethods();
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
