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

import java.util.function.Supplier;

import org.apache.shiro.mgt.DefaultSecurityManager;

import com.braintribe.model.processing.shiro.ShiroConstants;
import com.braintribe.model.processing.shiro.bootstrapping.NewUserRoleProvider;
import com.braintribe.provider.Holder;
import com.braintribe.transport.http.DefaultHttpClientProvider;
import com.braintribe.transport.http.HttpClientProvider;
import com.braintribe.transport.ssl.SslSocketFactoryProvider;
import com.braintribe.transport.ssl.impl.EasySslSocketFactoryProvider;
import com.braintribe.transport.ssl.impl.StrictSslSocketFactoryProvider;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;
import com.braintribe.wire.api.context.WireContextConfiguration;
import com.braintribe.wire.api.space.WireSpace;

import hiconic.rx.access.module.api.AccessContract;
import hiconic.rx.module.api.wire.RxMarshallingContract;
import hiconic.rx.module.api.wire.RxPlatformContract;
import hiconic.rx.security.api.SecurityContract;
import hiconic.rx.security.web.api.WebSecurityContract;
import hiconic.rx.web.server.api.WebServerContract;
import tribefire.extension.shiro.config.RxFixedNewUserRoleProvider;
import tribefire.extension.shiro.config.RxLogin;
import tribefire.extension.shiro.config.RxMappedNewUserRoleProvider;
import tribefire.extension.shiro.config.RxNewUserRoleProvider;
import tribefire.extension.shiro.config.RxShiroBootstrappingWorker;
import tribefire.extension.shiro.config.RxShiroServiceProcessor;
import tribefire.extension.shiro.ini.ShiroIniLoader;
import tribefire.extension.shiro.processing.bootstrapping.Bootstrapping;
import tribefire.extension.shiro.processing.bootstrapping.BootstrappingWorker;
import tribefire.extension.shiro.processing.bootstrapping.CustomEnvironmentLoader;
import tribefire.extension.shiro.processing.bootstrapping.FixedNewUserRolesProvider;
import tribefire.extension.shiro.processing.bootstrapping.InMemorySessionDao;
import tribefire.extension.shiro.processing.bootstrapping.MappedNewUserRolesProvider;
import tribefire.extension.shiro.processing.bootstrapping.MulticastSessionDao;
import tribefire.extension.shiro.processing.bootstrapping.NodeSessionIdGenerator;
import tribefire.extension.shiro.processing.bootstrapping.StringBasedIniEnvironment;
import tribefire.extension.shiro.processing.bootstrapping.ini.ShiroIniFactory;
import tribefire.extension.shiro.processing.login.SessionValidatorServlet;
import tribefire.extension.shiro.processing.login.ShiroLoginServlet;
import tribefire.extension.shiro.processing.service.HealthCheckProcessor;
import tribefire.extension.shiro.processing.service.ShiroServiceProcessor;
import tribefire.extension.shiro.processing.util.ExternalIconUrlHelper;
import tribefire.extension.shiro.processing.util.ShiroTools;
import tribefire.extension.shiro.templates.api.RxShiroTemplateContext;

@Managed
public class RxShiroDeployablesSpace implements WireSpace {

	// @formatter:off
	@Import private RxPlatformContract platform;
	@Import private RxMarshallingContract marshalling;
	@Import private WebServerContract webServer;
	@Import private WebSecurityContract webSecurity;
	@Import private RxShiroSpace shiro;
	@Import private AccessContract access;
	@Import private SecurityContract security;
	// @formatter:on

	@Override
	public void onLoaded(WireContextConfiguration configuration) {
		WireSpace.super.onLoaded(configuration);
		multicastSessionDao();
		nodeSessionIdGenerator();
	}

