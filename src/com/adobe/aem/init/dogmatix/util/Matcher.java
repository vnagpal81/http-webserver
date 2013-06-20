package com.adobe.aem.init.dogmatix.util;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * Helper class to match and extract URL patterns in request URI against the
 * module configs
 * 
 * @author vnagpal
 * 
 */
public class Matcher {

	/**
	 * Borrow unabashedly from Spring Framework
	 */
	private static PathMatcher pathMatcher = new AntPathMatcher();

	public static boolean matches(String str, String pattern) {
		return pathMatcher.match(pattern, str);
	}

	public static String extract(String str, String pattern) {
		return pathMatcher.extractPathWithinPattern(pattern, str);
	}
	
	public static void main(String[] args) {
		System.out.println(pathMatcher.extractPathWithinPattern("files/**","files/1/2/aem.txt"));
	}
}
