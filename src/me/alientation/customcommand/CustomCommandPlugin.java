package me.alientation.customcommand;

import org.bukkit.plugin.java.JavaPlugin;

import me.alientation.customcommand.api.CustomCommandManager;
import me.alientation.customcommand.test.TestCustomCommand;

public class CustomCommandPlugin extends JavaPlugin {
	
	private CustomCommandManager manager;
	private TestCustomCommand methods;
	
	public CustomCommandPlugin() throws Exception {
		this.methods = new TestCustomCommand();
		this.manager = new CustomCommandManager(this,this.methods);
	}
	
	@Override
	public void onEnable() {
		this.manager.registerPlugin(this);
		try {
			this.manager.registerCommands();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public CustomCommandManager getManager() {
		return this.manager;
	}

}
