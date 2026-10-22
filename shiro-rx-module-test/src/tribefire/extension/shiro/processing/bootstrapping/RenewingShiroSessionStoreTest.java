package tribefire.extension.shiro.processing.bootstrapping;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import org.junit.Test;

public class RenewingShiroSessionStoreTest {

	@Test
	public void movesAttributesAndTimeoutToNewSession() {
		SimpleSession oldSession = session("old", 42_000L);
		oldSession.setAttribute("oidc-profile", "profile");
		SimpleSession newSession = session("new", 1_000L);

		RenewingShiroSessionStore store = new TestSessionStore(oldSession, newSession);

		assertThat(store.renewSession(null)).isTrue();
		assertThat(oldSession.isValid()).isFalse();
		assertThat(newSession.getTimeout()).isEqualTo(42_000L);
		assertThat(newSession.getAttribute("oidc-profile")).isEqualTo("profile");
	}

	private static SimpleSession session(String id, long timeout) {
		SimpleSession session = new SimpleSession();
		session.setId(id);
		session.setTimeout(timeout);
		return session;
	}

	private static class TestSessionStore extends RenewingShiroSessionStore {
		private final Session oldSession;
		private final Session newSession;

		private TestSessionStore(Session oldSession, Session newSession) {
			this.oldSession = oldSession;
			this.newSession = newSession;
		}

		@Override
		protected Session getSession(boolean createSession) {
			return createSession ? newSession : oldSession;
		}
	}
}
