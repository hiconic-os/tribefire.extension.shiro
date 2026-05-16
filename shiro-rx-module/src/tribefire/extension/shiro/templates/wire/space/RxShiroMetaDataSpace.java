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
package tribefire.extension.shiro.templates.wire.space;

import com.braintribe.model.shiro.service.ShiroRequest;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;
import com.braintribe.wire.api.space.WireSpace;

import hiconic.rx.model.service.processing.md.ProcessWith;
import hiconic.rx.module.api.service.ModelConfiguration;
import hiconic.rx.module.api.service.ModelConfigurations;
import tribefire.extension.shiro._ShiroServiceModel_;
import tribefire.extension.shiro.templates.api.RxShiroTemplateContext;

@Managed
public class RxShiroMetaDataSpace implements WireSpace {

	@Import
	private RxShiroTemplatesSpace shiroTemplates;

//	@Managed
//	public GmMetaModel deploymentModel(RxShiroTemplateContext context) {
//		GmMetaModel rawDataModel = (GmMetaModel) context.lookup("model:" + ShiroConstants.DEPLOYMENT_MODEL_QUALIFIEDNAME);
//		GmMetaModel model = context.create(GmMetaModel.T, InstanceConfiguration.currentInstance());
//		setModelDetails(model, ShiroConstants.DEPLOYMENT_MODEL_QUALIFIEDNAME + "-" + normalizeName(context), rawDataModel);
//		return model;
//
//	}

//	private static String normalizeName(RxShiroTemplateContext context) {
//		String name = context.getName();
//		if (name == null) {
//			throw new IllegalArgumentException("The context does not contain a name.");
//		}
//		String newName = name.toLowerCase().replace(' ', '.');
//		newName = newName.replace('/', '-');
//		return newName;
//	}

	public void metaData(RxShiroTemplateContext context, ModelConfigurations configurations) {
		ModelConfiguration serviceModel = serviceModel(configurations);
		
		serviceModel.configureModel(modelEditor -> {
			ProcessWith processWithMetadata = ProcessWith.T.create();
			processWithMetadata.setAssociate(shiroTemplates.serviceRequestProcessor(context));
			modelEditor.onEntityType(ShiroRequest.T).addMetaData(processWithMetadata);
			
		});
	}

	@Managed
	private ModelConfiguration serviceModel(ModelConfigurations configurations) {
		return configurations.configuredModel(_ShiroServiceModel_.reflection);
	}

}
