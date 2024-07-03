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
package com.braintribe.model.processing.shiro;

public interface ShiroConstants {

	String CARTRIDGE_GROUPID = "tribefire.extension.shiro";

	String MODULE_NAME = "shiro-module";

	String CARTRIDGE_EXTERNALID = CARTRIDGE_GROUPID + ".shiro-cartridge";

	String CARTRIDGE_GLOBAL_ID = "cartridge:" + CARTRIDGE_EXTERNALID;

	String MODULE_GLOBAL_ID = "module://" + CARTRIDGE_GROUPID + ":" + MODULE_NAME;

	String DEPLOYMENT_MODEL_QUALIFIEDNAME = CARTRIDGE_GROUPID + ":shiro-deployment-model";
	String SERVICE_MODEL_QUALIFIEDNAME = CARTRIDGE_GROUPID + ":shiro-service-model";

	String SHIRO_LOGIN_EXTERNALID = "shiro.login.terminal";

	int MAJOR_VERSION = 3;

	String PATH_IDENTIFIER = "remote-login";

	String STATIC_IMAGES_RELATIVE_PATH = "/res/login-images/";
}
