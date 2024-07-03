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
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.eis.SessionDAO;

/**
 * Most simple implementation of the {@link SessionDAO} interface that stores all sessions in a local map.
 * This is merely for testing or when you can be sure that no cluster environment will be in place.
 */
public class InMemorySessionDao implements SessionDAO {

	private ConcurrentHashMap<String,Session> sessionMap = new ConcurrentHashMap<>();
	
	@Override
	public Serializable create(Session session) {
		String id = UUID.randomUUID().toString();
		((SimpleSession) session).setId(id);
		sessionMap.put(id, session);
		return id;
	}

	@Override
	public Session readSession(Serializable sessionId) throws UnknownSessionException {
		return sessionMap.get(sessionId.toString());
	}

	@Override
	public void update(Session session) throws UnknownSessionException {
		sessionMap.put(session.getId().toString(), session);
	}

	@Override
	public void delete(Session session) {
		sessionMap.remove(session.getId().toString());
	}

	@Override
	public Collection<Session> getActiveSessions() {
		return sessionMap.values();
	}

	@Override
	public String toString() {
		return "InMemorySessionDao with "+sessionMap.size()+" entries.";
	}
}
