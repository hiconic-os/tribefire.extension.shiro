// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package com.braintribe.model.processing.shiro;

public class ShiroAuthenticationUrl {

	private String authenticationUrl;
	private String name;
	private String imageUrl;
	private String iconResourceId;

	public ShiroAuthenticationUrl(String authenticationUrl, String name, String imageUrl) {
		super();
		this.authenticationUrl = authenticationUrl;
		this.name = name;
		this.imageUrl = imageUrl;
	}

	public String getAuthenticationUrl() {
		return authenticationUrl;
	}
	public void setAuthenticationUrl(String authenticationUrl) {
		this.authenticationUrl = authenticationUrl;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getIconResourceId() {
		return iconResourceId;
	}
	public void setIconResourceId(String iconResourceId) {
		this.iconResourceId = iconResourceId;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append(" / URL: ");
		sb.append(authenticationUrl);
		sb.append(" / Image: ");
		sb.append(imageUrl);
		sb.append(" / Icon Resource: ");
		sb.append(iconResourceId);
		return sb.toString();
	}
}
