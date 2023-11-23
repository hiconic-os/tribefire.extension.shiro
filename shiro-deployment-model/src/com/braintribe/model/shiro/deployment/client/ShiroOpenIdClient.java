// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
// 
// This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License along with this library; See http://www.gnu.org/licenses/.
// ============================================================================
package com.braintribe.model.shiro.deployment.client;

import java.util.Map;

import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.shiro.deployment.ShiroClient;

public interface ShiroOpenIdClient extends ShiroClient, ShiroScope {

	final EntityType<ShiroOpenIdClient> T = EntityTypes.T(ShiroOpenIdClient.class);

	String getClientId();
	void setClientId(String clientId);

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
