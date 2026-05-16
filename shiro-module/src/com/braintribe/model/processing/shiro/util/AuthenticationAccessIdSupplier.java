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
package com.braintribe.model.processing.shiro.util;

import java.util.function.Supplier;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.logging.Logger;
import com.braintribe.model.accessdeployment.IncrementalAccess;
import com.braintribe.model.cortex.deployment.CortexConfiguration;
import com.braintribe.model.processing.query.fluent.EntityQueryBuilder;
import com.braintribe.model.processing.session.api.persistence.PersistenceGmSession;

public class AuthenticationAccessIdSupplier implements Supplier<String> {

	private static Logger logger = Logger.getLogger(AuthenticationAccessIdSupplier.class);
	
	protected Supplier<PersistenceGmSession> cortexSessionProvider = null;
	protected String authenticationAccessId = null;

	@Override
	public String get() {
		initialize();
		return authenticationAccessId;
	}

	
	protected void initialize() {

		if (authenticationAccessId != null) {
			return;
		}
		PersistenceGmSession cortexSession = null;
		try {
			cortexSession = this.cortexSessionProvider.get();

			CortexConfiguration cc = cortexSession.query().entities(EntityQueryBuilder.from(CortexConfiguration.T).done()).first();
			if (cc != null) {
				IncrementalAccess authenticationAccess = cc.getAuthenticationAccess();
				if (authenticationAccess != null) {
					this.authenticationAccessId = authenticationAccess.getExternalId();
					logger.debug(() -> "Identified the authentication access: "+authenticationAccessId);
				}
			}
		} catch (Exception e) {
			logger.error("Could not get a session to the cortex access.", e);
		} finally {
			if (authenticationAccessId == null) {
				authenticationAccessId = "auth";
			}

		}
	}

	@Required @Configurable
	public void setCortexSessionProvider(Supplier<PersistenceGmSession> cortexSessionProvider) {
		this.cortexSessionProvider = cortexSessionProvider;
	}
	@Configurable
	public void setAuthenticationAccessId(String authenticationAccessId) {
		this.authenticationAccessId = authenticationAccessId;
	}

	

}
