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

import me.alientation.customcommand.exception.InvalidMethodException;

/**
 * Class for storing information regarding a CustomCommand
 *
 */
public class CustomCommand implements CommandExecutor, TabCompleter{
	
	private String commandID;		//A unique identifier for the specific command. For example the command /help list -> help.list
	private String commandName;		//The name of the command for which the player types in. For example the command /help list -> list
	private List<String> commandAliases;
	
	private CustomCommand parent;				//The super command
	private Set<CustomCommand> children;		//The sub commands of this parent command. All sub commands inherit the same permission requirements as the parent command
	
	private Set<String> permissions;
	private Set<String> requiredPermissions;
	
	private Method commandMethod;
	private Method tabMethod;
	private CustomCommandMethods commandMethodContainer;
	private CustomCommandMethods tabMethodContainer;
	
	private BaseCommand baseCommand;
	
	
	private boolean showAliasesInTabCompletion = true;
	
	/**
	 * Constructor that loads information regarding a specific command
	 * 
	 * @param id			String identifier of the command
	 * @param commandName	Name of the command
	 * @param permissions	Permissions the command has
	 * @throws Exception 
	 */
	public CustomCommand(String id, String commandName, Collection<String> permissions) {
		this.commandID = id;
		this.commandName = commandName;
		this.permissions = new HashSet<>();
		this.requiredPermissions = new HashSet<>();
		this.children = new HashSet<>();
		
		for (String perm : permissions)
			this.permissions.add(perm);
		
		this.commandAliases = new ArrayList<String>();
	}
	
	/**
	 * Constructor that loads in additional information regarding a specific command
	 * 
	 * @param id			String identifier of the command
	 * @param commandName	Name of the command
	 * @param aliases		Aliases the command has
	 * @param permissions	Permissions the command has
	 * @throws Exception
	 */
	public CustomCommand(String id, String commandName, List<String> aliases, Collection<String> permissions) {
		this(id,commandName,permissions);
		this.commandAliases= aliases;
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!hasPermissions(sender)) {
			invalidPermissions(sender,command,label,args);
			return false;
		}
		
		CustomCommand child = args.length > 0 ? getChildrenByName(args[0]) : null;
		if (child != null)
			return child.onCommand(sender, command, label, removeFirst(args));
		if (this.commandMethod == null) {
			invalidCommand(sender,command,label,args);
			return false;
		}
		
		//TODO: Figure out a way to conform the onCommand params into the user defined method
		
		Object[] params = {sender,command,label,args};
		try {
			return (boolean) this.commandMethod.invoke(this.commandMethodContainer,params);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (this.tabMethod != null) {
			try {
				Object[] params = {sender,command,label,args};
				return (List<String>) this.tabMethod.invoke(this.tabMethodContainer,params);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
				e.printStackTrace();
			}
		}
		
		if (args.length > 1) {
			CustomCommand child = getChildrenByName(args[0]);
			if (child == null)
				return null;
			return getChildrenByName(args[0]).onTabComplete(sender, command, label, removeFirst(args));
		}
		List<String> possibleCompletions = new ArrayList<>();
		for (CustomCommand commands : this.children) {
			if (commands.hasPermissions(sender) && commands.commandName.indexOf(args[0]) == 0)
				possibleCompletions.add(commands.commandName);
			if (showAliasesInTabCompletion)
				for (String s : commands.commandAliases)
					if (s.indexOf(args[0]) == 0)
						possibleCompletions.add(s);
		}
		return possibleCompletions;
	}
	
	/**
	 * Called whenever a command issued by a sender does not exist, likely meaning they had incorrect arguments
	 * 
	 * @param sender	Issuer of the command
	 * @param command	Command that was issued
	 * @param label		Text that was typed
	 * @param args		Arguments passed to the command
	 */
	public void invalidCommand(CommandSender sender, Command command, String label, String[] args) {
		
	}
	
	/**
	 * Called whenever a sender does not have the appropriate permissions to execute a command
	 * 
	 * @param sender	Issuer of the command
	 * @param command	Command that was issued
	 * @param label		Text that was typed
	 * @param args		Arguments passed to the command
	 */
	public void invalidPermissions(CommandSender sender, Command command, String label, String[] args) {
		
	}
	
