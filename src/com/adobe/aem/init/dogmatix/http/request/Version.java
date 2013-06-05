package com.adobe.aem.init.dogmatix.http.request;

public enum Version {

	VERSION_0_9("0.9"),
	VERSION_1_0("1.0"),
	VERSION_1_1("1.1");

	private String value;
	
	Version(String value) {
		this.value = value;
	}
	
	public String toString() {
		return value;
	}
	
	public static Version getVersion(String version) {
		for(Version v : values()){
	        if( v.toString().equals(version)){
	            return v;
	        }
	    }
	    return null;
	}
}
