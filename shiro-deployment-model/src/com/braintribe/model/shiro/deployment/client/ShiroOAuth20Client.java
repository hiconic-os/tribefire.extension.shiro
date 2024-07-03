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
package com.braintribe.model.shiro.deployment.client;

import java.util.Map;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.shiro.deployment.ShiroClient;

@Abstract
public interface ShiroOAuth20Client extends ShiroClient {

	final EntityType<ShiroOAuth20Client> T = EntityTypes.T(ShiroOAuth20Client.class);
	
	String getKey();
	void setKey(String key);
	
	String getSecret();
	void setSecret(String secret);

	void setCustomParams(Map<String, String> customParams);
	Map<String,String> getCustomParams();
	
}
