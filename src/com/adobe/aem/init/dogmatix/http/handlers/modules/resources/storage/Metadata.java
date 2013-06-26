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

package com.adobe.aem.init.dogmatix.http.handlers.modules.resources.storage;

/**
 * Metadata information about a path in a file system consisting of:
 * 
 * Name			:	The path which the metadata is about
 * Size			:	If the path points to a file, size is set to a 
 * 					non-zero value representing file size in bytes 
 * Type			:	If the path points to a file, Mimetype of the file
 * Owner		:	Creator's username in the file system
 * Created On	:	Timestamp of creation in the file system
 * Modified On	:	Timestamp of the last modification event
 * Leaf Node	:	True, if the path represents a file.
 * 					False, if the path represents a directory.
 * 
 * @author vnagpal
 *
 */
public class Metadata {
	
	private String name;

	private long size;
	
	private String type;
	
	private String owner;
	
	private long createdOn;
	
	private long lastModifiedOn;
	
	private boolean leafNode;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	public long getLastModifiedOn() {
		return lastModifiedOn;
	}

	public void setLastModifiedOn(long lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
	}

	public boolean isLeafNode() {
		return leafNode;
	}

	public void setLeafNode(boolean leafNode) {
		this.leafNode = leafNode;
	}
	
}