	@Managed
	public ShiroServiceProcessor serviceProcessor(RxShiroTemplateContext context, RxShiroServiceProcessor deployable) {
		AccessContract accessContract = context.getAccessContract();

		ShiroServiceProcessor bean = new ShiroServiceProcessor();
		bean.setConfiguration(deployable.getConfiguration());
		bean.setPathIdentifier(deployable.getPathIdentifier());
		bean.setStaticImagesRelativePath("/res/login-images/");
		bean.setMulticastSessionDao(multicastSessionDao());
		bean.setAuthAccessIdSupplier(authenticationAccessIdSupplier());
		bean.setSessionFactory(accessContract.systemSessionFactory());
		bean.setShiroTools(shiroTools());
		bean.setObfuscateLogOutput(deployable.getObfuscateLogOutput());
		return bean;
	}

	@Managed
	private ShiroTools shiroTools() {
		ShiroTools bean = new ShiroTools();
		return bean;
	}

	@Managed
	public ShiroLoginServlet loginServlet(RxShiroTemplateContext context, RxLogin deployable) {
		ShiroLoginServlet bean = new ShiroLoginServlet();
		bean.setPublicServicesUrl(context.getPublicServicesUrl());
		bean.setApplicationName(platform.application().applicationName());
		bean.setCookieHandler(webSecurity.cookieHandler());
		bean.setRemoteAddressResolver(webServer.remoteAddressResolver());
		bean.setSystemAttributeContextSupplier(platform.auth().systemAttributeContextSupplier());
		bean.setUserService(security.userService());
		//bean.setSystemSessionFactory(access.systemSessionFactory());
		//bean.setAuthAccessIdSupplier(authenticationAccessIdSupplier());
		bean.setCreateUsers(deployable.getCreateUsers());
		bean.setConfiguration(deployable.getConfiguration());
		bean.setHttpClientProvider(clientProvider());
		bean.setUserAcceptList(deployable.getUserAcceptList());
		bean.setUserBlockList(deployable.getUserBlockList());
		bean.setNewUserRoleProvider(newUserRoleProvider(deployable.getNewUserRoleProvider()));
		bean.setShowStandardLoginForm(deployable.getShowStandardLoginForm());
		bean.setShowTextLinks(deployable.getShowTextLinks());
		bean.setPathIdentifier(deployable.getPathIdentifier());
		bean.setAddSessionParameter(deployable.getAddSessionParameterOnRedirect());
		bean.setStaticImagesRelativePath(ShiroConstants.STATIC_IMAGES_RELATIVE_PATH);
		bean.setEvaluator(platform.serviceProcessing().systemEvaluator());
		bean.setExternalIconUrlHelper(externalIconUrlHelper());
		bean.setObfuscateLogOutput(deployable.getObfuscateLogOutput());
		bean.setShiroTools(shiroTools());
		return bean;
	}

	@Managed
	private ExternalIconUrlHelper externalIconUrlHelper() {
		ExternalIconUrlHelper bean = new ExternalIconUrlHelper();
		bean.setHttpClientProvider(clientProvider());
		return bean;
	}

	@Managed
	public SessionValidatorServlet sessionValidatorServlet() {
		SessionValidatorServlet bean = new SessionValidatorServlet();
		bean.setRequestEvaluator(platform.serviceProcessing().evaluator());
		bean.setMarshallerRegistry(platform.marshalling().marshallers());
		return bean;
	}

	@Managed
	public BootstrappingWorker bootstrappingWorker(RxShiroBootstrappingWorker deployable) {
		BootstrappingWorker bean = new BootstrappingWorker();
		bean.setIdentification(deployable);
		bean.setConfiguration(deployable.getConfiguration());
		bean.setShiroIniFactory(iniFactory());
		bean.setProxyFilter(shiro.shiroProxyFilter());
		bean.setBootstrapping(bootstrapping());
		// bean.setLoginPathIdentifier(deployable.getLogin().getPathIdentifier());
		bean.setLoginPathIdentifier(deployable.getPathIdentifier());
		return bean;
	}

	@Managed
	private Bootstrapping bootstrapping() {
		Bootstrapping bean = new Bootstrapping();
		bean.setEnvironmentLoaderListener(environmentLoader());
		bean.setServletContextSupplier(webServer.servletContextSupplier());
		return bean;
	}

