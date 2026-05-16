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
package tribefire.extension.shiro.configuration.model;

import java.util.List;
import java.util.Set;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.shiro.deployment.FieldEncoding;

public interface ShiroConfiguration extends GenericEntity {

	EntityType<ShiroConfiguration> T = EntityTypes.T(ShiroConfiguration.class);

	// General

	@Initializer("true")
	boolean getEnabled();
	void setEnabled(boolean enabled);

	@Initializer("'auth'")
	String getUserAccessId();
	void setUserAccessId(String userAccessId);

	@Initializer("'remote-login'")
	String getServletPath();
	void setServletPath(String servletPath);
	
	// Google

	boolean getEnableGoogle();
	void setEnableGoogle(boolean enableGoogle);

	String getGoogleClientId();
	void setGoogleClientId(String googleClientId);

	String getGoogleSecret();
	void setGoogleSecret(String googleSecret);

	// Azure AD

	boolean getEnableAzureAd();
	void setEnableAzureAd(boolean enableAzureAd);

	String getAzureAdClientId();
	void setAzureAdClientId(String azureAdClientId);

	String getAzureAdSecret();
	void setAzureAdSecret(String azureAdSecret);

	String getAzureAdTenant();
	void setAzureAdTenant(String azureAdTenant);

	// Twitter

	boolean getEnableTwitter();
	void setEnableTwitter(boolean enableTwitter);

	String getTwitterKey();
	void setTwitterKey(String twitterKey);

	String getTwitterSecret();
	void setTwitterSecret(String twitterSecret);

	// Facebook

	boolean getEnableFacebook();
	void setEnableFacebook(boolean enableFacebook);

	String getFacebookKey();
	void setFacebookKey(String facebookKey);

	String getFacebookSecret();
	void setFacebookSecret(String facebookSecret);

	// GitHub

	boolean getEnableGithub();
	void setEnableGithub(boolean enableGithub);

	String getGithubKey();
	void setGithubKey(String githubKey);

	String getGithubSecret();
	void setGithubSecret(String githubSecret);

	// Cognito

	boolean getEnableCognito();
	void setEnableCognito(boolean enableCognito);

	String getCognitoClientId();
	void setCognitoClientId(String cognitoClientId);

	String getCognitoSecret();
	void setCognitoSecret(String cognitoSecret);

	String getCognitoRegion();
	void setCognitoRegion(String cognitoRegion);

	String getCognitoUserPoolId();
	void setCognitoUserPoolId(String cognitoUserPoolId);

	boolean getCognitoExclusiveRoleProvider();
	void setCognitoExclusiveRoleProvider(boolean cognitoExclusiveRoleProvider);

	// Okta

	boolean getEnableOkta();
	void setEnableOkta(boolean enableOkta);

	String getOktaClientId();
	void setOktaClientId(String oktaClientId);

	String getOktaSecret();
	void setOktaSecret(String oktaSecret);

	String getOktaDiscoveryUrl();
	void setOktaDiscoveryUrl(String oktaDiscoveryUrl);

	boolean getOktaExclusiveRoleProvider();
	void setOktaExclusiveRoleProvider(boolean oktaExclusiveRoleProvider);

	String getOktaRolesField();
	void setOktaRolesField(String oktaRolesField);

	@Initializer("CSV")
	FieldEncoding getOktaRolesFieldEncoding();
	void setOktaRolesFieldEncoding(FieldEncoding oktaRolesFieldEncoding);

	Boolean getOktaIncludeAccessTokenClaimsInProfile();
	void setOktaIncludeAccessTokenClaimsInProfile(Boolean oktaIncludeAccessTokenClaimsInProfile);
	
	// Instagram

	boolean getEnableInstagram();
	void setEnableInstagram(boolean enableInstagram);

	String getInstagramClientId();
	void setInstagramClientId(String instagramClientId);

	String getInstagramSecret();
	void setInstagramSecret(String instagramSecret);


	// Login configuration

	@Initializer("['email']")
	List<String> getLoginUserRolesMapField();
	void setLoginUserRolesMapField(List<String> loginUserRolesMapField);

	String getLoginUserRolesMap();
	void setLoginUserRolesMap(String loginUserRolesMap);

	String getLoginAcceptList();
	void setLoginAcceptList(String loginAcceptList);

	String getLoginBlockList();
	void setLoginBlockList(String loginBlockList);

	@Initializer("true")
	boolean getLoginCreateUsers();
	void setLoginCreateUsers(boolean loginCreateUsers);

	Set<String> getLoginNewUserFixedRoles();
	void setLoginNewUserFixedRoles(Set<String> loginNewUserFixedRoles);

	// URLs

	String getPublicServicesUrl();
	void setPublicServicesUrl(String publicServicesUrl);

	String getCallbackUrl();
	void setCallbackUrl(String callbackUrl);

	String getUnauthorizedUrl();
	void setUnauthorizedUrl(String unauthorizedUrl);

	String getUnauthenticatedUrl();
	void setUnauthenticatedUrl(String unauthenticatedUrl);

	String getRedirectUrl();
	void setRedirectUrl(String redirectUrl);

	boolean getAddSessionParameterOnRedirect();
	void setAddSessionParameterOnRedirect(boolean addSessionParameterOnRedirect);

	// Login UI

	String getLoginDomain();
	void setLoginDomain(String loginDomain);

	String getLoginCustomParams();
	void setLoginCustomParams(String loginCustomParams);

	@Initializer("true")
	boolean getShowStandardLoginForm();
	void setShowStandardLoginForm(boolean showStandardLoginForm);

	boolean getShowTextLinks();
	void setShowTextLinks(boolean showTextLinks);

	@Initializer("true")
	boolean getObfuscateLogOutput();
	void setObfuscateLogOutput(boolean obfuscateLogOutput);

}