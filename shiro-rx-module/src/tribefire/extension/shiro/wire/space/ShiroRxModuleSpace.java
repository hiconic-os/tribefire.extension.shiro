package tribefire.extension.shiro.wire.space;

import java.util.Locale;

import com.braintribe.model.shiro.deployment.ShiroAuthenticationConfiguration;
import com.braintribe.model.shiro.deployment.ShiroClient;
import com.braintribe.model.shiro.deployment.client.ShiroAzureAdClient;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;

import hiconic.rx.check.api.CheckContract;
import hiconic.rx.check.model.aspect.CheckCoverage;
import hiconic.rx.check.model.aspect.CheckLatency;
import hiconic.rx.module.api.config.RxPlatformConfigurator;
import hiconic.rx.module.api.service.ModelConfigurations;
import hiconic.rx.module.api.service.ServiceDomainConfigurations;
import hiconic.rx.module.api.wire.RxModuleContract;
import hiconic.rx.module.api.wire.RxPlatformContract;
import hiconic.rx.security.web.api.LoginOption;
import hiconic.rx.security.web.api.WebSecurityExtensionContract;
import hiconic.rx.web.server.api.FilterSymbol;
import hiconic.rx.web.server.api.WebServerContract;
import hiconic.rx.worker.api.WorkerContract;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServlet;
import tribefire.extension.shiro._ShiroServiceModel_;
import tribefire.extension.shiro.ShiroRxConstants;
import tribefire.extension.shiro.config.RxLogin;
import tribefire.extension.shiro.config.RxServlet;
import tribefire.extension.shiro.config.RxSessionValidator;
import tribefire.extension.shiro.config.RxShiroBootstrappingWorker;
import tribefire.extension.shiro.configuration.model.ShiroConfiguration;
import tribefire.extension.shiro.processing.bootstrapping.BootstrappingWorker;
import tribefire.extension.shiro.processing.login.SessionValidatorServlet;
import tribefire.extension.shiro.processing.login.ShiroLoginServlet;
import tribefire.extension.shiro.processing.service.ShiroCheck;
import tribefire.extension.shiro.templates.wire.space.RxShiroDeployablesSpace;
import tribefire.extension.shiro.templates.wire.space.RxShiroSpace;
import tribefire.extension.shiro.wire.space.initializer.RxShiroInitializerSpace;

/**
 * Shiro extension provides single sign-on integration via the apache shiro library.
 */
@Managed
public class ShiroRxModuleSpace implements RxModuleContract {

	// @formatter:off
	@Import private RxPlatformContract platform;
	@Import private CheckContract check;
	@Import private WorkerContract worker;
	@Import private WebSecurityExtensionContract webSecurity;
	@Import private WebServerContract webServer;

	@Import private RxShiroSpace shiro;
	@Import private RxShiroInitializerSpace shiroInitializer;
	@Import private RxShiroDeployablesSpace shiroDeployables;
	// @formatter:on

	enum ShiroFilters implements FilterSymbol {
		shiroFilter,

	}

	private boolean loginOptionsRegistered;

	@Override
	public void configureModels(ModelConfigurations configurations) {
		if (!isShiroEnabled())
			return;

		shiroInitializer.metaData(configurations);
	}

	@Override
	public void configureServiceDomains(ServiceDomainConfigurations configurations) {
		if (!isShiroEnabled())
			return;

		// Cluster session requests are dispatched through multicast and must be resolvable on every Shiro-enabled node.
		configurations.internal().addModel(_ShiroServiceModel_.reflection);
	}

	@Override
	public void configurePlatform(RxPlatformConfigurator configurator) {
		if (!isShiroEnabled())
			return;

		registerLoginOptions();
	}

	private void registerLoginOptions() {
		if (loginOptionsRegistered)
			return;

		loginOptionsRegistered = true;
		RxLogin login = shiroInitializer.loginDenotation();
		ShiroAuthenticationConfiguration configuration = login.getConfiguration();
		for (ShiroClient client : configuration.getClients()) {
			if (!Boolean.TRUE.equals(client.getActive()))
				continue;

			String clientPath = client.getName().toLowerCase(Locale.ROOT);
			String targetPath = login.getPathIdentifier() + "/auth/" + clientPath;
			String imageName = client instanceof ShiroAzureAdClient ? "azuread" : clientPath;
			String iconPath = ShiroRxConstants.LOGIN_IMAGES + "/" + imageName + ".png";
			webSecurity.registerLoginOption(new LoginOption(client.getName(), client.getName(), targetPath, iconPath));
		}
	}

	@Override
	public void onDeploy() {
		if (!isShiroEnabled())
			return;

		registerFilter();
		registerLoginImages();
		registerLoginServlet();
		registerBootstrappingWorker();
		registerSessionValidatorServlet();
		registerCheckProcessor();
	}

	/** The login pages address these by URL, so the folder this module owns is mapped under the very same path segment. */
	private void registerLoginImages() {
		webServer.addPackagedWebResources(ShiroRxConstants.LOGIN_IMAGES, ShiroRxConstants.LOGIN_IMAGES, ShiroRxConstants.LOGIN_IMAGES);
	}

	private void registerFilter() {
		RxLogin login = shiroInitializer.loginDenotation();

		webServer.addFilter(ShiroFilters.shiroFilter, shiro.shiroProxyFilter());
		String url = "/" + login.getPathIdentifier() + "/auth/*";
		webServer.addFilterMapping(ShiroFilters.shiroFilter, url, DispatcherType.REQUEST);
	}

	private void registerBootstrappingWorker() {
		RxShiroBootstrappingWorker denotation = shiroInitializer.bootstrappingWorker();
		BootstrappingWorker bootstrappingWorker = shiroDeployables.bootstrappingWorker(denotation);

		worker.manager().deploy(bootstrappingWorker);
	}

	private void registerLoginServlet() {
		RxLogin login = shiroInitializer.loginDenotation();
		ShiroLoginServlet servlet = shiroDeployables.loginServlet(shiroInitializer.defaultContext(), login);
		registerServlet(login, servlet);
	}

	private void registerSessionValidatorServlet() {
		RxSessionValidator sessionValidator = shiroInitializer.sessionValidatorDenotation();
		SessionValidatorServlet servlet = shiroDeployables.sessionValidatorServlet();
		registerServlet(sessionValidator, servlet);
	}

	private void registerServlet(RxServlet denotation, HttpServlet servlet) {
		webServer.addServlet(denotation.getName(), denotation.getPathIdentifier() + "/*", servlet);
	}

	private void registerCheckProcessor() {
		check.checkProcessorRegistry().registerProcessor( //
				ShiroCheck.shiroAuthAcessAvailable, //
				shiroInitializer.healthCheckProcessor(), //
				CheckCoverage.functional, //
				CheckLatency.immediate);
	}

	private boolean isShiroEnabled() {
		return shiroConfiguration().getEnabled();
	}

	@Managed
	public ShiroConfiguration shiroConfiguration() {
		return platform.configuration().readConfig(ShiroConfiguration.T).get();
	}
}
