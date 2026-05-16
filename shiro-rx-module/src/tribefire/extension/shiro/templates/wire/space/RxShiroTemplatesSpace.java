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
package tribefire.extension.shiro.templates.wire.space;

import static com.braintribe.utils.lcd.CollectionTools2.asList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.processing.shiro.ShiroAuthenticationUrl;
import com.braintribe.model.processing.shiro.ShiroConstants;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.shiro.deployment.FieldEncoding;
import com.braintribe.model.shiro.deployment.ShiroAuthenticationConfiguration;
import com.braintribe.model.shiro.deployment.ShiroClient;
import com.braintribe.model.shiro.deployment.UserToRolesMapEntry;
import com.braintribe.model.shiro.deployment.client.OAuth20ClientAuthenticationMethod;
import com.braintribe.model.shiro.deployment.client.ShiroAwsCognitoClient;
import com.braintribe.model.shiro.deployment.client.ShiroAzureAdClient;
import com.braintribe.model.shiro.deployment.client.ShiroFacebookClient;
import com.braintribe.model.shiro.deployment.client.ShiroGithubClient;
import com.braintribe.model.shiro.deployment.client.ShiroInstagramOAuth20Client;
import com.braintribe.model.shiro.deployment.client.ShiroOidcGoogleClient;
import com.braintribe.model.shiro.deployment.client.ShiroOktaOpenIdClient;
import com.braintribe.model.shiro.deployment.client.ShiroTwitterClient;
import com.braintribe.utils.StringTools;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;
import com.braintribe.wire.api.space.WireSpace;

import tribefire.extension.shiro.config.RxFixedNewUserRoleProvider;
import tribefire.extension.shiro.config.RxLogin;
import tribefire.extension.shiro.config.RxMappedNewUserRoleProvider;
import tribefire.extension.shiro.config.RxSessionValidator;
import tribefire.extension.shiro.config.RxShiroBootstrappingWorker;
import tribefire.extension.shiro.config.RxShiroServiceProcessor;
import tribefire.extension.shiro.processing.service.HealthCheckProcessor;
import tribefire.extension.shiro.processing.service.ShiroServiceProcessor;
import tribefire.extension.shiro.templates.api.RxShiroTemplateContext;

@Managed
public class RxShiroTemplatesSpace implements WireSpace {

	@Import
	private RxShiroDeployablesSpace deployables;

	@Import
	private RxShiroMetaDataSpace shiroMetaData;

	@Managed
	public ShiroAuthenticationConfiguration authenticationConfiguration(RxShiroTemplateContext context) {
		ShiroAuthenticationConfiguration bean = ShiroAuthenticationConfiguration.T.create();

		if (context.getGoogleEnabled())
			bean.getClients().add(authenticationGoogle(context));

		if (context.getFacebookEnabled())
			bean.getClients().add(authenticationFacebook(context));

		if (context.getTwitterEnabled())
			bean.getClients().add(authenticationTwitter(context));

		if (context.getGithubEnabled())
			bean.getClients().add(authenticationGithub(context));

		if (context.getAzureEnabled())
			bean.getClients().add(authenticationAzureAd(context));

		if (context.getCognitoEnabled())
			bean.getClients().add(authenticationCognito(context));

		if (context.getOktaEnabled())
			bean.getClients().add(authenticationOkta(context));

		if (context.getInstagramEnabled())
			bean.getClients().add(authenticationInstagram(context));

		bean.setDefaultRedirectUrl(context.getPublicServicesUrl());
		bean.setServletPath(context.getServletPath());
		bean.setCallbackUrl(context.getCallbackUrl());
		bean.setDefaultRedirectUrl(context.getRedirectUrl());
		bean.setUnauthorizedUrl(context.getUnauthorizedUrl());
		bean.setUnauthenticatedUrl(context.getUnauthenticatedUrl());
		bean.setFallbackUrl(context.getFallbackUrl());

		return bean;
	}

	@Managed
	private ShiroOidcGoogleClient authenticationGoogle(RxShiroTemplateContext context) {
		ShiroOidcGoogleClient bean = ShiroOidcGoogleClient.T.create();
		bean.setName("Google");

		bean.setClientId(context.getGoogleClientId());
		bean.setSecret(context.getGoogleSecret());
		bean.setUsernamePatterns(asList("{email}"));
		bean.setUserIconUrl("{picture}");
		bean.setUserDescriptionPattern("{name}");
		bean.setUserMailField("{email}");
		bean.setFirstNamePattern("{given_name}");
		bean.setLastNamePattern("{family_name}");
		bean.setUseNonce(true);
		bean.setActive(true);

		Map<String, String> customParams = new HashMap<>();
		String loginDomain = context.getLoginDomain();
		if (!StringTools.isBlank(loginDomain)) {
			customParams.put("hd", loginDomain.trim());
		}
		Map<String, String> otherCustomParams = context.getCustomParameters();
		if (otherCustomParams != null) {
			customParams.putAll(otherCustomParams);
		}
		if (!customParams.isEmpty()) {
			bean.setCustomParams(customParams);
		}
		return bean;
	}

