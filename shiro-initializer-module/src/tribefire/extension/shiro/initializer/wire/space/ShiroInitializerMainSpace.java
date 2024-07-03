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
package tribefire.extension.shiro.initializer.wire.space;

import tribefire.cortex.initializer.support.integrity.wire.contract.CoreInstancesContract;
import tribefire.cortex.initializer.support.wire.space.AbstractInitializerSpace;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;

import tribefire.extension.shiro.initializer.wire.contract.ExistingInstancesContract;
import tribefire.extension.shiro.initializer.wire.contract.ShiroInitializerContract;
import tribefire.extension.shiro.initializer.wire.contract.ShiroInitializerMainContract;
import tribefire.extension.shiro.initializer.wire.contract.ShiroInitializerModelsContract;
import tribefire.extension.shiro.initializer.wire.contract.ShiroRuntimePropertiesContract;

/**
 * @see {@link ShiroInitializerMainContract}
 */
@Managed
public class ShiroInitializerMainSpace extends AbstractInitializerSpace implements ShiroInitializerMainContract {

	@Import
	private ShiroInitializerModelsContract models;
	
	@Import
	private ExistingInstancesContract existingInstances;
	
	@Import
	private CoreInstancesContract coreInstances;
	
	@Import
	private ShiroRuntimePropertiesContract properties;
	
	@Import
	private ShiroInitializerContract shiroInitializer;
	
	@Override
	public ShiroInitializerModelsContract initializerModelsContract() {
		return models;
	}

	@Override
	public ExistingInstancesContract existingInstancesContract() {
		return existingInstances;
	}
	
	@Override
	public CoreInstancesContract coreInstancesContract() {
		return coreInstances;
	}

	@Override
	public ShiroRuntimePropertiesContract propertiesContract() {
		return properties;
	}

	@Override
	public ShiroInitializerContract shiroInitializerContract() {
		return shiroInitializer;
	}
}
