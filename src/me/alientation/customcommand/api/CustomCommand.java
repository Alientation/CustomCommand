package me.alientation.customcommand.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

/**
 * Class for storing information regarding a CustomCommand
 *
 */
public abstract class CustomCommand implements CommandExecutor, TabCompleter{
	
	private String commandID;		//A unique identifier for the specific command and arguments. For example the command /help list -> help.list
	private String commandName;		//The name of the command for which the player types in. For example the command /help list -> list
	
	private CustomCommand parent;				//The super command
	private List<CustomCommand> children;		//The sub commands of this parent command. All sub commands inherit the same permission requirements as the parent command
	
	private Set<String> permissions;
	private Set<String> requiredPermissions;
	
	private Method method;
	private CustomCommandMethods methods;
	
	/**
	 * Constructor that loads information regarding a specific command
	 * 
	 * @param id			String identifier of the command
	 * @param commandName	Name of the command
	 * @param permissions	Permissions the command has
	 * @throws Exception 
	 */
	public CustomCommand(String id, String commandName, Collection<String> permissions, Method method, CustomCommandMethods methods) throws Exception {
		this.commandID = id;
		this.commandName = commandName;
		this.permissions = new HashSet<>();
		this.requiredPermissions = new HashSet<>();
		this.children = new ArrayList<>();
		
		for (String perm : permissions)
			this.permissions.add(perm);
		
		validateMethod(method);
		this.methods = methods;
	}
	
	public void validateMethod(Method method) throws Exception {
		if (method.getParameterCount() != 4)
			throw new Exception("Invalid Parameter Count for method " + method.toString() + ". Required 4");
		if (method.getParameterTypes()[0] != CommandSender.class || method.getParameterTypes()[1] != Command.class || method.getParameterTypes()[2] != String.class || method.getParameterTypes()[3] != String[].class)
			throw new Exception("Invalid Parameter Type for method " + method.toString() + ". Required types (CommandSender, Command, String, String[])");
	}
	
	/**
	 * Adds a child command and updates permissions of that child
	 * 
	 * @param child	The command to be added as a children
	 */
	public void addChildCommand(CustomCommand child) {
		this.children.add(child);
		child.parent = this;
		child.addPermissions(this.permissions);
		child.addRequiredPermissions(this.requiredPermissions);
	}
	
	
	/**
	 * Check if a player has permission to use the command
	 * 
	 * @param player	The player that used the command
	 * 
	 * @return Whether the player has permission to use the command
	 */
	public boolean hasPermissions(Player player) {
		boolean hasPermissions = false;
		for (String perm : this.permissions) {
			if (!player.isPermissionSet(perm) && this.requiredPermissions.contains(perm))
				return false;
			if (player.isPermissionSet(perm))
				hasPermissions = true;
		}
		return hasPermissions;
	}
	
	/**
	 * Add a single permission
	 * 
	 * @param permission	The permission to be added to this command
	 */
	public void addPermission(String permission) {
		this.permissions.add(permission);
	}
	
	/**
	 * Add a single required permission to this command
	 * 
	 * @param permission	The required permission to be added
	 */
	public void addRequiredPermission(String permission) {
		this.requiredPermissions.add(permission);
	}
	
	/**
	 * Adds permissions to the command
	 * 
	 * @param permissions	The permissions to be added
	 */
	public void addPermissions(Collection<String> permissions) {
		for (String perm : permissions)
			addPermission(perm);
	}
	
	/**
	 * Adds required permissions to the command
	 * 
	 * @param permissions	The required permissions to be added
	 */
	public void addRequiredPermissions(Collection<String> permissions) {
		for (String perm : permissions)
			addRequiredPermission(perm);
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		CustomCommand child = getChildrenByName(args[0]);
		if (child != null)
			return child.onCommand(sender, command, label, removeFirst(args));
		Object[] params = {sender,command,label,args};
		try {
			return (boolean) this.method.invoke(this.methods,params);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length > 1)
			return getChildrenByName(args[0]).onTabComplete(sender, command, label, removeFirst(args));
		List<String> possibleCompletions = new ArrayList<>();
		for (CustomCommand commands : this.children)
			if (commands.commandName.indexOf(args[0]) == 0)
				possibleCompletions.add(commands.commandName);
		return possibleCompletions;
	}
	
	public String[] removeFirst(String[] array) {
		String[] newArray = new String[array.length-1];
		for (int i = 1; i < array.length; i++)
			newArray[i-1] = array[i];
		return newArray;
	}
	
	public CustomCommand getChildrenByName(String name) {
		for (CustomCommand cmd : this.children)
			if (cmd.commandName.equals(name))
				return cmd;
		return null;
	}
	
	public String getCommandID() {
		return this.commandID;
	}
	
	public String getCommandName() {
		return this.commandName;
	}
	
	public Set<String> getPermission() {
		return this.permissions;
	}
	
	public Set<String> getRequiredPermission() {
		return this.requiredPermissions;
	}
	
	public CustomCommand getParent() {
		return this.parent;
	}
	
	public List<CustomCommand> getChildren() {
		return this.children;
	}
	
	@Override
	public String toString() {
		return this.commandName + "@" + this.commandID;
	}
	
}
