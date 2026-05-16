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
package tribefire.extension.shiro.config;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.shiro.deployment.ShiroAuthenticationConfiguration;

public interface RxShiroBootstrappingWorker extends GenericEntity {

	EntityType<RxShiroBootstrappingWorker> T = EntityTypes.T(RxShiroBootstrappingWorker.class);

	ShiroAuthenticationConfiguration getConfiguration();
	void setConfiguration(ShiroAuthenticationConfiguration configuration);

	// Login was only needed for its Pathidentifier
	// Login getLogin();
	// void setLogin(Login login);

	@Mandatory
	String getPathIdentifier();
	void setPathIdentifier(String pathIdentifier);

}
