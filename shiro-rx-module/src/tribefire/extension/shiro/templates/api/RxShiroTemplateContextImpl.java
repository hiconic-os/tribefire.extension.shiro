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
import com.braintribe.utils.lcd.CollectionTools2;

import hiconic.rx.access.module.api.AccessContract;

public class RxShiroTemplateContextImpl implements RxShiroTemplateContext, RxShiroTemplateContextBuilder {

	private String name;
	private String idPrefix;

	private String servletPath;

	private AccessContract accessContract;
	private String authAccessId;

	private boolean googleEnabled;
	private String googleClientId;
	private String googleSecret;

	private boolean azureEnabled;
	private String azureClientId;
	private String azureSecret;
	private String azureTenant;

	private boolean twitterEnabled;
	private String twitterKey;
	private String twitterSecret;

	private boolean facebookEnabled;
	private String facebookKey;
	private String facebookSecret;

	private boolean githubEnabled;
	private String githubKey;
	private String githubSecret;

	private boolean cognitoEnabled;
	private String cognitoClientId;
	private String cognitoSecret;
	private String cognitoRegion;
	private String cognitoUserPoolId;
	private boolean cognitoExclusiveRoleProvider;

	private boolean oktaEnabled;
	private String oktaClientId;
	private String oktaSecret;
	private String oktaDiscoveryUrl;
	private boolean oktaExclusiveRoleProvider;
	private String oktaRolesField;
	private FieldEncoding oktaRolesFieldEncoding;
	private Boolean oktaIncludeAccessTokenClaimsInProfile;

	private boolean instagramEnabled;
	private String instagramClientId;
	private String instagramSecret;
	private String instagramAuthUrl = "https://api.instagram.com/oauth/authorize";
	private String instagramTokenUrl = "https://api.instagram.com/oauth/access_token";
	private String instagramProfileUrl = "https://graph.instagram.com";
	private String instagramUserInformationUrl = "https://www.instagram.com/{username}/?__a=1";
	private String instagramUsernamePattern = "{username}";
	private String instagramScope = "user_profile";

	private List<String> userRolesMapField = CollectionTools2.asList("email");
	private Map<Set<String>, Set<String>> userRolesMap;

	private Set<String> acceptList;
	private Set<String> blockList;

	private boolean createUsers = true;
	private String publicServicesUrl;
	private String callbackUrl;
	private String unauthorizedUrl;
	private String unauthenticatedUrl;
	private String redirectUrl;
	private String fallbackUrl;
	private boolean addSessionParameterOnRedirect;

	private String loginDomain;
	private Map<String, String> customParameters;
	private boolean showStandardLoginForm = true;
	private boolean showTextLinks;

	private boolean obfuscateLogOutput = true;

	private Set<String> fixedUserRolesForNewUsers = Set.of("tf-internal");

	// #############################################
	// ## . . . . . . . . Build . . . . . . . . . ##
	// #############################################

	@Override
	public RxShiroTemplateContext build() {
		return this;
	}

	// #############################################
	// ## . . . . . . . . Getters . . . . . . . . ##
	// #############################################

	// @formatter:off
	@Override public String getName() { return name; }
	@Override public String getIdPrefix() { return idPrefix; }

	@Override public String getServletPath() { return servletPath; }

	@Override public AccessContract getAccessContract() { return accessContract; }
	@Override public String getAuthAccessId() { return authAccessId; }

	@Override public boolean getGoogleEnabled() { return googleEnabled; }
	@Override public String getGoogleClientId() { return googleClientId; }
	@Override public String getGoogleSecret() { return googleSecret; }

	@Override public boolean getAzureEnabled() { return azureEnabled; }
	@Override public String getAzureClientId() { return azureClientId; }
	@Override public String getAzureSecret() { return azureSecret; }
	@Override public String getAzureTenant() { return azureTenant; }

	@Override public boolean getTwitterEnabled() { return twitterEnabled; }
	@Override public String getTwitterKey() { return twitterKey; }
	@Override public String getTwitterSecret() { return twitterSecret; }

