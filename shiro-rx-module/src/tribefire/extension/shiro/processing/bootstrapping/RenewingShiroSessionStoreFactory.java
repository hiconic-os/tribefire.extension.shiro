package tribefire.extension.shiro.processing.bootstrapping;

import org.pac4j.core.context.FrameworkParameters;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.context.session.SessionStoreFactory;

/** Supplies the Shiro session store used by pac4j. */
public class RenewingShiroSessionStoreFactory implements SessionStoreFactory {

	private static final SessionStore INSTANCE = new RenewingShiroSessionStore();

	@Override
	public SessionStore newSessionStore(FrameworkParameters parameters) {
		return INSTANCE;
	}
}
