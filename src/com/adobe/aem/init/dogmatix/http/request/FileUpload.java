package com.adobe.aem.init.dogmatix.http.request;

public class FileUpload {
	
	public FileUpload(String fileName, String type, String tempFilePath,
			int size) {
		super();
		this.fileName = fileName;
		this.type = type;
		this.tempFilePath = tempFilePath;
		this.size = size;
	}

	private String fileName;
	
	private String type;
	
	private String tempFilePath;
	
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
