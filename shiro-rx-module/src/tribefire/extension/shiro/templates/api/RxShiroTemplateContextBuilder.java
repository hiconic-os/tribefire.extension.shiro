// ============================================================================
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

import hiconic.rx.access.module.api.AccessContract;

public interface RxShiroTemplateContextBuilder {

	RxShiroTemplateContextBuilder setName(String name);
	RxShiroTemplateContextBuilder setIdPrefix(String idPrefix);

	RxShiroTemplateContextBuilder setServletPath(String servletPath);
	
	RxShiroTemplateContextBuilder setAccessContract(AccessContract accessContract);
	RxShiroTemplateContextBuilder setAuthAccessId(String authAccessId);

	RxShiroTemplateContextBuilder setGoogleEnabled(boolean googleEnabled);
	RxShiroTemplateContextBuilder setGoogleClientId(String googleClientId);
	RxShiroTemplateContextBuilder setGoogleSecret(String googleSecret);

	RxShiroTemplateContextBuilder setAzureEnabled(boolean azureEnabled);
	RxShiroTemplateContextBuilder setAzureClientId(String azureClientId);
	RxShiroTemplateContextBuilder setAzureSecret(String azureSecret);
	RxShiroTemplateContextBuilder setAzureTenant(String azureTenant);

	RxShiroTemplateContextBuilder setTwitterEnabled(boolean twitterEnabled);
	RxShiroTemplateContextBuilder setTwitterKey(String twitterKey);
	RxShiroTemplateContextBuilder setTwitterSecret(String twitterSecret);

	RxShiroTemplateContextBuilder setFacebookEnabled(boolean facebookEnabled);
	RxShiroTemplateContextBuilder setFacebookKey(String facebookKey);
	RxShiroTemplateContextBuilder setFacebookSecret(String facebookSecret);

	RxShiroTemplateContextBuilder setGithubEnabled(boolean githubEnabled);
	RxShiroTemplateContextBuilder setGithubKey(String githubKey);
	RxShiroTemplateContextBuilder setGithubSecret(String githubSecret);

	RxShiroTemplateContextBuilder setCognitoEnabled(boolean cognitoEnabled);
	RxShiroTemplateContextBuilder setCognitoClientId(String cognitoClientId);
	RxShiroTemplateContextBuilder setCognitoSecret(String cognitoSecret);
	RxShiroTemplateContextBuilder setCognitoRegion(String cognitoRegion);
	RxShiroTemplateContextBuilder setCognitoUserPoolId(String cognitoUserPoolId);
	RxShiroTemplateContextBuilder setCognitoExclusiveRoleProvider(boolean cognitoExclusiveRoleProvider);

	RxShiroTemplateContextBuilder setOktaEnabled(boolean oktaEnabled);
	RxShiroTemplateContextBuilder setOktaClientId(String oktaClientId);
	RxShiroTemplateContextBuilder setOktaSecret(String oktaSecret);
	RxShiroTemplateContextBuilder setOktaDiscoveryUrl(String oktaDiscoveryUrl);
	RxShiroTemplateContextBuilder setOktaExclusiveRoleProvider(boolean oktaExclusiveRoleProvider);
	RxShiroTemplateContextBuilder setOktaRolesField(String oktaRolesField);
	RxShiroTemplateContextBuilder setOktaRolesFieldEncoding(FieldEncoding oktaRolesFieldEncoding);
	RxShiroTemplateContextBuilder setOktaIncludeAccessTokenClaimsInProfile(Boolean oktaIncludeAccessTokenClaimsInProfile);

	RxShiroTemplateContextBuilder setInstagramEnabled(boolean instagramEnabled);
	RxShiroTemplateContextBuilder setInstagramClientId(String instagramClientId);
	RxShiroTemplateContextBuilder setInstagramSecret(String instagramSecret);
	RxShiroTemplateContextBuilder setInstagramAuthUrl(String instagramAuthUrl);
	RxShiroTemplateContextBuilder setInstagramTokenUrl(String instagramTokenUrl);
	RxShiroTemplateContextBuilder setInstagramProfileUrl(String instagramProfileUrl);
	RxShiroTemplateContextBuilder setInstagramUserInformationUrl(String instagramUserInformationUrl);
	RxShiroTemplateContextBuilder setInstagramUsernamePattern(String instagramUsernamePattern);
	RxShiroTemplateContextBuilder setInstagramScope(String instagramScope);

	RxShiroTemplateContextBuilder setUserRolesMapField(List<String> userRolesMapField);
	RxShiroTemplateContextBuilder setUserRolesMap(Map<Set<String>, Set<String>> userRolesMap);
	RxShiroTemplateContextBuilder setAcceptList(Set<String> acceptList);
	RxShiroTemplateContextBuilder setBlockList(Set<String> blockList);
	RxShiroTemplateContextBuilder setCreateUsers(boolean createUsers);
	RxShiroTemplateContextBuilder setPublicServicesUrl(String publicServicesUrl);
	RxShiroTemplateContextBuilder setCallbackUrl(String callbackUrl);
	RxShiroTemplateContextBuilder setUnauthorizedUrl(String unauthorizedUrl);
	RxShiroTemplateContextBuilder setUnauthenticatedUrl(String unauthenticatedUrl);
	RxShiroTemplateContextBuilder setRedirectUrl(String redirectUrl);
	RxShiroTemplateContextBuilder setFallbackUrl(String fallbackUrl);
	RxShiroTemplateContextBuilder setAddSessionParameterOnRedirect(boolean addSessionParameterOnRedirect);
	RxShiroTemplateContextBuilder setLoginDomain(String loginDomain);
	RxShiroTemplateContextBuilder setCustomParameters(Map<String, String> customParameters);
	RxShiroTemplateContextBuilder setShowStandardLoginForm(boolean showStandardLoginForm);
	RxShiroTemplateContextBuilder setShowTextLinks(boolean showTextLinks);

	RxShiroTemplateContextBuilder setObfuscateLogOutput(boolean obfuscateLogOutput);

	RxShiroTemplateContextBuilder setFixedUserRolesForNewUsers(Set<String> fixedUserRolesForNewUsers);

	RxShiroTemplateContext build();

}