	@Override public boolean getFacebookEnabled() { return facebookEnabled; }
	@Override public String getFacebookKey() { return facebookKey; }
	@Override public String getFacebookSecret() { return facebookSecret; }

	@Override public boolean getGithubEnabled() { return githubEnabled; }
	@Override public String getGithubKey() { return githubKey; }
	@Override public String getGithubSecret() { return githubSecret; }

	@Override public boolean getCognitoEnabled() { return cognitoEnabled; }
	@Override public String getCognitoClientId() { return cognitoClientId; }
	@Override public String getCognitoSecret() { return cognitoSecret; }
	@Override public String getCognitoRegion() { return cognitoRegion; }
	@Override public String getCognitoUserPoolId() { return cognitoUserPoolId; }
	@Override public boolean getCognitoExclusiveRoleProvider() { return cognitoExclusiveRoleProvider; }

	@Override public boolean getInstagramEnabled() { return this.instagramEnabled; }
	@Override public String getInstagramClientId() { return this.instagramClientId; }
	@Override public String getInstagramSecret() { return this.instagramSecret; }
	@Override public String getInstagramAuthUrl() { return this.instagramAuthUrl; }
	@Override public String getInstagramTokenUrl() { return this.instagramTokenUrl; }
	@Override public String getInstagramProfileUrl() { return this.instagramProfileUrl; }
	@Override public String getInstagramUsernamePattern() { return this.instagramUsernamePattern; }
	@Override public String getInstagramScope() { return this.instagramScope; }

	@Override public boolean getOktaEnabled() { return oktaEnabled; }
	@Override public String getOktaClientId() { return oktaClientId; }
	@Override public String getOktaSecret() { return oktaSecret; }
	@Override public String getOktaDiscoveryUrl() { return oktaDiscoveryUrl; }
	@Override public boolean getOktaExclusiveRoleProvider() { return oktaExclusiveRoleProvider; }
	@Override public String getOktaRolesField() { return oktaRolesField; }
	@Override public FieldEncoding getOktaRolesFieldEncoding() { return oktaRolesFieldEncoding; }
	@Override public Boolean getOktaIncludeAccessTokenClaimsInProfile() { return oktaIncludeAccessTokenClaimsInProfile; }

	@Override public List<String> getUserRolesMapField() { return userRolesMapField; }
	@Override public Map<Set<String>, Set<String>> getUserRolesMap() { return userRolesMap; }
	@Override public Set<String> getAcceptList() { return acceptList; }
	@Override public Set<String> getBlockList() { return blockList; }
	@Override public boolean getCreateUsers() { return createUsers; }
	@Override public String getPublicServicesUrl() { return publicServicesUrl; }
	@Override public boolean getAddSessionParameterOnRedirect() { return addSessionParameterOnRedirect; }
	@Override public String getLoginDomain() { return loginDomain; }
	@Override public Map<String, String> getCustomParameters() { return customParameters; }
	@Override public boolean getShowStandardLoginForm() { return showStandardLoginForm; }
	@Override public boolean getShowTextLinks() { return showTextLinks; }
	@Override public String getUnauthenticatedUrl() { return unauthenticatedUrl; }
	@Override public String getFallbackUrl() { return fallbackUrl; }

	@Override public Boolean getObfuscateLogOutput() { return obfuscateLogOutput; }
	@Override public Set<String> getFixedUserRolesForNewUsers() { return fixedUserRolesForNewUsers; }
	// @formatter:on

