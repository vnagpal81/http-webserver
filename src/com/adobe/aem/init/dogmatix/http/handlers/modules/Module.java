package com.adobe.aem.init.dogmatix.http.handlers.modules;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Module {
	
	String url();
	
	Setting[] settings() default {};

}
