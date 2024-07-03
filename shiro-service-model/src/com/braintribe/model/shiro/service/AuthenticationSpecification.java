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
package com.braintribe.model.shiro.service;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.SelectiveInformation;
import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.annotation.meta.Name;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@SelectiveInformation("Authentication: ${name}")
public interface AuthenticationSpecification extends GenericEntity {

	EntityType<AuthenticationSpecification> T = EntityTypes.T(AuthenticationSpecification.class);

	@Name("Name")
	@Description("Name of the authentication provider.")
	String getName();
	void setName(String name);

	@Name("Authentication URL")
	@Description("URL to initiate authentication with this provider.")
	String getAuthenticationUrl();
	void setAuthenticationUrl(String authenticationUrl);

	@Name("Image URL")
	@Description("URL to a small banner image representing the authentication provider.")
	String getImageUrl();
	void setImageUrl(String imageUrl);
}
