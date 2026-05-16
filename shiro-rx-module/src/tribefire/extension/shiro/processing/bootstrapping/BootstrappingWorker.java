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
package tribefire.extension.shiro.processing.bootstrapping;

import com.braintribe.exception.Exceptions;
import com.braintribe.logging.Logger;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.processing.worker.api.Worker;
import com.braintribe.model.processing.worker.api.WorkerContext;
import com.braintribe.model.processing.worker.api.WorkerException;
import com.braintribe.model.shiro.deployment.ShiroAuthenticationConfiguration;
import com.braintribe.utils.lcd.StringTools;

import tribefire.extension.shiro.processing.bootstrapping.ini.ShiroIniFactory;

/**
 * Worker that is responsible for finalize the bootstrapping of the Shiro module after everything has been setup and server is running. The
 * functionality will not be available before this worker has been started.
 */
public class BootstrappingWorker implements Worker {

	private static Logger logger = Logger.getLogger(BootstrappingWorker.class);

	private GenericEntity identification;
	private ShiroIniFactory shiroIniFactory;
	private ShiroProxyFilter proxyFilter;
	private ShiroAuthenticationConfiguration configuration;
	private Bootstrapping bootstrapping;
	private String loginPathIdentifier;

	@Override
	public GenericEntity getWorkerIdentification() {
		return identification;
	}

	@Override
	public void start(WorkerContext workerContext) throws WorkerException {

		logger.debug(() -> "Starting BootstrappingWorker");

		if (configuration == null || StringTools.isBlank(configuration.getCallbackUrl()) || StringTools.isBlank(configuration.getUnauthorizedUrl()))
			throw new IllegalStateException(
					"The configuration must be set and must contain a callbackUrl and an unauthorizedUrl. Configuration: " + configuration);

		logger.debug(() -> "Setting the configuration in the ShiroIni Factory");

		shiroIniFactory.setConfiguration(configuration);

		logger.debug(() -> "Setting the path identifier in the ShiroIni Factory: " + loginPathIdentifier);

		shiroIniFactory.setLoginServletPath(loginPathIdentifier);

		logger.debug(() -> "Initiating the bootstrapping of Shiro");

		try {
			bootstrapping.start();
		} catch (Throwable e) {
			logger.error("Error while bootstrapping Shiro: " + e.getMessage(), e);
			throw Exceptions.unchecked(e);
		}

		logger.debug(
				() -> "Done with finalizing the configuration. Activating the proxy filter (fallbackUrl: " + configuration.getFallbackUrl() + ").");

		proxyFilter.setPublicServicesUrl(configuration.getPublicServicesUrl());
		proxyFilter.setServletPath(configuration.getServletPath());
		proxyFilter.setFallbackUrl(configuration.getFallbackUrl());
		proxyFilter.activate();

		logger.debug(() -> "Done with executing the BootstrappingWorker");
	}

	@Override
	public void stop(WorkerContext workerContext) throws WorkerException {
		// Nothing to do
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	public void setIdentification(GenericEntity identification) {
		this.identification = identification;
	}
	public void setShiroIniFactory(ShiroIniFactory shiroIniFactory) {
		this.shiroIniFactory = shiroIniFactory;
	}
	public void setProxyFilter(ShiroProxyFilter proxyFilter) {
		this.proxyFilter = proxyFilter;
	}
	public void setConfiguration(ShiroAuthenticationConfiguration configuration) {
		this.configuration = configuration;
	}
	public void setBootstrapping(Bootstrapping bootstrapping) {
		this.bootstrapping = bootstrapping;
	}
	public void setLoginPathIdentifier(String loginPathIdentifier) {
		this.loginPathIdentifier = loginPathIdentifier;
	}

}
