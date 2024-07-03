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
package com.braintribe.model.processing.shiro.service;

import java.util.List;
import java.util.function.Supplier;

import com.braintribe.cfg.Required;
import com.braintribe.logging.Logger;
import com.braintribe.model.check.service.CheckRequest;
import com.braintribe.model.check.service.CheckResult;
import com.braintribe.model.check.service.CheckResultEntry;
import com.braintribe.model.check.service.CheckStatus;
import com.braintribe.model.processing.check.api.CheckProcessor;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.utils.lcd.StringTools;

public class HealthCheckProcessor implements CheckProcessor {

	private static final Logger logger = Logger.getLogger(HealthCheckProcessor.class);

	private Supplier<String> authAccessIdSupplier;

	@Override
	public CheckResult check(ServiceRequestContext requestContext, CheckRequest request) {

		CheckResult response = CheckResult.T.create();

		List<CheckResultEntry> entries = response.getEntries();
		
		String authId = authAccessIdSupplier.get();
		
		CheckResultEntry entry = CheckResultEntry.T.create();
		entry.setCheckStatus(!StringTools.isBlank(authId) ? CheckStatus.ok : CheckStatus.fail);
		entry.setName("Authentication");
		entry.setDetails("Connected to authentication access: "+authId);
		
		entries.add(entry);

		return response;
	}

	@Required
	public void setAuthAccessIdSupplier(Supplier<String> authAccessIdSupplier) {
		this.authAccessIdSupplier = authAccessIdSupplier;
	}


}
