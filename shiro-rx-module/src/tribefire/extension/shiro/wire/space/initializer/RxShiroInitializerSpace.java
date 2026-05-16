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
package tribefire.extension.shiro.wire.space.initializer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.braintribe.model.processing.shiro.utils.ShiroInitializationTools;
import com.braintribe.utils.StringTools;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;
import com.braintribe.wire.api.space.WireSpace;

import hiconic.rx.access.module.api.AccessContract;
import hiconic.rx.module.api.service.ModelConfigurations;
import hiconic.rx.web.server.api.WebServerContract;
import tribefire.extension.shiro.config.RxLogin;
import tribefire.extension.shiro.config.RxSessionValidator;
import tribefire.extension.shiro.config.RxShiroBootstrappingWorker;
import tribefire.extension.shiro.configuration.model.ShiroConfiguration;
import tribefire.extension.shiro.processing.service.HealthCheckProcessor;
import tribefire.extension.shiro.templates.api.RxShiroTemplateContext;
import tribefire.extension.shiro.templates.api.RxShiroTemplateContextBuilder;
import tribefire.extension.shiro.templates.wire.space.RxShiroMetaDataSpace;
import tribefire.extension.shiro.templates.wire.space.RxShiroTemplatesSpace;
import tribefire.extension.shiro.wire.space.ShiroRxModuleSpace;

@Managed
public class RxShiroInitializerSpace implements WireSpace {

	// @formatter:off
	@Import private ShiroRxModuleSpace shiroModule;
	@Import private RxShiroTemplatesSpace templates;
	@Import private RxShiroMetaDataSpace metaData;
	@Import private WebServerContract webServer;
	@Import private AccessContract access;
	// @formatter:on

	@Managed
	public RxShiroTemplateContext defaultContext() {
		ShiroConfiguration config = shiroConfiguration();

		String publicServicesUrl = config.getPublicServicesUrl();
		if (StringTools.isBlank(publicServicesUrl))
			publicServicesUrl = webServer.defaultEndpointUrl();

		String callbackUrl = config.getCallbackUrl();
		if (StringTools.isBlank(callbackUrl))
			callbackUrl = publicServicesUrl + "/" +config.getServletPath() + "/auth/callback";

		String redirectUrl = config.getRedirectUrl();
		if (StringTools.isBlank(redirectUrl))
			redirectUrl = publicServicesUrl;

		String unauthorizedUrl = config.getUnauthorizedUrl();
		if (StringTools.isBlank(unauthorizedUrl))
			unauthorizedUrl = publicServicesUrl + "/" + config.getServletPath();

		String acceptListString = config.getLoginAcceptList();
		String blockListString = config.getLoginBlockList();

		Map<String, String> customParams = ShiroInitializationTools.decodeMap(config.getLoginCustomParams());

		//@formatter:off
		RxShiroTemplateContextBuilder builder = RxShiroTemplateContext.builder()
			.setAccessContract(access)
			.setServletPath(config.getServletPath())
			.setAuthAccessId(config.getUserAccessId())
			.setObfuscateLogOutput(config.getObfuscateLogOutput())
			.setGoogleEnabled(config.getEnableGoogle())
			.setFacebookEnabled(config.getEnableFacebook())
			.setTwitterEnabled(config.getEnableTwitter())
			.setGithubEnabled(config.getEnableGithub())
			.setAzureEnabled(config.getEnableAzureAd())
			.setCognitoEnabled(config.getEnableCognito())
			.setOktaEnabled(config.getEnableOkta())
			.setInstagramEnabled(config.getEnableInstagram())
			.setPublicServicesUrl(publicServicesUrl)
			.setCallbackUrl(callbackUrl)
			.setRedirectUrl(redirectUrl)
			.setUnauthorizedUrl(unauthorizedUrl)
			.setUnauthenticatedUrl(config.getUnauthenticatedUrl())
			.setLoginDomain(config.getLoginDomain())
			.setCustomParameters(customParams)
			.setAcceptList(ShiroInitializationTools.parseCollection(acceptListString))
			.setBlockList(ShiroInitializationTools.parseCollection(blockListString))
			.setCreateUsers(config.getLoginCreateUsers())
			.setFixedUserRolesForNewUsers(config.getLoginNewUserFixedRoles())
			.setShowStandardLoginForm(config.getShowStandardLoginForm())
			.setShowTextLinks(config.getShowTextLinks())
			.setAddSessionParameterOnRedirect(config.getAddSessionParameterOnRedirect());
		
		configureGoogle(builder);
		configureFacebook(builder);
		configureTwitter(builder);
		configureGithub(builder);
		configureAzure(builder);
		configureCognito(builder);
		configureOkta(builder);
		configureInstagram(builder);
		
		configureMappedNewUserRoles(builder);
		
		RxShiroTemplateContext context = builder
			.setIdPrefix("Shiro.Default")
			.setName("Default")
			.build();
		//@formatter:on

		return context;

	}

	private void configureGoogle(RxShiroTemplateContextBuilder builder) {
		ShiroConfiguration config = shiroConfiguration();
		if (config.getEnableGoogle()) {
			builder.setGoogleClientId(config.getGoogleClientId());
			builder.setGoogleSecret(config.getGoogleSecret());
		}
	}

