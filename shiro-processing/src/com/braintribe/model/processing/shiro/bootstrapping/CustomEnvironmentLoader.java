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
import org.apache.shiro.web.env.WebEnvironment;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.logging.Logger;
import com.braintribe.model.processing.shiro.bootstrapping.ini.ShiroIniFactory;

/**
 * Extension of the {@link EnvironmentLoader} that allows for an external supplier of
 * an INI file. This file content will be provided by the {@link ShiroIniFactory}.
 */
public class CustomEnvironmentLoader extends EnvironmentLoader {

	private static Logger logger = Logger.getLogger(CustomEnvironmentLoader.class);

	private StringBasedIniEnvironment iniEnvironmentSupplier;
	
	@Override
	protected WebEnvironment determineWebEnvironment(ServletContext servletContext) {
		if (iniEnvironmentSupplier != null) {
			
			logger.debug(() -> "IniEnvironmentSupplier used: "+iniEnvironmentSupplier.toString());
			
			return iniEnvironmentSupplier;
		} else {
			
			logger.debug(() -> "No IniEnvironmentSupplier available.");
			
			return super.determineWebEnvironment(servletContext);
		}
	}

	@Required
	@Configurable
	public void setIniEnvironment(StringBasedIniEnvironment iniEnvironmentSupplier) {
		this.iniEnvironmentSupplier = iniEnvironmentSupplier;
	}

}
