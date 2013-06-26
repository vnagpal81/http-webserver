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

package com.adobe.aem.init.dogmatix.http.request;

/**
 * Possible HTTP versions that the server can support
 * 
 * HTTP/0.9
 * HTTP/1.0
 * HTTP/1.1
 * 
 * @author vnagpal
 *
 */
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