	private void configureAzure(RxShiroTemplateContextBuilder builder) {
		ShiroConfiguration config = shiroConfiguration();
		if (config.getEnableAzureAd()) {
			builder.setAzureClientId(config.getAzureAdClientId());
			builder.setAzureSecret(config.getAzureAdSecret());
			builder.setAzureTenant(config.getAzureAdTenant());
		}
	}

	private void configureCognito(RxShiroTemplateContextBuilder builder) {
		ShiroConfiguration config = shiroConfiguration();
		if (config.getEnableCognito()) {
			builder.setCognitoClientId(config.getCognitoClientId());
			builder.setCognitoSecret(config.getCognitoSecret());
			builder.setCognitoRegion(config.getCognitoRegion());
			builder.setCognitoUserPoolId(config.getCognitoUserPoolId());
			builder.setCognitoExclusiveRoleProvider(config.getCognitoExclusiveRoleProvider());
		}
	}

	private void configureOkta(RxShiroTemplateContextBuilder builder) {
		ShiroConfiguration config = shiroConfiguration();
		if (config.getEnableOkta()) {
			builder.setOktaClientId(config.getOktaClientId());
			builder.setOktaSecret(config.getOktaSecret());
			builder.setOktaDiscoveryUrl(config.getOktaDiscoveryUrl());
			builder.setOktaExclusiveRoleProvider(config.getOktaExclusiveRoleProvider());
			builder.setOktaRolesField(config.getOktaRolesField());
			builder.setOktaRolesFieldEncoding(config.getOktaRolesFieldEncoding());
			builder.setOktaIncludeAccessTokenClaimsInProfile(config.getOktaIncludeAccessTokenClaimsInProfile());
		}
	}

	private void configureInstagram(RxShiroTemplateContextBuilder builder) {
		ShiroConfiguration config = shiroConfiguration();
		if (config.getEnableInstagram()) {
			builder.setInstagramClientId(config.getInstagramClientId());
			builder.setInstagramSecret(config.getInstagramSecret());
		}
	}

	private void configureTwitter(RxShiroTemplateContextBuilder builder) {
		ShiroConfiguration config = shiroConfiguration();
		if (config.getEnableTwitter()) {
			builder.setTwitterKey(config.getTwitterKey());
			builder.setTwitterSecret(config.getTwitterSecret());
		}
	}

	private void configureFacebook(RxShiroTemplateContextBuilder builder) {
		ShiroConfiguration config = shiroConfiguration();
		if (config.getEnableFacebook()) {
			builder.setFacebookKey(config.getFacebookKey());
			builder.setFacebookSecret(config.getFacebookSecret());
		}
	}

	private void configureGithub(RxShiroTemplateContextBuilder builder) {
		ShiroConfiguration config = shiroConfiguration();
		if (config.getEnableGithub()) {
			builder.setGithubKey(config.getGithubKey());
			builder.setGithubSecret(config.getGithubSecret());
		}
	}

	public void configureMappedNewUserRoles(RxShiroTemplateContextBuilder builder) {
		ShiroConfiguration config = shiroConfiguration();

		builder.setUserRolesMapField(config.getLoginUserRolesMapField());

		Map<Set<String>, Set<String>> map = new HashMap<>();

		String listString = config.getLoginUserRolesMap();
		if (!StringTools.isBlank(listString)) {
			String[] mapEntries = StringTools.splitSemicolonSeparatedString(listString, true);
			for (String mapEntry : mapEntries) {
				int index = mapEntry.indexOf('=');
				if (index != -1) {
					String userSpecString = mapEntry.substring(0, index).trim();
					String rolesSpecString = mapEntry.substring(index + 1).trim();

					Set<String> userSpecsSet = null;
					String[] userSpecs = StringTools.splitCommaSeparatedString(userSpecString, true);
					if (userSpecs != null && userSpecs.length > 0) {
						userSpecsSet = new HashSet<>(Arrays.asList(userSpecs));
					}

					Set<String> rolesSpecsSet = null;
					String[] rolesSpecs = StringTools.splitCommaSeparatedString(rolesSpecString, true);
					if (rolesSpecs != null && rolesSpecs.length > 0) {
						rolesSpecsSet = new HashSet<>(Arrays.asList(rolesSpecs));
					}

					if (userSpecsSet != null && rolesSpecsSet != null) {
						map.put(userSpecsSet, rolesSpecsSet);
					}
				}
			}
		}

		if (!map.isEmpty())
			builder.setUserRolesMap(map);
	}

	private ShiroConfiguration shiroConfiguration() {
		return shiroModule.shiroConfiguration();
	}

	@Managed
	public RxLogin loginDenotation() {
		RxShiroTemplateContext context = defaultContext();
		return templates.login(context);
	}

	@Managed
	public RxSessionValidator sessionValidatorDenotation() {
		return templates.sessionValidator();
	}

	// TODO
	@Managed
	public RxShiroBootstrappingWorker bootstrappingWorker() {
		RxShiroTemplateContext context = defaultContext();
		return templates.bootstrappingWorker(context);
	}

	public void metaData(ModelConfigurations configurations) {
		RxShiroTemplateContext context = defaultContext();
		metaData.metaData(context, configurations);
	}

	@Managed
	public HealthCheckProcessor healthCheckProcessor() {
		return templates.healthCheckProcessor();
	}

}
