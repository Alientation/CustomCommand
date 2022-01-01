package me.alientation.customcommand.api;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import me.alientation.customcommand.annotation.CommandAnnotation;
import me.alientation.customcommand.annotation.PermissionAnnotation;

public class CustomCommandMethods {
	
	private Map<String,Method> methodMap;
	private CustomCommandManager manager;
	
	public CustomCommandMethods(CustomCommandManager manager) throws Exception {
		this.manager = manager;
		this.methodMap = new HashMap<>();
	}
	
	public void registerMethods() throws Exception {
		for (Method method : this.getClass().getDeclaredMethods()) {
			if (method.isAnnotationPresent(CommandAnnotation.class)) {
				CommandAnnotation annotation = method.getAnnotation(CommandAnnotation.class);
				method.setAccessible(true);
				this.methodMap.put(annotation.commandID(), method);
				
				CustomCommand command = this.manager.getCustomCommandMap().get(annotation.commandID());
				
				if (command == null)
					throw new Exception("Invalid CommandAnnotation at " + method.toString());
				
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
