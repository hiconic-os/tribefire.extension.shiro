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
package tribefire.extension.shiro.initializer.wire.contract;

import com.braintribe.model.extensiondeployment.check.CheckBundle;
import com.braintribe.model.shiro.deployment.Login;
import com.braintribe.model.shiro.deployment.SessionValidator;
import com.braintribe.model.shiro.deployment.ShiroAuthenticationConfiguration;
import com.braintribe.model.shiro.deployment.ShiroBootstrappingWorker;
import com.braintribe.model.shiro.deployment.ShiroServiceProcessor;
import com.braintribe.wire.api.space.WireSpace;

/**
 * <p>
 * This {@link WireSpace Wire contract} exposes our custom instances (e.g. instances of our deployables).
 * </p>
 */
public interface ShiroInitializerContract extends WireSpace {

	void metaData();

	ShiroAuthenticationConfiguration authenticationConfiguration();

	Login login();

	SessionValidator sessionValidator();

	ShiroBootstrappingWorker bootstrappingWorker();

	ShiroServiceProcessor serviceRequestProcessor();

	CheckBundle functionalCheckBundle();

}
