package me.alientation.customcommand.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ METHOD, ANNOTATION_TYPE })
@Repeatable(AliasesAnnotations.class)
public @interface AliasesAnnotation {
	String aliases();
}

@Retention(RUNTIME)
@Target({METHOD,ANNOTATION_TYPE})
@interface AliasesAnnotations {
	AliasesAnnotation[] value();
}
