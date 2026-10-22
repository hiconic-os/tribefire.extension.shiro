package tribefire.extension.shiro.processing.bootstrapping;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.shiro.session.Session;
import org.pac4j.core.context.WebContext;

import io.buji.pac4j.context.ShiroSessionStore;

/** Shiro session store with the session-fixation protection expected by pac4j's callback logic. */
public class RenewingShiroSessionStore extends ShiroSessionStore {

	@Override
	public boolean renewSession(WebContext context) {
		Session oldSession = getSession(false);
		if (oldSession == null)
			return false;

		Map<Object, Object> attributes = new LinkedHashMap<>();
		if (oldSession.getAttributeKeys() != null)
			for (Object key : oldSession.getAttributeKeys())
				attributes.put(key, oldSession.getAttribute(key));
		long timeout = oldSession.getTimeout();

		oldSession.stop();

		Session newSession = getSession(true);
		newSession.setTimeout(timeout);
		attributes.forEach(newSession::setAttribute);
		return true;
	}
}
