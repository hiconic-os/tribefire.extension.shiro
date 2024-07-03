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

import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.braintribe.utils.StringTools;

public class IdTokenContent {

	public String token;
	public String firstName;
	public String lastName;
	public String fullName;
	public String username;
	public String email;
	public String subject;
	public String audience;
	public Date expiration;
	public Date issuedAt;
	public String issuer;
	public Set<String> roles;
	public Map<String, Object> claims;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Subject: " + subject + "\n");
		sb.append("Audience: " + audience + "\n");
		sb.append("Expiration: " + expiration + "\n");
		sb.append("Issed At: " + issuedAt + "\n");
		sb.append("Issuer: " + issuer + "\n");
		sb.append("Roles: " + roles + "\n");
		sb.append("Claims: " + claims + "\n");
		sb.append("Username: " + username + "\n");
		sb.append("First Name: " + firstName + "\n");
		sb.append("Last Name: " + lastName + "\n");
		sb.append("Full Name: " + fullName + "\n");
		sb.append("Email: " + email + "\n");
		return StringTools.asciiBoxMessage(sb.toString());
	}
}