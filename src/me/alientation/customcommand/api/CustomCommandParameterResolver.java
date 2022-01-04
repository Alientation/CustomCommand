package me.alientation.customcommand.api;

/**
 * A resolver to a parameter
 *
 */
public interface CustomCommandParameterResolver<T,C extends CommandContext> {
	public T resolveParameter();
}
