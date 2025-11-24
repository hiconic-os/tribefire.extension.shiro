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

import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.annotation.meta.Confidential;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.shiro.deployment.HasRolesField;
import com.braintribe.model.shiro.deployment.ShiroClient;

public interface ShiroOpenIdClient extends ShiroClient, ShiroScope, HasRolesField {

	final EntityType<ShiroOpenIdClient> T = EntityTypes.T(ShiroOpenIdClient.class);

	// Marked as confidential, as the props for the value are encrypted, e.g. SHIRO_GOOGLE_CLIENTID_ENCRYPTED 
	@Confidential
	String getClientId();
	void setClientId(String clientId);

	// Marked as confidential, as the props for the value are encrypted, e.g. SHIRO_GOOGLE_SECRET_ENCRYPTED 
	@Confidential
	String getSecret();
	void setSecret(String secret);

	String getDiscoveryUri();
	void setDiscoveryUri(String discoveryUri);

	@Initializer("true")
	Boolean getUseNonce();
	void setUseNonce(Boolean useNonce);

	void setCustomParams(Map<String, String> customParams);
	Map<String, String> getCustomParams();

	void setMetaData(ShiroOpenIdMetaData metaData);
	ShiroOpenIdMetaData getMetaData();

}