	@Managed
	private ShiroAzureAdClient authenticationAzureAd(RxShiroTemplateContext context) {
		ShiroAzureAdClient bean = ShiroAzureAdClient.T.create();
		bean.setName("AzureAd");

		bean.setClientId(context.getAzureClientId());
		bean.setSecret(context.getAzureSecret());
		bean.setUsernamePatterns(asList("{email}"));
		bean.setUserDescriptionPattern("{name}");
		bean.setUserMailField("{email}");
		bean.setFirstNamePattern("{given_name}");
		bean.setLastNamePattern("{family_name}");
		bean.setUseNonce(true);
		bean.setActive(true);
		bean.setTenant(context.getAzureTenant());

		Map<String, String> customParams = new HashMap<>();
		String loginDomain = context.getLoginDomain();
		if (!StringTools.isBlank(loginDomain)) {
			customParams.put("domain_hint", loginDomain.trim());
		}
		Map<String, String> otherCustomParams = context.getCustomParameters();
		if (otherCustomParams != null) {
			customParams.putAll(otherCustomParams);
		}
		if (!customParams.isEmpty()) {
			bean.setCustomParams(customParams);
		}
		return bean;
	}

	@Managed
	private ShiroAwsCognitoClient authenticationCognito(RxShiroTemplateContext context) {
		ShiroAwsCognitoClient bean = ShiroAwsCognitoClient.T.create();
		bean.setName("Cognito");

		bean.setClientId(context.getCognitoClientId());
		bean.setSecret(context.getCognitoSecret());
		bean.setUsernamePatterns(asList("{email}"));
		bean.setUserDescriptionPattern("{username}");
		bean.setUserMailField("{email}");
		bean.setUseNonce(true);
		bean.setActive(true);
		bean.setRegion(context.getCognitoRegion());
		bean.setUserPoolId(context.getCognitoUserPoolId());
		bean.setRolesField("{cognito:roles}");
		bean.setRolesFieldEncoding(FieldEncoding.JSON);
		bean.setExclusiveRoleProvider(context.getCognitoExclusiveRoleProvider());

		Map<String, String> customParams = new HashMap<>();
		String loginDomain = context.getLoginDomain();
		if (!StringTools.isBlank(loginDomain)) {
			customParams.put("domain_hint", loginDomain.trim());
		}
		Map<String, String> otherCustomParams = context.getCustomParameters();
		if (otherCustomParams != null) {
			customParams.putAll(otherCustomParams);
		}
		if (!customParams.isEmpty()) {
			bean.setCustomParams(customParams);
		}
		return bean;
	}

	@Managed
	private ShiroOktaOpenIdClient authenticationOkta(RxShiroTemplateContext context) {
		ShiroOktaOpenIdClient bean = ShiroOktaOpenIdClient.T.create();
		bean.setName("Okta");

		bean.setClientId(context.getOktaClientId());
		bean.setSecret(context.getOktaSecret());
		bean.setDiscoveryUri(context.getOktaDiscoveryUrl());
		bean.setUsernamePatterns(asList("{preferred_username}", "{email}"));
		bean.setUserDescriptionPattern("{name}");
		bean.setUserMailField("{email}");
		bean.setFirstNamePattern("{given_name}");
		bean.setLastNamePattern("{family_name}");
		bean.setUseNonce(true);
		bean.setActive(true);
		bean.setExclusiveRoleProvider(context.getOktaExclusiveRoleProvider());
		bean.setRolesField(context.getOktaRolesField());
		bean.setRolesFieldEncoding(context.getOktaRolesFieldEncoding());
		bean.setIncludeAccessTokenClaimsInProfile(context.getOktaIncludeAccessTokenClaimsInProfile());

		Map<String, String> customParams = new HashMap<>();
		Map<String, String> otherCustomParams = context.getCustomParameters();
		if (otherCustomParams != null) {
			customParams.putAll(otherCustomParams);
		}
		if (!customParams.isEmpty()) {
			bean.setCustomParams(customParams);
		}
		return bean;
	}

