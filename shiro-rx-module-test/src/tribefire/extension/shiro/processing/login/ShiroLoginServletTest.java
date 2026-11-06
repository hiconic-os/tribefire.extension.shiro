package tribefire.extension.shiro.processing.login;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Test;

import com.braintribe.common.attribute.AttributeContext;
import com.braintribe.model.processing.service.common.context.UserSessionAspect;
import com.braintribe.model.processing.securityservice.api.attributes.OpenUserSessionEntryPointAttribute;
import com.braintribe.model.security.service.config.OpenUserSessionEntryPoint;
import com.braintribe.model.securityservice.OpenUserSession;
import com.braintribe.model.securityservice.credentials.GrantedCredentials;
import com.braintribe.model.usersession.UserSession;
import com.braintribe.utils.collection.impl.AttributeContexts;

import jakarta.servlet.http.HttpServletRequest;

public class ShiroLoginServletTest {

	@Test
	public void anchorsResolvedEntryPointInTrustedContext() {
		UserSession systemSession = UserSession.T.create();
		systemSession.setSessionId("system-session");
		AttributeContext systemContext = AttributeContexts.derivePeek()
				.set(UserSessionAspect.class, systemSession)
				.build();

		TestShiroLoginServlet servlet = new TestShiroLoginServlet();
		servlet.setSystemAttributeContextSupplier(() -> systemContext);
		OpenUserSessionEntryPoint entryPoint = OpenUserSessionEntryPoint.T.create();
		entryPoint.setName("platform");
		servlet.setEntryPointProvider(request -> entryPoint);

		OpenUserSessionEntryPoint resolvedEntryPoint = servlet.resolveEntryPoint(null);
		AttributeContexts.push(AttributeContexts.derivePeek()
				.set(OpenUserSessionEntryPointAttribute.class, resolvedEntryPoint)
				.build());
		try {
			OpenUserSession request = servlet.createOpenUserSessionRequest("entra-user", null);

			assertThat(request.getEntryPoint()).isNull();
			assertThat(request.getCredentials()).isInstanceOf(GrantedCredentials.class);
			assertThat(AttributeContexts.peek().findOrNull(OpenUserSessionEntryPointAttribute.class)).isSameAs(entryPoint);
		} finally {
			servlet.popContext();
		}
	}

	@Test
	public void redirectsInterventionsToConfiguredCommonLogin() {
		TestShiroLoginServlet servlet = new TestShiroLoginServlet();
		servlet.setPublicServicesUrl("https://example.test/services");
		servlet.setDefaultLoginPath("login");
		Map<String, String> parameters = new LinkedHashMap<>();
		parameters.put("loginInterventionState", "ACTION_REQUIRED");
		parameters.put("loginInterventionMessage", "Approve this device & browser");

		assertThat(servlet.loginUrl(parameters)).isEqualTo(
				"https://example.test/services/login?loginInterventionState=ACTION_REQUIRED&loginInterventionMessage=Approve+this+device+%26+browser");
	}

	@Test
	public void loginTemplateParses() throws Exception {
		String resource = "/tribefire/extension/shiro/processing/login/login.html.vm";
		try (InputStream in = ShiroLoginServlet.class.getResourceAsStream(resource)) {
			assertThat(in).as("login template resource").isNotNull();
			String template = new String(in.readAllBytes(), StandardCharsets.UTF_8);
			assertThat(new VelocityEngine().evaluate(new VelocityContext(), new StringWriter(), resource, template)).isTrue();
		}
	}

	private static class TestShiroLoginServlet extends ShiroLoginServlet {
		@Override
		protected OpenUserSessionEntryPoint resolveEntryPoint(HttpServletRequest request) {
			return super.resolveEntryPoint(request);
		}

		@Override
		protected OpenUserSession createOpenUserSessionRequest(String user, HttpServletRequest request) {
			return super.createOpenUserSessionRequest(user, request);
		}

		@Override
		protected String loginUrl(Map<String, String> parameters) {
			return super.loginUrl(parameters);
		}
	}
}
