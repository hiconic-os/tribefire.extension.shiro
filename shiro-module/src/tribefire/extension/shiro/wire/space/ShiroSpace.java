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
package tribefire.extension.shiro.wire.space;

import java.io.File;

import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.web.servlet.ShiroFilter;

import com.braintribe.model.processing.shiro.bootstrapping.Bootstrapping;
import com.braintribe.model.processing.shiro.bootstrapping.CustomEnvironmentLoader;
import com.braintribe.model.processing.shiro.bootstrapping.InMemorySessionDao;
import com.braintribe.model.processing.shiro.bootstrapping.ShiroProxyFilter;
import com.braintribe.model.processing.shiro.bootstrapping.StringBasedIniEnvironment;
import com.braintribe.model.processing.shiro.bootstrapping.ini.ShiroIniFactory;
import com.braintribe.utils.FileTools;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;
import com.braintribe.wire.api.space.WireSpace;

import tribefire.module.wire.contract.ModuleResourcesContract;
import tribefire.module.wire.contract.TribefireWebPlatformContract;

@Managed
public class ShiroSpace implements WireSpace {

	@Import
	private TribefireWebPlatformContract tfPlatform;

	@Import
	private ModuleResourcesContract moduleResources;

	@Managed
	public Bootstrapping bootstrapping() {
		Bootstrapping bean = new Bootstrapping();
		bean.setEnvironmentLoaderListener(environmentLoader());
		bean.setServletContext(tfPlatform.servlets().context());
		return bean;
	}

	@Managed
	public ShiroProxyFilter shiroProxyFilter() {
		ShiroProxyFilter bean = new ShiroProxyFilter();
		bean.setDelegateSupplier(this::shiroFilter);
		return bean;
	}

	@Managed
	private ShiroFilter shiroFilter() {
		ShiroFilter bean = new ShiroFilter();
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

		File iniFile = moduleResources.resource("shiro.ini.vm").asFile();
		String template = FileTools.readStringFromFile(iniFile, "UTF-8");

		bean.setIniTemplate(template);
		return bean;
	}

}
