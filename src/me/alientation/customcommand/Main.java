package me.alientation.customcommand;

import org.bukkit.plugin.java.JavaPlugin;

import me.alientation.customcommand.api.CustomCommandManager;
import me.alientation.customcommand.api.CustomCommandMethods;
import me.alientation.customcommand.test.TestCustomCommand;

public class Main extends JavaPlugin {
	
	private CustomCommandManager manager;
	private CustomCommandMethods methods;
	
	public Main() {
		this.methods = new CustomCommandMethods();
		this.manager = new CustomCommandManager(this,this.methods);
		this.manager.registerPlugin(this);
	}
	
	@Override
	public void onEnable() {
		TestCustomCommand.onEnable();
	}
	
	public CustomCommandManager getManager() {
		return this.manager;
	}

}
