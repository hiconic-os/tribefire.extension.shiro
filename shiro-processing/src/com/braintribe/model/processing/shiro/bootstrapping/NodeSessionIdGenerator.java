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
package com.braintribe.model.processing.shiro.bootstrapping;

import java.io.Serializable;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.utils.RandomTools;

public class NodeSessionIdGenerator implements SessionIdGenerator {

	public static NodeSessionIdGenerator INSTANCE;

	protected String instanceIdAsString;

	@Override
	public Serializable generateId(Session session) {
		return INSTANCE.generateIdInternal(session);
	}

	public Serializable generateIdInternal(Session session) {
		String id = RandomTools.newStandardUuid() + "@" + instanceIdAsString;
		if (session instanceof SimpleSession) {
			((SimpleSession) session).setId(id);
		}

		return null;
	}
	@Configurable
	@Required
	public void setInstanceIdAsString(String instanceIdAsString) {
		this.instanceIdAsString = instanceIdAsString;
	}

}