	@Managed
	private ShiroInstagramOAuth20Client authenticationInstagram(RxShiroTemplateContext context) {
		ShiroInstagramOAuth20Client bean = ShiroInstagramOAuth20Client.T.create();
		bean.setName("Instagram");

		bean.setKey(context.getInstagramClientId());
		bean.setSecret(context.getInstagramSecret());
		bean.setAuthUrl(context.getInstagramAuthUrl());
		bean.setTokenUrl(context.getInstagramTokenUrl());
		bean.setProfileUrl(context.getInstagramProfileUrl());
		bean.setUserInformationUrl(context.getInstagramUserInformationUrl());
		bean.setUsernamePatterns(asList(context.getInstagramUsernamePattern()));
		bean.setClientAuthenticationMethod(OAuth20ClientAuthenticationMethod.requestBody);
		bean.setUsePathUrlResolver(true);
		bean.setActive(true);

		Map<String, String> customParams = new HashMap<>();
		String instagramScope = context.getInstagramScope();
		if (!StringTools.isBlank(instagramScope)) {
			customParams.put("scope", instagramScope);
		}

		Map<String, String> otherCustomParams = context.getCustomParameters();
		if (otherCustomParams != null) {
			customParams.putAll(otherCustomParams);
		}
		if (!customParams.isEmpty()) {
			bean.setCustomParams(customParams);
		}
		return bean;
	}

	@Managed
	private ShiroTwitterClient authenticationTwitter(RxShiroTemplateContext context) {
		ShiroTwitterClient bean = ShiroTwitterClient.T.create();
		bean.setName("Twitter");
		bean.setKey(context.getTwitterKey());
		bean.setSecret(context.getTwitterSecret());
		bean.setUsernamePatterns(asList("@{screen_name}"));
		bean.setUserIconUrl("{profile_image_url_https}");
		bean.setUserDescriptionPattern("{name}");
		bean.setActive(true);
		return bean;
	}

	@Managed
	private ShiroFacebookClient authenticationFacebook(RxShiroTemplateContext context) {
		ShiroFacebookClient bean = ShiroFacebookClient.T.create();
		bean.setName("Facebook");
		bean.setKey(context.getFacebookKey());
		bean.setSecret(context.getFacebookSecret());
		bean.setUsernamePatterns(asList("{email}"));
		bean.setUserDescriptionPattern("{name}");
		bean.setUserMailField("{email}");
		bean.setFirstNamePattern("{first_name}");
		bean.setLastNamePattern("{last_name}");
		bean.setScope("public_profile,email");
		bean.setUserIconUrl("https://graph.facebook.com/{id}/picture?type=large");
		bean.setActive(true);
		return bean;
	}

	@Managed
	private ShiroGithubClient authenticationGithub(RxShiroTemplateContext context) {
		ShiroGithubClient bean = ShiroGithubClient.T.create();
		bean.setName("Github");
		bean.setKey(context.getGithubKey());
		bean.setSecret(context.getGithubSecret());
		bean.setScope("user, user:email");
		bean.setUsernamePatterns(asList("{email}", "{login}@github.com"));
		bean.setUserIconUrl("{avatar_url}");
		bean.setUserDescriptionPattern("{login}");
		bean.setActive(true);
		return bean;
	}

	@Managed
	private RxFixedNewUserRoleProvider fixedNewUserRoleProvider(RxShiroTemplateContext context) {
		RxFixedNewUserRoleProvider bean = RxFixedNewUserRoleProvider.T.create();
		bean.getRoles().addAll(context.getFixedUserRolesForNewUsers());
		return bean;
	}

	@Managed
	private RxMappedNewUserRoleProvider mappedNewUserRoleProvider(RxShiroTemplateContext context) {
		RxMappedNewUserRoleProvider bean = RxMappedNewUserRoleProvider.T.create();
		bean.setFields(context.getUserRolesMapField());

		Map<Set<String>, Set<String>> userRolesMap = context.getUserRolesMap();
		if (userRolesMap != null && !userRolesMap.isEmpty()) {
			for (Map.Entry<Set<String>, Set<String>> entry : userRolesMap.entrySet()) {
				Set<String> userSpecsSet = entry.getKey();
				Set<String> rolesSpecsSet = entry.getValue();

				if (userSpecsSet != null && !userSpecsSet.isEmpty() && rolesSpecsSet != null && !rolesSpecsSet.isEmpty()) {
					UserToRolesMapEntry e = userToRolesMapEntry();
					e.setUsernameSpecifications(userSpecsSet);
					e.setRoles(rolesSpecsSet);
					bean.getMapping().add(e);
				}
			}
		}

		return bean;
	}

