package tribefire.extension.shiro.processing.login;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Test;

import com.braintribe.common.attribute.AttributeContext;
import com.braintribe.model.shiro.deployment.ShiroAuthenticationConfiguration;
import com.braintribe.model.shiro.deployment.client.ShiroAzureAdClient;
import com.braintribe.model.processing.service.common.context.UserSessionAspect;
import com.braintribe.model.processing.securityservice.api.attributes.OpenUserSessionEntryPointAttribute;
import com.braintribe.model.security.service.config.OpenUserSessionEntryPoint;
import com.braintribe.model.securityservice.OpenUserSession;
import com.braintribe.model.securityservice.credentials.GrantedCredentials;
import com.braintribe.model.usersession.UserSession;
import com.braintribe.utils.collection.impl.AttributeContexts;

import hiconic.rx.security.web.api.WebSecurityConstants;

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
	public void keepsNativeMessagesOnConfiguredCommonLogin() {
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
	public void keepsExternalInterventionsOnTheShiroDialog() {
		TestShiroLoginServlet servlet = new TestShiroLoginServlet();
		servlet.setPublicServicesUrl("https://example.test/services");
		servlet.setPathIdentifier("component/remote-login");
		ShiroAzureAdClient client = ShiroAzureAdClient.T.create();
		client.setName("EntraID");
		Map<String, String> parameters = new LinkedHashMap<>();
		parameters.put("loginInterventionState", "ACTION_REQUIRED");
		parameters.put("loginInterventionMessage", "Approve this device & browser");

		assertThat(servlet.loginUrl(client, parameters)).isEqualTo(
				"https://example.test/services/component/remote-login?loginInterventionState=ACTION_REQUIRED&loginInterventionMessage=Approve+this+device+%26+browser");
	}

	@Test
	public void rendersExternalApprovalAsAnIsolatedTerminalFlow() {
		TestShiroLoginServlet servlet = new TestShiroLoginServlet();
		servlet.setPublicServicesUrl("https://example.test/services");
		servlet.setPathIdentifier("component/remote-login");
		servlet.setShowStandardLoginForm(true);
		ShiroAzureAdClient client = ShiroAzureAdClient.T.create();
		client.setName("EntraID");
		client.setActive(true);
		ShiroAuthenticationConfiguration configuration = ShiroAuthenticationConfiguration.T.create();
		configuration.getClients().add(client);
		servlet.setConfiguration(configuration);

		VelocityContext context = servlet.context(request(Map.of(
				WebSecurityConstants.REQUEST_PARAM_LOGIN_INTERVENTION_STATE, "ACTION_REQUIRED",
				WebSecurityConstants.REQUEST_PARAM_LOGIN_OPTION, "EntraID",
				WebSecurityConstants.REQUEST_PARAM_LOGIN_ACTION, "request-device-browser-approval",
				WebSecurityConstants.REQUEST_PARAM_LOGIN_INTERVENTION_ACTION_LABEL, "Request approval",
				WebSecurityConstants.REQUEST_PARAM_CONTINUE, "https://example.test/app")));

		assertThat(context.get("externalIntervention")).isEqualTo(true);
		assertThat(context.get("showStandardLoginForm")).isEqualTo(false);
		assertThat(context.get("interventionActionUrl")).isEqualTo(
				"https://example.test/services/component/remote-login/action/entraid?loginAction=request-device-browser-approval&continue=https%3A%2F%2Fexample.test%2Fapp");
		assertThat(context.get("externalRetryUrl")).isEqualTo(
				"https://example.test/services/component/remote-login/auth/entraid?continue=https%3A%2F%2Fexample.test%2Fapp");
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
		VelocityContext context(HttpServletRequest request) {
			return createContext(request, null);
		}

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

		@Override
		protected String loginUrl(com.braintribe.model.shiro.deployment.ShiroClient client, Map<String, String> parameters) {
			return super.loginUrl(client, parameters);
		}
	}

	private static HttpServletRequest request(Map<String, String> parameters) {
		return (HttpServletRequest) Proxy.newProxyInstance(HttpServletRequest.class.getClassLoader(),
				new Class<?>[] { HttpServletRequest.class }, (proxy, method, args) -> {
					if (method.getName().equals("getParameter"))
						return parameters.get(args[0]);
					Class<?> type = method.getReturnType();
					if (!type.isPrimitive())
						return null;
					if (type == boolean.class)
						return false;
					if (type == char.class)
						return '\0';
					return 0;
				});
	}
}
