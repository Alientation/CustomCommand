package me.alientation.customcommand.api;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.alientation.customcommand.annotation.CommandAnnotation;
import me.alientation.customcommand.annotation.PermissionAnnotation;

public class CustomCommandMethods {
	
	private Map<String,Method> methodMap;
	private CustomCommandManager manager;
	
	public CustomCommandMethods() {
		this.methodMap = new HashMap<>();
	}
	
	public void registerManager(CustomCommandManager manager) {
		this.manager = manager;
	}
	
	public void registerMethods() throws Exception {
		for (Method method : this.getClass().getDeclaredMethods()) {
			if (method.isAnnotationPresent(CommandAnnotation.class)) {
				CommandAnnotation annotation = method.getAnnotation(CommandAnnotation.class);
				method.setAccessible(true);
				this.methodMap.put(annotation.commandID(), method);
				
				CustomCommand command = this.manager.getCustomCommandMap().get(annotation.commandID());
				
				if (command == null) {
					command = new CustomCommand(annotation.commandID(), annotation.commandName(), new ArrayList<String>(), this);
					this.manager.mapCommand(command);
				}
				
				if (method.isAnnotationPresent(PermissionAnnotation.class)) {
					for (PermissionAnnotation perm : method.getAnnotationsByType(PermissionAnnotation.class)) {
						if (perm.required())
							command.addRequiredPermission(perm.permission());
						else
							command.addPermission(perm.permission());
					}
				}
				
				command.validateMethod(method);
			}
		}
	}
	
	
}
