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
package tribefire.extension.shiro.templates.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.shiro.deployment.FieldEncoding;

import tribefire.extension.templates.api.TemplateContext;

public interface ShiroTemplateContext extends TemplateContext {

	static ShiroTemplateContextBuilder builder() {
		return new ShiroTemplateContextImpl();
	}

	boolean getGoogleEnabled();
	String getGoogleClientId();
	String getGoogleSecret();

	boolean getAzureEnabled();
	String getAzureClientId();
	String getAzureSecret();
	String getAzureTenant();

	boolean getTwitterEnabled();
	String getTwitterKey();
	String getTwitterSecret();

	boolean getFacebookEnabled();
	String getFacebookKey();
	String getFacebookSecret();

	boolean getGithubEnabled();
	String getGithubKey();
	String getGithubSecret();

	boolean getCognitoEnabled();
	String getCognitoClientId();
	String getCognitoSecret();
	String getCognitoRegion();
	String getCognitoUserPoolId();
	boolean getCognitoExclusiveRoleProvider();

	boolean getOktaEnabled();
	String getOktaClientId();
	String getOktaSecret();
	String getOktaDiscoveryUrl();
	boolean getOktaExclusiveRoleProvider();
	String getOktaRolesField();
	FieldEncoding getOktaRolesFieldEncoding();

	boolean getInstagramEnabled();
	String getInstagramClientId();
	String getInstagramSecret();
	String getInstagramAuthUrl();
	String getInstagramTokenUrl();
	String getInstagramProfileUrl();
	String getInstagramUserInformationUrl();
	String getInstagramUsernamePattern();
	String getInstagramScope();

	List<String> getUserRolesMapField();
	Map<Set<String>, Set<String>> getUserRolesMap();
	Set<String> getAcceptList();
	Set<String> getBlockList();
	boolean getCreateUsers();
	String getPublicServicesUrl();
	String getCallbackUrl();
	String getUnauthorizedUrl();
	String getUnauthenticatedUrl();
	String getFallbackUrl();
	String getRedirectUrl();
	boolean getAddSessionParameterOnRedirect();
	String getLoginDomain();
	Map<String, String> getCustomParameters();
	boolean getShowStandardLoginForm();
	boolean getShowTextLinks();

	Boolean getObfuscateLogOutput();
	Set<String> getFixedUserRolesForNewUsers();

}