	@Managed
	private CustomEnvironmentLoader environmentLoader() {
		CustomEnvironmentLoader bean = new CustomEnvironmentLoader();
		bean.setIniEnvironment(iniEnvironment());
		return bean;
	}

	@Managed
	private StringBasedIniEnvironment iniEnvironment() {
		StringBasedIniEnvironment bean = new StringBasedIniEnvironment();
		bean.setIniConfigSupplier(iniFactory());
		return bean;
	}

	@Managed
	private DefaultSecurityManager securityManager() {
		DefaultSecurityManager bean = new DefaultSecurityManager();
		return bean;
	}

	@Managed
	private InMemorySessionDao inMemorySessionDao() {
		InMemorySessionDao bean = new InMemorySessionDao();
		return bean;
	}

	@Managed
	public ShiroIniFactory iniFactory() {
		ShiroIniFactory bean = new ShiroIniFactory();
		bean.setIniTemplate(ShiroIniLoader.loadIniTempalte());
		return bean;
	}

	@Managed
	private MulticastSessionDao multicastSessionDao() {
		MulticastSessionDao bean = new MulticastSessionDao();
		bean.setRequestEvaluator(platform.serviceProcessing().systemEvaluator());
		bean.setInstanceIdAsString(platform.application().instanceId().stringify());
		MulticastSessionDao.INSTANCE = bean;
		return bean;
	}

	@Managed
	private NodeSessionIdGenerator nodeSessionIdGenerator() {
		NodeSessionIdGenerator bean = new NodeSessionIdGenerator();
		bean.setInstanceIdAsString(platform.application().instanceId().stringify());
		NodeSessionIdGenerator.INSTANCE = bean;
		return bean;
	}

	private NewUserRoleProvider newUserRoleProvider(RxNewUserRoleProvider newUserRoleProvider) {
		if (newUserRoleProvider instanceof RxFixedNewUserRoleProvider)
			return fixedNewUserRolesProvider((RxFixedNewUserRoleProvider) newUserRoleProvider);

		if (newUserRoleProvider instanceof RxMappedNewUserRoleProvider)
			return mappedNewUserRolesProvider((RxMappedNewUserRoleProvider) newUserRoleProvider);

		throw new IllegalArgumentException("Unsupported new user role provider type: " + newUserRoleProvider.getClass().getName());
	}

	@Managed
	public FixedNewUserRolesProvider fixedNewUserRolesProvider(RxFixedNewUserRoleProvider deployable) {
		FixedNewUserRolesProvider bean = new FixedNewUserRolesProvider();
		bean.setConfiguredRoles(deployable.getRoles());

		return bean;
	}

	@Managed
	public MappedNewUserRolesProvider mappedNewUserRolesProvider(RxMappedNewUserRoleProvider deployable) {
		MappedNewUserRolesProvider bean = new MappedNewUserRolesProvider();
		bean.setConfiguredRoles(deployable.getMapping());
		bean.setFields(deployable.getFields());

		return bean;
	}

	@Managed
	public HealthCheckProcessor healthCheckProcessor() {
		HealthCheckProcessor bean = new HealthCheckProcessor();
		bean.setAuthAccessIdSupplier(authenticationAccessIdSupplier());
		return bean;
	}

	@Managed
	private Supplier<String> authenticationAccessIdSupplier() {
		// provide via context and fill with ENV variable if ever needed
		Supplier<String> bean = Holder.of("auth");
		return bean;
	}

	@Managed
	private HttpClientProvider clientProvider() {
		DefaultHttpClientProvider bean = new DefaultHttpClientProvider();
		bean.setSslSocketFactoryProvider(sslSocketFactoryProvider());
		return bean;
	}

	@Managed
	private SslSocketFactoryProvider sslSocketFactoryProvider() {
		// boolean acceptSslCertificates = TribefireRuntime.getAcceptSslCertificates();
		// TODO accept SSL certificates - maybe should be configurable?
		boolean acceptSslCertificates = true;

		SslSocketFactoryProvider bean = acceptSslCertificates ? //
				new EasySslSocketFactoryProvider() : //
				new StrictSslSocketFactoryProvider();

		return bean;
	}

}