	@Override
	public String getCallbackUrl() {
		if (callbackUrl == null) {
			String publicServicesUrl = getPublicServicesUrl();
			if (!publicServicesUrl.endsWith("/")) {
				publicServicesUrl = publicServicesUrl + "/";
			}
			callbackUrl = publicServicesUrl + servletPath + "/auth/callback";
		}
		return callbackUrl;
	}
	@Override
	public String getUnauthorizedUrl() {
		if (unauthorizedUrl == null) {
			String publicServicesUrl = getPublicServicesUrl();
			if (!publicServicesUrl.endsWith("/")) {
				publicServicesUrl = publicServicesUrl + "/";
			}
			unauthorizedUrl = publicServicesUrl + servletPath;
		}
		return unauthorizedUrl;
	}
	@Override
	public String getRedirectUrl() {
		if (redirectUrl == null) {
			redirectUrl = getPublicServicesUrl();
		}
		return redirectUrl;
	}
	// #############################################
	// ## . . . . . . . . Setters . . . . . . . . ##
	// #############################################

	@Override
	public RxShiroTemplateContextBuilder setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public RxShiroTemplateContextBuilder setIdPrefix(String idPrefix) {
		this.idPrefix = idPrefix;
		return this;
	}

	@Override
	public RxShiroTemplateContextBuilder setServletPath(String servletPath) {
		this.servletPath = servletPath;
		return this;
	}

	@Override
	public RxShiroTemplateContextBuilder setAuthAccessId(String authAccessId) {
		this.authAccessId = authAccessId;
		return this;
	}

	@Override
	public RxShiroTemplateContextBuilder setAccessContract(AccessContract accessContract) {
		this.accessContract = accessContract;
		return this;
	}

