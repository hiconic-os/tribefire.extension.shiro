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

import static com.braintribe.utils.lcd.CollectionTools2.asList;
import static com.braintribe.utils.lcd.CollectionTools2.asSet;

import java.util.Set;

import javax.servlet.DispatcherType;

import com.braintribe.model.processing.deployment.api.binding.DenotationBindingBuilder;
import com.braintribe.model.processing.shiro.ShiroConstants;
import com.braintribe.model.processing.shiro.bootstrapping.NewUserRoleProvider;
import com.braintribe.model.shiro.deployment.FixedNewUserRoleProvider;
import com.braintribe.model.shiro.deployment.HealthCheckProcessor;
import com.braintribe.model.shiro.deployment.Login;
import com.braintribe.model.shiro.deployment.MappedNewUserRoleProvider;
import com.braintribe.model.shiro.deployment.SessionValidator;
import com.braintribe.model.shiro.deployment.ShiroBootstrappingWorker;
import com.braintribe.model.shiro.deployment.ShiroServiceProcessor;
import com.braintribe.web.impl.registry.ConfigurableFilterRegistration;
import com.braintribe.web.impl.registry.ConfigurableUrlPatternFilterMapping;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;

import tribefire.module.wire.contract.TribefireModuleContract;
import tribefire.module.wire.contract.TribefireWebPlatformContract;
import tribefire.module.wire.contract.WebPlatformBindersContract;
import tribefire.module.wire.contract.WebPlatformHardwiredDeployablesContract;

@Managed
public class ShiroModuleSpace implements TribefireModuleContract {

	@Import
	private TribefireWebPlatformContract tfPlatform;

	@Import
	private WebPlatformBindersContract commonComponents;

	@Import
	private ShiroDeployablesSpace deployables;

	@Import
	private WebPlatformHardwiredDeployablesContract webPlatform;

	@Import
	private ShiroSpace shiro;

	@Override
	public void bindHardwired() {

		ConfigurableFilterRegistration bean = new ConfigurableFilterRegistration();
		bean.setName("ShiroFilter");
		bean.setFilter(shiro.shiroProxyFilter());

		ConfigurableUrlPatternFilterMapping filterMapping = new ConfigurableUrlPatternFilterMapping();
		Set<DispatcherType> dispatcherTypesSet = asSet(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ERROR);
		filterMapping.setDispatcherTypes(dispatcherTypesSet);
		filterMapping.setUrlPatterns(asList("/component/" + ShiroConstants.PATH_IDENTIFIER + "/auth/*"));

		bean.setMappings(asList(filterMapping));

		webPlatform.webRegistry().addFilter(bean);
	}

	@Override
	public void bindDeployables(DenotationBindingBuilder bindings) {

		//@formatter:off
		bindings.bind(ShiroServiceProcessor.T)
					.component(commonComponents.serviceProcessor())
					.expertFactory(deployables::serviceProcessor);

		bindings.bind(Login.T)
					.component(commonComponents.webTerminal())
					.expertFactory(deployables::loginServlet);

		bindings.bind(SessionValidator.T)
					.component(commonComponents.webTerminal())
					.expertFactory(deployables::sessionValidatorServlet);

		bindings.bind(ShiroBootstrappingWorker.T)
					.component(commonComponents.worker())
					.expertFactory(deployables::bootstrappingWorker);

		bindings.bind(FixedNewUserRoleProvider.T)
					.component(com.braintribe.model.shiro.deployment.NewUserRoleProvider.T, NewUserRoleProvider.class)
					.expertFactory(deployables::fixedNewUserRolesProvider);

		bindings.bind(MappedNewUserRoleProvider.T)
					.component(com.braintribe.model.shiro.deployment.NewUserRoleProvider.T, NewUserRoleProvider.class)
					.expertFactory(deployables::mappedNewUserRolesProvider);
				
		bindings.bind(HealthCheckProcessor.T).component(commonComponents.checkProcessor())
					.expertFactory(this.deployables::healthCheckProcessor);
				
		//@formatter:on

	}
}
