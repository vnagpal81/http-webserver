/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. The ASF licenses this file to You 
 * under the Apache License, Version 2.0 (the "License"); you may not 
 * use this file except in compliance with the License.  
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.adobe.aem.init.dogmatix.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

/**
 * Utility class exposing helper methods to use Java Reflection API
 * 
 * @author vnagpal
 *
 */
public class ReflectionUtils {

	/**
	 * Looks up a method having the specified name within a class. In case of overloaded methods, returns the first method.
	 * 
	 * @param clazz The class to look into reflectively
	 * @param name Name of the method to look for
	 * @return {@link Method} object
	 * @throws NoSuchMethodException Thrown if not method exists in the class with this name
	 */
	public static Method getMethodWithExactName(Class<? extends Object> clazz, String name) throws NoSuchMethodException {
		for(Method m : clazz.getMethods()) {
			if(m.getName().equals(name)) {
				return m;
			}
		}
		throw new NoSuchMethodException();
	}
	
	/**
	 * Looks up classes annotated with the specified annotation type within the specified package
	 * 
	 * @param packagePath The package to look in for classes
	 * @param annotationClass The annotation type to look for on the classes
	 * @return List of class types
	 */
	public static List<Class<?>> getClassesWithAnnotation(String packagePath, Class<? extends Annotation> annotationClass) {
		Reflections reflections = new Reflections(packagePath);
		Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(annotationClass);
		return new ArrayList<Class<?>>(annotated);
	}
	
}
