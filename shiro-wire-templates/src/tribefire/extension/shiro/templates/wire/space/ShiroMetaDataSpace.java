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
package tribefire.extension.shiro.templates.wire.space;

import com.braintribe.logging.Logger;
import com.braintribe.model.extensiondeployment.meta.ProcessWith;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.meta.editor.BasicModelMetaDataEditor;
import com.braintribe.model.processing.shiro.ShiroConstants;
import com.braintribe.model.shiro.service.ShiroRequest;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;
import com.braintribe.wire.api.scope.InstanceConfiguration;
import com.braintribe.wire.api.space.WireSpace;

import tribefire.extension.shiro.templates.api.ShiroTemplateContext;
import tribefire.extension.shiro.templates.wire.contract.ShiroMetaDataContract;
import tribefire.extension.shiro.templates.wire.contract.ShiroTemplatesContract;

@Managed
public class ShiroMetaDataSpace implements WireSpace, ShiroMetaDataContract {

	private static final Logger logger = Logger.getLogger(ShiroMetaDataSpace.class);

	@Import
	private ShiroTemplatesContract shiroTemplates;

	@Override
	@Managed
	public GmMetaModel serviceModel(ShiroTemplateContext context) {
		GmMetaModel model = context.create(GmMetaModel.T, InstanceConfiguration.currentInstance());
		GmMetaModel rawServiceModel = (GmMetaModel) context.lookup("model:" + ShiroConstants.SERVICE_MODEL_QUALIFIEDNAME);
		setModelDetails(model, ShiroConstants.SERVICE_MODEL_QUALIFIEDNAME + "-" + normalizeName(context), rawServiceModel);
		return model;
	}

	@Override
	@Managed
	public GmMetaModel deploymentModel(ShiroTemplateContext context) {
		GmMetaModel rawDataModel = (GmMetaModel) context.lookup("model:" + ShiroConstants.DEPLOYMENT_MODEL_QUALIFIEDNAME);
		GmMetaModel model = context.create(GmMetaModel.T, InstanceConfiguration.currentInstance());
		setModelDetails(model, ShiroConstants.DEPLOYMENT_MODEL_QUALIFIEDNAME + "-" + normalizeName(context), rawDataModel);
		return model;

	}

	private static String normalizeName(ShiroTemplateContext context) {
		String name = context.getName();
		if (name == null) {
			throw new IllegalArgumentException("The context does not contain a name.");
		}
		String newName = name.toLowerCase().replace(' ', '.');
		newName = newName.replace('/', '-');
		return newName;
	}

	@Override
	public void metaData(ShiroTemplateContext context) {

		GmMetaModel serviceModel = serviceModel(context);

		BasicModelMetaDataEditor modelEditor = new BasicModelMetaDataEditor(serviceModel);

		ProcessWith processWithMetadata = processWithShiroServiceProcessor(context);
		processWithMetadata.setProcessor(shiroTemplates.serviceRequestProcessor(context));

		modelEditor.onEntityType(ShiroRequest.T).addMetaData(processWithMetadata);

		GmMetaModel cortexServiceModel = context.lookup("model:tribefire.cortex:tribefire-cortex-service-model");
		cortexServiceModel.getDependencies().add(serviceModel);
	}

	@Managed
	private ProcessWith processWithShiroServiceProcessor(ShiroTemplateContext context) {
		ProcessWith bean = context.create(ProcessWith.T, InstanceConfiguration.currentInstance());
		return bean;
	}

	private static void setModelDetails(GmMetaModel targetModel, String modelName, GmMetaModel... dependencies) {
		targetModel.setName(modelName);
		targetModel.setVersion(ShiroConstants.MAJOR_VERSION + ".0");
		if (dependencies != null) {
			for (GmMetaModel dependency : dependencies) {
				if (dependency != null) {
					targetModel.getDependencies().add(dependency);
				}
			}
		}
	}
}
