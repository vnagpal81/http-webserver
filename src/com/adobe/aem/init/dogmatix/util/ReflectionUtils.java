package com.adobe.aem.init.dogmatix.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

public class ReflectionUtils {

	public static Method getMethodWithExactName(Class<? extends Object> clazz, String name) throws NoSuchMethodException {
		for(Method m : clazz.getMethods()) {
			if(m.getName().equals(name)) {
				return m;
			}
		}
		throw new NoSuchMethodException();
	}
	
	public static List<Class<?>> getClassesWithAnnotation(String packagePath, Class<? extends Annotation> annotationClass) {
		Reflections reflections = new Reflections(packagePath);
		Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(annotationClass);
		return new ArrayList<Class<?>>(annotated);
	}
	
}