	/**
	 * Remove the first element of an array
	 * 
	 * @param array		original array
	 * @return 			an array[N-1] with the first element removed from the original
	 */
	private String[] removeFirst(String[] array) {
		String[] newArray = new String[array.length-1];
		for (int i = 1; i < array.length; i++)
			newArray[i-1] = array[i];
		return newArray;
	}
	
	
	/**
	 * Temporary barebones validation for a method
	 * 
	 * @param method		The method to be validated
	 * @throws Exception
	 */
	public void validateCommandMethod(Method method, CustomCommandMethods methodObject) throws InvalidMethodException {
		if (method.getParameterCount() != 4)
			throw new InvalidMethodException("Invalid Parameter Count for method " + method.toString() + ". Required 4");
		if (method.getParameterTypes()[0] != CommandSender.class || method.getParameterTypes()[1] != Command.class || method.getParameterTypes()[2] != String.class || method.getParameterTypes()[3] != String[].class)
			throw new InvalidMethodException("Invalid Parameter Type for method " + method.toString() + ". Required types (CommandSender, Command, String, String[])");
		if (methodObject.getMethodMap().get("@commandAnnotation@" + this.getCommandID()) != method)
			throw new InvalidMethodException("Method is not contained within the supplied methodObject > " + method.toString());
		this.commandMethod = method;
		this.commandMethodContainer = methodObject;
	}
	
	
	/**
	 * Temporary barebones validation for a method
	 * 
	 * @param method		The method to be validated
	 * @throws Exception
	 */
	public void validateTabMethod(Method method, CustomCommandMethods methodObject) throws InvalidMethodException {
		if (method.getParameterCount() != 4)
			throw new InvalidMethodException("Invalid Parameter Count for method " + method.toString() + ". Required 4");
		if (method.getParameterTypes()[0] != CommandSender.class || method.getParameterTypes()[1] != Command.class || method.getParameterTypes()[2] != String.class || method.getParameterTypes()[3] != String[].class)
			throw new InvalidMethodException("Invalid Parameter Type for method " + method.toString() + ". Required types (CommandSender, Command, String, String[])");
		if (methodObject.getMethodMap().get("@tabAnnotation@" + this.getCommandID()) != method)
			throw new InvalidMethodException("Method is not contained within the supplied methodObject > " + method.toString());
		this.tabMethod = method;
		this.tabMethodContainer = methodObject;
	}
	
	
	/**
	 * Check if a command sender has permission to use the command
	 * 
	 * @param sender	The command sender that used the command
	 * 
	 * @return Whether the command sender has permission to use the command
	 */
	public boolean hasPermissions(CommandSender sender) {
		for (String reqPerm : this.requiredPermissions)
			if (!sender.isPermissionSet(reqPerm))
				return false;
		for (String perm : this.permissions)
			if (sender.isPermissionSet(perm))
				return true;
		return false;
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
	 * Assigns the permission to the appropriate container
	 *
	 * @param permission	The name of the permission to be added
	 * @param isRequired	Whether the permission is required for the command to execute
	 */
	public void addPermission(String permission, boolean isRequired) {
		if (isRequired)
			addRequiredPermission(permission);
		else
			addPermission(permission);
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
	
	/**
	 * Kills the previous baseCommand as a safety check and sets a new one
	 * 
	 * @param baseCommand	new baseCommand
	 */
	public void setBaseCommand(BaseCommand baseCommand) {
		if (this.baseCommand != null) this.baseCommand.kill();
		this.baseCommand = baseCommand;
		this.baseCommand.setExecutor(this);
	}
	
	
	/**
	 * Adds a child command and updates permissions of that child
	 * 
	 * @param child	The command to be added as a children
	 */
	public void addChildCommand(CustomCommand child) {
		this.children.add(child);
		child.parent = this;
	}
	
	
	/**
	 * Searches for a child command
	 * 
	 * @param 	name
	 * @return	The child command object or null if it does not exist
	 */
	public CustomCommand getChildrenByName(String name) {
		for (CustomCommand cmd : this.children)
			if (cmd.commandName.equals(name) || cmd.commandAliases.contains(name))
				return cmd;
		return null;
	}
	
	
	public void addAlias(String alias) {
		this.commandAliases.add(alias);
	}
	
	public void addAliases(Collection<String> aliases) {
		for (String s : aliases)
			addAlias(s);
	}
	
	public List<String> getAliases() {
		return this.commandAliases;
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
	
	public void setParent(CustomCommand parent) {
		this.parent = parent;
	}
	
	public boolean isParent() {
		return this.parent == null;
	}
	
	public Set<CustomCommand> getChildren() {
		return this.children;
	}
	
	public boolean hasMethod() {
		return this.commandMethod != null;
	}
	
	@Override
	public String toString() {
		return this.commandName + "@" + this.commandID;
	}
	
}
