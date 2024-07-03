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
package tribefire.extension.shiro.templates.wire.contract;

import java.util.List;

import com.braintribe.model.extensiondeployment.check.CheckBundle;
import com.braintribe.model.processing.shiro.ShiroAuthenticationUrl;
import com.braintribe.model.shiro.deployment.Login;
import com.braintribe.model.shiro.deployment.SessionValidator;
import com.braintribe.model.shiro.deployment.ShiroAuthenticationConfiguration;
import com.braintribe.model.shiro.deployment.ShiroBootstrappingWorker;
import com.braintribe.model.shiro.deployment.ShiroServiceProcessor;
import com.braintribe.wire.api.space.WireSpace;

import tribefire.extension.shiro.templates.api.ShiroTemplateContext;

public interface ShiroTemplatesContract extends WireSpace {

	ShiroServiceProcessor serviceRequestProcessor(ShiroTemplateContext context);

	ShiroAuthenticationConfiguration authenticationConfiguration(ShiroTemplateContext context);

	Login login(ShiroTemplateContext context);

	ShiroBootstrappingWorker bootstrappingWorker(ShiroTemplateContext context);

	CheckBundle functionalCheckBundle(ShiroTemplateContext context);

	SessionValidator sessionValidator(ShiroTemplateContext context);

	List<ShiroAuthenticationUrl> getAuthenticationUrls(ShiroTemplateContext context);
}
