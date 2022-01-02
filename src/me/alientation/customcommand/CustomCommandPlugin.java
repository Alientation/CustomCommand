package me.alientation.customcommand;

import org.bukkit.plugin.java.JavaPlugin;

import me.alientation.customcommand.api.CustomCommandManager;
import me.alientation.customcommand.test.TestCustomCommand;

public class CustomCommandPlugin extends JavaPlugin {
	
	private CustomCommandManager manager;
	
	public CustomCommandPlugin() throws Exception {
		this.manager = new CustomCommandManager(this);
	}
	
	@Override
	public void onEnable() {
		this.manager.loadCommand(new TestCustomCommand());
		this.manager.registerPlugin(this);
		this.manager.registerCommand();
	}
	
	public CustomCommandManager getManager() {
		return this.manager;
	}

}
