package tribefire.extension.shiro.wire.space;

import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;

import hiconic.rx.check.api.CheckContract;
import hiconic.rx.check.model.aspect.CheckCoverage;
import hiconic.rx.check.model.aspect.CheckLatency;
import hiconic.rx.module.api.config.RxPlatformConfigurator;
import hiconic.rx.module.api.service.ModelConfigurations;
import hiconic.rx.module.api.wire.RxModuleContract;
import hiconic.rx.module.api.wire.RxPlatformContract;
import hiconic.rx.security.web.api.WebSecurityConfigurationContract;
import hiconic.rx.web.server.api.FilterSymbol;
import hiconic.rx.web.server.api.WebServerContract;
import hiconic.rx.worker.api.WorkerContract;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServlet;
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
	@Import private WebSecurityConfigurationContract webSecurityConfiguration;
	@Import private WebServerContract webServer;

	@Import private RxShiroSpace shiro;
	@Import private RxShiroInitializerSpace shiroInitializer;
	@Import private RxShiroDeployablesSpace shiroDeployables;
	// @formatter:on

	enum ShiroFilters implements FilterSymbol {
		shiroFilter,

	}

	@Override
	public void configureModels(ModelConfigurations configurations) {
		if (!isShiroEnabled())
			return;

		shiroInitializer.metaData(configurations);
	}

	@Override
	public void configurePlatform(RxPlatformConfigurator configurator) {
		if (!isShiroEnabled())
			return;

		webSecurityConfiguration.setDefaultLoginPath(shiroConfiguration().getServletPath());
	}

	@Override
	public void onDeploy() {
		if (!isShiroEnabled())
			return;

		registerFilter();
		registerLoginServlet();
		registerBootstrappingWorker();
		registerSessionValidatorServlet();
		registerCheckProcessor();
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