	@Override
	public RxShiroTemplateContextBuilder setGoogleEnabled(boolean googleEnabled) {
		this.googleEnabled = googleEnabled;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setGoogleClientId(String googleClientId) {
		this.googleClientId = googleClientId;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setGoogleSecret(String googleSecret) {
		this.googleSecret = googleSecret;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setAzureEnabled(boolean azureEnabled) {
		this.azureEnabled = azureEnabled;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setAzureClientId(String azureClientId) {
		this.azureClientId = azureClientId;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setAzureSecret(String azureSecret) {
		this.azureSecret = azureSecret;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setAzureTenant(String azureTenant) {
		this.azureTenant = azureTenant;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setTwitterEnabled(boolean twitterEnabled) {
		this.twitterEnabled = twitterEnabled;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setTwitterKey(String twitterKey) {
		this.twitterKey = twitterKey;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setTwitterSecret(String twitterSecret) {
		this.twitterSecret = twitterSecret;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setFacebookEnabled(boolean facebookEnabled) {
		this.facebookEnabled = facebookEnabled;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setFacebookKey(String facebookKey) {
		this.facebookKey = facebookKey;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setFacebookSecret(String facebookSecret) {
		this.facebookSecret = facebookSecret;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setGithubEnabled(boolean githubEnabled) {
		this.githubEnabled = githubEnabled;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setGithubKey(String githubKey) {
		this.githubKey = githubKey;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setGithubSecret(String githubSecret) {
		this.githubSecret = githubSecret;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setCognitoEnabled(boolean cognitoEnabled) {
		this.cognitoEnabled = cognitoEnabled;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setCognitoClientId(String cognitoClientId) {
		this.cognitoClientId = cognitoClientId;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setCognitoSecret(String cognitoSecret) {
		this.cognitoSecret = cognitoSecret;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setCognitoRegion(String cognitoRegion) {
		this.cognitoRegion = cognitoRegion;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setCognitoUserPoolId(String cognitoUserPoolId) {
		this.cognitoUserPoolId = cognitoUserPoolId;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setCognitoExclusiveRoleProvider(boolean cognitoExclusiveRoleProvider) {
		this.cognitoExclusiveRoleProvider = cognitoExclusiveRoleProvider;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setOktaEnabled(boolean oktaEnabled) {
		this.oktaEnabled = oktaEnabled;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setOktaClientId(String oktaClientId) {
		this.oktaClientId = oktaClientId;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setOktaSecret(String oktaSecret) {
		this.oktaSecret = oktaSecret;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setOktaDiscoveryUrl(String oktaDiscoveryUrl) {
		this.oktaDiscoveryUrl = oktaDiscoveryUrl;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setOktaExclusiveRoleProvider(boolean oktaExclusiveRoleProvider) {
		this.oktaExclusiveRoleProvider = oktaExclusiveRoleProvider;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setOktaRolesField(String oktaRolesField) {
		this.oktaRolesField = oktaRolesField;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setOktaRolesFieldEncoding(FieldEncoding oktaRolesFieldEncoding) {
		this.oktaRolesFieldEncoding = oktaRolesFieldEncoding;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setOktaIncludeAccessTokenClaimsInProfile(Boolean oktaIncludeAccessTokenClaimsInProfile) {
		this.oktaIncludeAccessTokenClaimsInProfile = oktaIncludeAccessTokenClaimsInProfile;
		return this;
	}
	
	@Override
	public RxShiroTemplateContextBuilder setUserRolesMapField(List<String> userRolesMapField) {
		this.userRolesMapField = userRolesMapField;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setUserRolesMap(Map<Set<String>, Set<String>> userRolesMap) {
		this.userRolesMap = userRolesMap;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setAcceptList(Set<String> acceptList) {
		this.acceptList = acceptList;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setBlockList(Set<String> blockList) {
		this.blockList = blockList;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setCreateUsers(boolean createUsers) {
		this.createUsers = createUsers;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setPublicServicesUrl(String publicServicesUrl) {
		this.publicServicesUrl = publicServicesUrl;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setUnauthorizedUrl(String unauthorizedUrl) {
		this.unauthorizedUrl = unauthorizedUrl;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setUnauthenticatedUrl(String unauthenticatedUrl) {
		this.unauthenticatedUrl = unauthenticatedUrl;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setFallbackUrl(String fallbackUrl) {
		this.fallbackUrl = fallbackUrl;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setAddSessionParameterOnRedirect(boolean addSessionParameterOnRedirect) {
		this.addSessionParameterOnRedirect = addSessionParameterOnRedirect;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setLoginDomain(String loginDomain) {
		this.loginDomain = loginDomain;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setCustomParameters(Map<String, String> customParameters) {
		this.customParameters = customParameters;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setShowStandardLoginForm(boolean showStandardLoginForm) {
		this.showStandardLoginForm = showStandardLoginForm;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setShowTextLinks(boolean showTextLinks) {
		this.showTextLinks = showTextLinks;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setInstagramEnabled(boolean instagramEnabled) {
		this.instagramEnabled = instagramEnabled;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setInstagramClientId(String instagramClientId) {
		this.instagramClientId = instagramClientId;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setInstagramSecret(String instagramSecret) {
		this.instagramSecret = instagramSecret;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setInstagramAuthUrl(String instagramAuthUrl) {
		this.instagramAuthUrl = instagramAuthUrl;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setInstagramTokenUrl(String instagramTokenUrl) {
		this.instagramTokenUrl = instagramTokenUrl;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setInstagramProfileUrl(String instagramProfileUrl) {
		this.instagramProfileUrl = instagramProfileUrl;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setInstagramUsernamePattern(String instagramUsernamePattern) {
		this.instagramUsernamePattern = instagramUsernamePattern;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setInstagramScope(String instagramScope) {
		this.instagramScope = instagramScope;
		return this;
	}

	@Override
	public RxShiroTemplateContextBuilder setInstagramUserInformationUrl(String instagramUserInformationUrl) {
		this.instagramUserInformationUrl = instagramUserInformationUrl;
		return this;
	}
	@Override
	public String getInstagramUserInformationUrl() {
		return instagramUserInformationUrl;
	}
	@Override
	public RxShiroTemplateContextBuilder setObfuscateLogOutput(boolean obfuscateLogOutput) {
		this.obfuscateLogOutput = obfuscateLogOutput;
		return this;
	}
	@Override
	public RxShiroTemplateContextBuilder setFixedUserRolesForNewUsers(Set<String> fixedUserRolesForNewUsers) {
		if (fixedUserRolesForNewUsers != null) {
			this.fixedUserRolesForNewUsers = fixedUserRolesForNewUsers;
		}
		return this;
	}
}
