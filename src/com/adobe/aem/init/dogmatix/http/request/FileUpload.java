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
 * Represents a file upload in POST requests
 * 
 * @author vnagpal
 *
 */
public class FileUpload {
	
	public FileUpload(String fileName, String type, String tempFilePath,
			int size) {
		super();
		this.fileName = fileName;
		this.type = type;
		this.tempFilePath = tempFilePath;
		this.size = size;
	}

	/**
	 * Name of the uploaded file
	 */
	private String fileName;
	
	/**
	 * Mime type of the uploaded file
	 */
	private String type;
	
	/**
	 * Path of the temp file in which the contents are uploaded for the time being
	 */
	private String tempFilePath;
	
	/**
	 * Size of the uploaded file in bytes
	 */
	private int size;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTempFilePath() {
		return tempFilePath;
	}

	public void setTempFilePath(String tempFilePath) {
		this.tempFilePath = tempFilePath;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

}
