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
package com.braintribe.model.processing.shiro.bootstrapping.ini;

import java.util.LinkedHashMap;
import java.util.Map;

import com.braintribe.model.shiro.deployment.ShiroClient;

/**
 * Internal data structure that is use to convey data from the Shiro configuration deployables to the {@link ShiroIniFactory}.
 */
public class AuthClient {

	private String name;
	private Map<String,String> configuration = new LinkedHashMap<>();
	private Map<String,String> filters = new LinkedHashMap<>();
	private String urlPart;
	
	public void setClient(ShiroClient shiroClient) {
		
		this.name = shiroClient.getName();
		this.urlPart = "";
		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Map<String, String> configuration) {
		this.configuration = configuration;
	}

	public Map<String, String> getFilters() {
		return filters;
	}

	public void setFilters(Map<String, String> filters) {
		this.filters = filters;
	}

	public String getUrlPart() {
		return urlPart;
	}

	public void setUrlPart(String urlPart) {
		this.urlPart = urlPart;
	}
}
