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

import java.util.function.Supplier;

import org.apache.shiro.web.env.EnvironmentLoader;

import com.braintribe.cfg.LifecycleAware;
import com.braintribe.cfg.Required;
import com.braintribe.logging.Logger;

import jakarta.servlet.ServletContext;

/**
 * This class takes care of initializing the environment loader (via LifecycleAware mechanics). It also assigns the custom session DAO.
 */
public class Bootstrapping implements LifecycleAware {

	private static Logger logger = Logger.getLogger(Bootstrapping.class);

	private Supplier<ServletContext> servletContextSupplier;
	private CustomEnvironmentLoader environmentLoaderListener;

	public void start() {
		logger.debug(() -> "Starting the initEnvironment method of the environmentLoaderListener: " + environmentLoaderListener);

		String ENVIRONMENT_ATTRIBUTE_KEY = EnvironmentLoader.class.getName() + ".ENVIRONMENT_ATTRIBUTE_KEY";
		servletContext().removeAttribute(ENVIRONMENT_ATTRIBUTE_KEY);

		environmentLoaderListener.initEnvironment(servletContext());
		logger.debug(() -> "Done with initializing the WebEnvironment");
	}

	public void stop() {
		logger.debug(() -> "Shutting down the WebEnvironment");
		environmentLoaderListener.destroyEnvironment(servletContext());
	}

	@Required
	public void setEnvironmentLoaderListener(CustomEnvironmentLoader environmentLoaderListener) {
		this.environmentLoaderListener = environmentLoaderListener;
	}

	@Required
		public void setServletContextSupplier(Supplier<ServletContext> servletContextSupplier) {
		this.servletContextSupplier = servletContextSupplier;
	}

	@Override
	public void postConstruct() {
		// Will be done by the Bootstrapping Worker
	}

	@Override
	public void preDestroy() {
		stop();
	}

	private ServletContext servletContext() {
		return servletContextSupplier.get();
	}
	
}
