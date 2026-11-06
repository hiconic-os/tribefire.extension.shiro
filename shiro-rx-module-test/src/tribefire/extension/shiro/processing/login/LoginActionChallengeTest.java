package tribefire.extension.shiro.processing.login;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.Test;

import tribefire.extension.shiro.processing.login.LoginActionChallenge.PendingLoginAction;

public class LoginActionChallengeTest {
	private static final Instant NOW = Instant.parse("2026-11-06T16:00:00Z");
	private static final String SECRET = "shared-oidc-client-secret";

	@Test
	public void roundTripsWithoutServerSideSessionState() {
		LoginActionChallenge issuer = new LoginActionChallenge(Clock.fixed(NOW, ZoneOffset.UTC));
		String token = issuer.create("request-device-browser-approval", "EntraID", "https://example.test/app", SECRET);

		LoginActionChallenge verifier = new LoginActionChallenge(Clock.fixed(NOW.plusSeconds(30), ZoneOffset.UTC));
		PendingLoginAction action = verifier.verify(token, SECRET);

		assertThat(action).isNotNull();
		assertThat(action.actionId()).isEqualTo("request-device-browser-approval");
		assertThat(action.clientName()).isEqualTo("EntraID");
		assertThat(action.continuePath()).isEqualTo("https://example.test/app");
	}

	@Test
	public void rejectsTamperingAndWrongClientSecret() {
		LoginActionChallenge challenge = new LoginActionChallenge(Clock.fixed(NOW, ZoneOffset.UTC));
		String token = challenge.create("request-device-browser-approval", "EntraID", null, SECRET);

		assertThat(challenge.verify(token.substring(0, token.length() - 1) + "A", SECRET)).isNull();
		assertThat(challenge.verify(token, "another-client-secret")).isNull();
	}

	@Test
	public void rejectsExpiredChallenges() {
		LoginActionChallenge issuer = new LoginActionChallenge(Clock.fixed(NOW, ZoneOffset.UTC));
		String token = issuer.create("request-device-browser-approval", "EntraID", null, SECRET);

		LoginActionChallenge verifier = new LoginActionChallenge(Clock.fixed(NOW.plusSeconds(301), ZoneOffset.UTC));
		assertThat(verifier.verify(token, SECRET)).isNull();
	}
}
