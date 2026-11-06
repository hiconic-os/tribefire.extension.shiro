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
package tribefire.extension.shiro.processing.login;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

final class LoginActionChallenge {
	private static final String VERSION = "1";
	private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
	private static final Base64.Decoder DECODER = Base64.getUrlDecoder();
	private static final Duration LIFETIME = Duration.ofMinutes(5);
	private static final int MAX_VALUE_LENGTH = 2_048;

	private final Clock clock;

	LoginActionChallenge() {
		this(Clock.systemUTC());
	}

	LoginActionChallenge(Clock clock) {
		this.clock = clock;
	}

	String create(String actionId, String clientName, String continuePath, String secret) {
		requireValue("actionId", actionId);
		requireValue("clientName", clientName);
		requireSecret(secret);
		String payload = String.join(".", VERSION, Long.toString(clock.instant().plus(LIFETIME).getEpochSecond()), encode(actionId),
				encode(clientName), encode(continuePath == null ? "" : continuePath), encode(UUID.randomUUID().toString()));
		return payload + "." + encode(sign(payload, secret));
	}

	PendingLoginAction verify(String token, String secret) {
		requireSecret(secret);
		if (token == null || token.length() > 8_192)
			return null;
		String[] parts = token.split("\\.", -1);
		if (parts.length != 7 || !VERSION.equals(parts[0]))
			return null;
		String payload = String.join(".", parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
		byte[] suppliedSignature;
		try {
			suppliedSignature = DECODER.decode(parts[6]);
		} catch (IllegalArgumentException e) {
			return null;
		}
		if (!MessageDigest.isEqual(sign(payload, secret), suppliedSignature))
			return null;
		try {
			long expiresAt = Long.parseLong(parts[1]);
			if (clock.instant().getEpochSecond() > expiresAt)
				return null;
			return new PendingLoginAction(decode(parts[2]), decode(parts[3]), emptyToNull(decode(parts[4])));
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	private static String encode(String value) {
		if (value.length() > MAX_VALUE_LENGTH)
			throw new IllegalArgumentException("Login action challenge value is too long");
		return ENCODER.encodeToString(value.getBytes(StandardCharsets.UTF_8));
	}

	private static String encode(byte[] value) {
		return ENCODER.encodeToString(value);
	}

	private static String decode(String value) {
		return new String(DECODER.decode(value), StandardCharsets.UTF_8);
	}

	private static byte[] sign(String payload, String secret) {
		try {
			Mac mac = Mac.getInstance("HmacSHA256");
			byte[] key = MessageDigest.getInstance("SHA-256")
					.digest(("hiconic-shiro-login-action\0" + secret).getBytes(StandardCharsets.UTF_8));
			mac.init(new SecretKeySpec(key, "HmacSHA256"));
			return mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException("Required login action challenge algorithms are unavailable", e);
		}
	}

	private static void requireValue(String name, String value) {
		if (value == null || value.isBlank())
			throw new IllegalArgumentException(name + " must not be blank");
	}

	private static void requireSecret(String secret) {
		if (secret == null || secret.isBlank())
			throw new IllegalArgumentException("External authentication client secret must not be blank");
	}

	private static String emptyToNull(String value) {
		return value.isEmpty() ? null : value;
	}

	record PendingLoginAction(String actionId, String clientName, String continuePath) {
	}
}
