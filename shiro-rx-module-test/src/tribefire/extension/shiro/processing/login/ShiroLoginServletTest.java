package tribefire.extension.shiro.processing.login;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.braintribe.common.attribute.AttributeContext;
import com.braintribe.model.processing.service.common.context.UserSessionAspect;
import com.braintribe.model.securityservice.OpenUserSession;
import com.braintribe.model.usersession.UserSession;
import com.braintribe.utils.collection.impl.AttributeContexts;

import jakarta.servlet.http.HttpServletRequest;

public class ShiroLoginServletTest {

	@Test
	public void anchorsResolvedEntryPointOnOpenUserSession() {
		UserSession systemSession = UserSession.T.create();
		systemSession.setSessionId("system-session");
		AttributeContext systemContext = AttributeContexts.derivePeek()
				.set(UserSessionAspect.class, systemSession)
				.build();

		TestShiroLoginServlet servlet = new TestShiroLoginServlet();
		servlet.setSystemAttributeContextSupplier(() -> systemContext);
		servlet.setEntryPointProvider(request -> "platform");

		OpenUserSession request = servlet.createOpenUserSessionRequest("entra-user", null);

		assertThat(request.getEntryPoint()).isEqualTo("platform");
	}

	private static class TestShiroLoginServlet extends ShiroLoginServlet {
		@Override
		protected OpenUserSession createOpenUserSessionRequest(String user, HttpServletRequest request) {
			return super.createOpenUserSessionRequest(user, request);
		}
	}
}
