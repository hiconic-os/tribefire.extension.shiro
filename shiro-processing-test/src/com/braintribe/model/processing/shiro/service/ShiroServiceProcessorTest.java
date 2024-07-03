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

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;

import com.braintribe.model.processing.shiro.util.ShiroTools;
import com.braintribe.model.shiro.service.EnsureUserByIdToken;
import com.braintribe.model.shiro.service.EnsuredUser;
import com.braintribe.model.user.User;

import io.jsonwebtoken.Jwts;

public class ShiroServiceProcessorTest {

	@Test
	public void testIdTokenParsing() throws Exception {

		EnsureUserByIdToken request = EnsureUserByIdToken.T.create();

		Map<String, Object> claims = new HashMap<>();
		claims.put("email", "test@example.com");
		claims.put("first_name", "C.");
		claims.put("last_name", "Cortex");

		Instant now = Instant.now();
		//@formatter:off
		String jwt = Jwts.builder()
		        .setAudience("https://braintribe.okta.com/oauth2/v1/token")
		        .setIssuedAt(Date.from(now))
		        .setExpiration(Date.from(now.plus(5L, ChronoUnit.MINUTES)))
		        .setIssuer("0oa160j8din1F60Dh4x7")
		        .setSubject("testuser")
		        .setId(UUID.randomUUID().toString())
		        .addClaims(claims)
		        .compact();
		//@formatter:on

		request.setIdToken(jwt);

		ShiroServiceProcessor proc = new ShiroServiceProcessor() {
			@Override
			protected User ensureUser(String username, String firstName, String lastName, String email, Set<String> roles) {
				User newUser = User.T.create();
				newUser.setName(username);
				newUser.setFirstName(firstName);
				newUser.setLastName(lastName);
				newUser.setEmail(email);
				return newUser;
			}
		};

		proc.setShiroTools(new ShiroTools()); // But why?

		EnsuredUser result = proc.ensureUserByIdToken(null, request);
		assertThat(result).isNotNull();
		assertThat(result.getSuccess()).isTrue();
		User ensuredUser = result.getEnsuredUser();
		assertThat(ensuredUser).isNotNull();
		assertThat(ensuredUser.getFirstName()).isEqualTo("C.");
		assertThat(ensuredUser.getLastName()).isEqualTo("Cortex");
		assertThat(ensuredUser.getEmail()).isEqualTo("test@example.com");
		assertThat(ensuredUser.getName()).isEqualTo("testuser");

	}
}
