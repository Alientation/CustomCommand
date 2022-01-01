package me.alientation.customcommand.test;

import me.alientation.customcommand.annotation.PermissionAnnotation;

public class TestCustomCommand {
	
	@PermissionAnnotation(required = false, permission = "")
	public static void onEnable() {
		
	}
	
}
