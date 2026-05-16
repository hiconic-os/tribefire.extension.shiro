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
package tribefire.extension.shiro.processing.service;

import java.util.function.Supplier;

import com.braintribe.cfg.Required;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.utils.lcd.StringTools;

import hiconic.rx.check.api.CheckProcessor;
import hiconic.rx.check.model.result.CheckResult;
import hiconic.rx.check.model.result.CheckResultEntry;
import hiconic.rx.check.model.result.CheckStatus;

public class HealthCheckProcessor implements CheckProcessor {

	private Supplier<String> authAccessIdSupplier;

	@Required
	public void setAuthAccessIdSupplier(Supplier<String> authAccessIdSupplier) {
		this.authAccessIdSupplier = authAccessIdSupplier;
	}

	@Override
	public CheckResult check(ServiceRequestContext requestContext) {
		CheckResult response = CheckResult.T.create();

		String authId = authAccessIdSupplier.get();
		
		// TODO improve check, see that the access actually exists...

		CheckResultEntry entry = CheckResultEntry.T.create();
		entry.setCheckStatus(!StringTools.isBlank(authId) ? CheckStatus.ok : CheckStatus.fail);
		entry.setName("Authentication");
		entry.setDetails("Connected to authentication access: " + authId);

		response.getEntries().add(entry);

		return response;
	}

}
