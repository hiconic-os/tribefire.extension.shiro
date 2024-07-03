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

import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;

import tribefire.cortex.initializer.support.wire.space.AbstractInitializerSpace;
import tribefire.extension.shiro.initializer.wire.contract.ExistingInstancesContract;
import tribefire.extension.shiro.initializer.wire.contract.ShiroInitializerModelsContract;
import tribefire.extension.shiro.templates.api.ShiroTemplateContext;
import tribefire.extension.shiro.templates.wire.contract.ShiroMetaDataContract;

/**
 * @see {@link ShiroInitializerModelsContract}
 */
@Managed
public class ShiroInitializerModelsSpace extends AbstractInitializerSpace implements ShiroInitializerModelsContract {

	@Import
	private ExistingInstancesContract existingInstances;

	@Import
	private ShiroMetaDataContract metaData;

	@Import
	private ShiroInitializerSpace initializer;

	@Managed
	@Override
	public GmMetaModel configuredServiceModel() {
		ShiroTemplateContext context = initializer.defaultContext();
		return metaData.serviceModel(context);
	}

	@Managed
	@Override
	public GmMetaModel configuredDeploymentModel() {
		ShiroTemplateContext context = initializer.defaultContext();
		return metaData.deploymentModel(context);
	}
}