	@Managed
	private UserToRolesMapEntry userToRolesMapEntry() {
		UserToRolesMapEntry bean = UserToRolesMapEntry.T.create();
		return bean;
	}

	@Managed
	public RxLogin login(RxShiroTemplateContext context) {
		RxLogin bean = RxLogin.T.create();
		bean.setName("Remote Login Terminal");
		bean.setPathIdentifier(context.getServletPath());
		bean.setConfiguration(authenticationConfiguration(context));
		if (context.getUserRolesMap() != null) {
			bean.setNewUserRoleProvider(mappedNewUserRoleProvider(context));
		} else {
			bean.setNewUserRoleProvider(fixedNewUserRoleProvider(context));
		}
		bean.setUserAcceptList(context.getAcceptList());
		bean.setUserBlockList(context.getBlockList());

		bean.setCreateUsers(context.getCreateUsers());

		bean.setShowStandardLoginForm(context.getShowStandardLoginForm());
		bean.setShowTextLinks(context.getShowTextLinks());
		bean.setAddSessionParameterOnRedirect(context.getAddSessionParameterOnRedirect());
		bean.setObfuscateLogOutput(context.getObfuscateLogOutput());
		return bean;
	}

	@Managed
	public RxSessionValidator sessionValidator() {
		RxSessionValidator bean = RxSessionValidator.T.create();
		bean.setName("Session Validator Terminal");
		bean.setPathIdentifier("session-validator");
		return bean;
	}

	@Managed
	public RxShiroBootstrappingWorker bootstrappingWorker(RxShiroTemplateContext context) {
		RxShiroBootstrappingWorker bean = RxShiroBootstrappingWorker.T.create();
		bean.setId(context.getIdPrefix() + "-bootstrapping-worker"); // needed to create a worker identification
		bean.setConfiguration(authenticationConfiguration(context));
		bean.setPathIdentifier(login(context).getPathIdentifier());
		return bean;
	}

	@Managed
	public ShiroServiceProcessor serviceRequestProcessor(RxShiroTemplateContext context) {
		return deployables.serviceProcessor(context, serviceRequestProcessorDenotation(context));
	}

	private RxShiroServiceProcessor serviceRequestProcessorDenotation(RxShiroTemplateContext context) {
		RxShiroServiceProcessor bean = RxShiroServiceProcessor.T.create();
		bean.setConfiguration(authenticationConfiguration(context));
		bean.setPathIdentifier(context.getServletPath());
		bean.setObfuscateLogOutput(context.getObfuscateLogOutput());

		return bean;
	}

	// @Managed
	// public CheckBundle functionalCheckBundle(RxShiroTemplateContext context) {
	// CheckBundle bean = CheckBundle.T.create();
	// bean.getChecks().add(healthCheckProcessor(context));
	// bean.setName("Shiro Checks");
	// bean.setWeight(CheckWeight.under1s);
	// bean.setCoverage(CheckCoverage.functional);
	// bean.setIsPlatformRelevant(false);
	//
	// return bean;
	// }
	//
	// @Managed
	// private HealthCheckProcessor healthCheckProcessor(RxShiroTemplateContext context) {
	// HealthCheckProcessor bean = HealthCheckProcessor.T.create();
	// bean.setName("Shiro Check Processor");
	// return bean;
	// }

	@Managed
	public HealthCheckProcessor healthCheckProcessor() {
		return deployables.healthCheckProcessor();
	}

	public List<ShiroAuthenticationUrl> getAuthenticationUrls(RxShiroTemplateContext context) {
		String tfs = context.getPublicServicesUrl();
		if (!tfs.endsWith("/")) {
			tfs = tfs + "/";
		}
		List<ShiroAuthenticationUrl> result = new ArrayList<>();
		ShiroAuthenticationConfiguration config = authenticationConfiguration(context);
		for (ShiroClient client : config.getClients()) {
			String clientName = client.getName();
			String authUrl = tfs + context.getServletPath() + "/auth/" + clientName.toLowerCase();
			// TODO not sure about these images...
			String imageUrl = tfs + ShiroConstants.STATIC_IMAGES_RELATIVE_PATH + clientName.toLowerCase() + ".png";
			ShiroAuthenticationUrl ctx = new ShiroAuthenticationUrl(authUrl, clientName, imageUrl);

			Resource loginIcon = client.getLoginIcon();
			if (loginIcon != null) {
				ctx.setIconResourceId(loginIcon.getId());
			}

			result.add(ctx);
		}
		return result;
	}
}
