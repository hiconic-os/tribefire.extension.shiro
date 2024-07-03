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

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.exception.Exceptions;
import com.braintribe.logging.Logger;
import com.braintribe.utils.DateTools;
import com.braintribe.utils.StringTools;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;

public class ShiroTools {

	private static Logger logger = Logger.getLogger(ShiroTools.class);

	private ClassLoader moduleClassLoader;

	public record TokenContent(Header header, Claims body) {
	}

	public void printUser(String source) {
		StringBuilder sb = new StringBuilder(source + " (" + DateTools.getCurrentDateString() + ")\n\n");

		Subject user = SecurityUtils.getSubject();
		if (user != null) {

			Session session = user.getSession();
			sb.append("Session: " + session + "\n");
			sb.append("User authenticated: " + user.isAuthenticated() + "\n");

			// if (user.isAuthenticated()) {
			PrincipalCollection principals = user.getPrincipals();
			if (principals != null && !principals.isEmpty()) {
				principals.forEach(p -> {
					sb.append("Principal: " + p + " (" + p.getClass() + ")\n");
				});
			}
			// } else {
			// sb.append("not authenticated\n");
			// }
			sb.append(user.toString() + "\n");
		} else {
			sb.append("no user\n");
		}
		System.out.println(StringTools.asciiBoxMessage(sb.toString(), -1));
	}

	/**
	 * Parses a JWT token without checking the signature AT ALL.
	 * 
	 * @param token
	 *            The full token to be parsed.
	 * @param silently
	 *            Indicated how an error should be treated. When true, only logging will be done in case of an error, If
	 *            false, any exception will be re-thrown.
	 * @return A TokenContent object containing the header and the payload. If silently is set to true, it may also be null
	 *         in case of an error.
	 */
	public TokenContent parseTokenWithoutValidation(String token, boolean silently) {

		ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(moduleClassLoader);
		try {
			String[] tokenParts = StringTools.splitString(token, ".");
			token.split(".");

			String headerBase64 = "eyJhbGciOiJub25lIn0="; // {"alg":"none"}
			String tokenForParsing = headerBase64 + "." + tokenParts[1] + ".";

			Jwt jwt = Jwts.parser().unsecured().build().parseUnsecuredClaims(tokenForParsing);

			Claims body = (Claims) jwt.getPayload();
			Header header = jwt.getHeader();

			return new TokenContent(header, body);

		} catch (ExpiredJwtException e) {
			String msg = "Not accepting an expired token: " + token;
			if (silently) {
				logger.warn(msg);
				return null;
			} else {
				Exceptions.contextualize(e, msg);
				throw e;
			}
		} catch (Exception e) {
			String msg = "Error while parsing potential JWT token: " + token;
			if (silently) {
				logger.debug(msg, e);
				return null;
			} else {
				throw Exceptions.unchecked(e, msg);
			}
		} finally {
			Thread.currentThread().setContextClassLoader(oldClassLoader);
		}
	}

	@Configurable
	@Required
	public void setModuleClassLoader(ClassLoader moduleClassLoader) {
		this.moduleClassLoader = moduleClassLoader;
	}
}
