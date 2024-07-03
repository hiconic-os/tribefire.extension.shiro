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
package com.braintribe.model.shiro.deployment;

import java.util.Set;

import com.braintribe.model.extensiondeployment.WebTerminal;
import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Login extends WebTerminal {

	final EntityType<Login> T = EntityTypes.T(Login.class);

	@Initializer("false")
	Boolean getCreateUsers();
	void setCreateUsers(Boolean createUsers);

	NewUserRoleProvider getNewUserRoleProvider();
	void setNewUserRoleProvider(NewUserRoleProvider newUserRoleProvider);

	ShiroAuthenticationConfiguration getConfiguration();
	void setConfiguration(ShiroAuthenticationConfiguration configuration);

	Set<String> getUserAcceptList();
	void setUserAcceptList(Set<String> userAcceptList);

	Set<String> getUserBlockList();
	void setUserBlockList(Set<String> userBlockList);

	/**
	 * @deprecated Use {@link #getUserAcceptList()} instead.
	 */
	@Deprecated
	Set<String> getUserWhiteList();
	/**
	 * @deprecated Use {@link #setUserAcceptList(Set)} instead.
	 */
	@Deprecated
	void setUserWhiteList(Set<String> userWhiteList);

	/**
	 * @deprecated Use {@link #getUserBlockList()} instead.
	 */
	@Deprecated
	Set<String> getUserBlackList();
	/**
	 * @deprecated Use {@link #setUserBlockList(Set)} instead.
	 */
	@Deprecated
	void setUserBlackList(Set<String> userBlackList);

	void setShowStandardLoginForm(boolean showStandardLoginForm);
	boolean getShowStandardLoginForm();

	void setShowTextLinks(boolean showTextLinks);
	boolean getShowTextLinks();

	void setAddSessionParameterOnRedirect(boolean addSessionParameterOnRedirect);
	boolean getAddSessionParameterOnRedirect();

	@Initializer("false")
	Boolean getObfuscateLogOutput();
	void setObfuscateLogOutput(Boolean obfuscateLogOutput);
}
