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
package com.braintribe.model.processing.shiro.bootstrapping;

import java.util.Set;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.model.user.User;

public class FixedNewUserRolesProvider implements NewUserRoleProvider {

	private Set<String> configuredRoles;

	@Override
	public Set<String> apply(User user) {
		return configuredRoles;
	}
	
	@Configurable
	@Required
	public void setConfiguredRoles(Set<String> configuredRoles) {
		this.configuredRoles = configuredRoles;
	}

}
