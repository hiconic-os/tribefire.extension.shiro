// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
//
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
package com.braintribe.model.processing.shiro.bootstrapping;

import javax.servlet.ServletContext;

import org.apache.shiro.web.env.EnvironmentLoader;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.LifecycleAware;
import com.braintribe.cfg.Required;
import com.braintribe.logging.Logger;

/**
 * This class takes care of initializing the environment loader (via LifecycleAware mechanics). It also assigns the
 * custom session DAO.
 */
public class Bootstrapping implements LifecycleAware {

	private static Logger logger = Logger.getLogger(Bootstrapping.class);

	private ServletContext servletContext;
	private CustomEnvironmentLoader environmentLoaderListener;

	public void start() {

		logger.debug(() -> "Starting the initEnvironment method of the environmentLoaderListener: " + environmentLoaderListener);

		String ENVIRONMENT_ATTRIBUTE_KEY = EnvironmentLoader.class.getName() + ".ENVIRONMENT_ATTRIBUTE_KEY";
		servletContext.removeAttribute(ENVIRONMENT_ATTRIBUTE_KEY);

		environmentLoaderListener.initEnvironment(servletContext);

		logger.debug(() -> "Done with initializing the WebEnvironment");
	}

	public void stop() {
		logger.debug(() -> "Shutting down the WebEnvironment");
		environmentLoaderListener.destroyEnvironment(servletContext);
	}

	@Required
	@Configurable
	public void setEnvironmentLoaderListener(CustomEnvironmentLoader environmentLoaderListener) {
		this.environmentLoaderListener = environmentLoaderListener;
	}

	@Required
	@Configurable
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Override
	public void postConstruct() {
		// Will be done by the Bootstrapping Worker
	}

	@Override
	public void preDestroy() {
		stop();
	}
}
