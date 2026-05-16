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

import static com.braintribe.utils.lcd.CollectionTools2.isEmpty;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.velocity.VelocityContext;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oidc.profile.OidcProfile;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.codec.Codec;
import com.braintribe.codec.CodecException;
import com.braintribe.codec.string.MapCodec;
import com.braintribe.codec.string.UrlEscapeCodec;
import com.braintribe.common.attribute.AttributeContext;
import com.braintribe.common.attribute.common.Waypoint;
import com.braintribe.common.lcd.Pair;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.logging.Logger;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.i18n.LocalizedString;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.processing.securityservice.api.exceptions.AuthenticationException;
import com.braintribe.model.processing.service.api.aspect.RequestedEndpointAspect;
import com.braintribe.model.processing.service.api.aspect.RequestorAddressAspect;
import com.braintribe.model.processing.service.common.context.UserSessionAspect;
import com.braintribe.model.processing.shiro.bootstrapping.NewUserRoleProvider;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.securityservice.OpenUserSession;
import com.braintribe.model.securityservice.OpenUserSessionResponse;
import com.braintribe.model.securityservice.credentials.ExistingSessionCredentials;
import com.braintribe.model.securityservice.credentials.GrantedCredentials;
import com.braintribe.model.securityservice.credentials.identification.UserNameIdentification;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.model.shiro.deployment.FieldEncoding;
import com.braintribe.model.shiro.deployment.HasRolesField;
import com.braintribe.model.shiro.deployment.ShiroAuthenticationConfiguration;
import com.braintribe.model.shiro.deployment.ShiroClient;
import com.braintribe.model.shiro.deployment.client.ShiroInstagramOAuth20Client;
import com.braintribe.model.user.Role;
import com.braintribe.model.user.User;
import com.braintribe.model.usersession.UserSession;
import com.braintribe.transport.http.HttpClientProvider;
import com.braintribe.utils.StringTools;
import com.braintribe.utils.collection.impl.AttributeContexts;
import com.braintribe.utils.i18n.I18nTools;
import com.braintribe.utils.lcd.CollectionTools2;
import com.nimbusds.oauth2.sdk.token.AccessToken;

import dev.hiconic.servlet.api.remote.RemoteAddressInformation;
import dev.hiconic.servlet.api.remote.RemoteClientAddressResolver;
import dev.hiconic.servlet.impl.remote.DefaultRemoteClientAddressResolver;
import hiconic.rx.security.api.UserService;
import hiconic.rx.security.web.api.CookieHandler;
import hiconic.rx.security.web.api.WebSecurityConstants;
import hiconic.rx.servlet.velocity.BasicTemplateBasedServlet;
import io.buji.pac4j.subject.Pac4jPrincipal;
import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.minidev.json.JSONArray;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import tribefire.extension.shiro.processing.util.ExternalIconUrlHelper;
import tribefire.extension.shiro.processing.util.ShiroTools;
import tribefire.extension.shiro.processing.util.ShiroTools.TokenContent;

public class ShiroLoginServlet extends BasicTemplateBasedServlet {

	private static final long serialVersionUID = -1L;

	private static Logger logger = Logger.getLogger(ShiroLoginServlet.class);

	public final static String TRIBEFIRE_RUNTIME_OFFER_STAYSIGNED = "TRIBEFIRE_RUNTIME_OFFER_STAYSIGNED";

	private Codec<Map<String, String>, String> urlParamCodec;

	private String publicServicesUrl;
	private String applicationName;
	private final boolean offerStaySigned = true;
	private RemoteClientAddressResolver remoteAddressResolver;
	private boolean addSessionUrlParameter = false;

	private UserService userService;
	private Supplier<AttributeContext> systemAttributeContextSupplier;

	private ShiroAuthenticationConfiguration configuration;

	@SuppressWarnings("unused")
	private HttpClientProvider httpClientProvider;

	private NewUserRoleProvider newUserRoleProvider;

	private boolean createUsers = false;
	private Set<String> userAcceptList;
	private Set<String> userBlockList;

	private boolean showStandardLoginForm = true;
	private boolean showTextLinks = false;
	private String staticImagesRelativePath;
	private String pathIdentifier;

	private CookieHandler cookieHandler;

	private Evaluator<ServiceRequest> evaluator;

	private ExternalIconUrlHelper externalIconUrlHelper;

	// TODO support icons
	@SuppressWarnings("unused")
	private final ConcurrentHashMap<String, String> iconContentCache = new ConcurrentHashMap<>();

	private boolean obfuscateLogOutput = true;

	private ShiroTools shiroTools;

	@Override
	public void init() throws ServletException {
		setRelativeTemplateLocation("login.html.vm");
		super.init();
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// resp.setHeader("Cache-Control", "no-cache, no-store");
		resp.setHeader("Expires", "Tue, 03 Jul 2001 06:00:00 GMT");
		resp.setDateHeader("Last-Modified", new Date().getTime());
		resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
		resp.setHeader("Pragma", "no-cache");

		String showLogin = req.getParameter("showLogin");
		if (showLogin != null && showLogin.equalsIgnoreCase("true")) {
			logger.debug(() -> "The showLogin parameter is true. Hence, showing the login dialog and don't do any further action.");
			super.service(req, resp);
			return;
		}

		boolean authenticated = false;

		Subject user = null;
		try {
			user = SecurityUtils.getSubject();
		} catch (UnavailableSecurityManagerException nosm) {
			// Ignore
		}

		if (user != null) {

			logger.debug("Subject: " + user);

			PrincipalCollection principals = user.getPrincipals();
			if (principals != null && !principals.isEmpty()) {

				logger.debug(() -> "At least one principal found.");

				Iterator<Object> iterator = principals.iterator();

				while (iterator != null && iterator.hasNext()) {

					Object principal = iterator.next();
					if (principal instanceof Pac4jPrincipal) {

						logger.debug(() -> "Principal is of the expected type.");

						Pac4jPrincipal pp = (Pac4jPrincipal) principal;
						UserProfile cp = pp.getProfile();
						Map<String, Object> attributeMap = getAttributeMap(cp);

						ShiroClient shiroClient = getShiroClient(cp);
						String username = shiroClient != null ? getUsername(shiroClient, attributeMap) : null;

						logPrincipal(cp, attributeMap, username, shiroClient);

						// We do not to use Shiro to maintain the user session; this is our turf
						SecurityUtils.getSecurityManager().logout(user);

						if (username != null && acceptUsername(username) && ensureUser(username, shiroClient, attributeMap)) {

							logger.debug(() -> "Authenticating user: " + username);

							initializeContext(req);
							try {
								OpenUserSession authRequest = createOpenUserSessionRequest(username, req);

								enrichOpenUserSessionRequest(authRequest, shiroClient, attributeMap);

								UserSession session = null;
								try {

									// Do authentication.
									session = authenticate(resp, authRequest);
									if (session == null) {
										logger.debug("Authentication of user " + username + " failed.");
										return; // Response message is already handled in authenticate.
									}

									final String sessionId = session.getSessionId();
									logger.debug(() -> "Successfully authenticated user: " + username + " with session: " + sessionId);

									String continuePath = acquireContinuePath(req);
									String redirectUrl = continuePath;

									cookieHandler.ensureCookie(req, resp, sessionId);

									if (addSessionUrlParameter) {
										redirectUrl = handleUrl(sessionId, continuePath);
									}

									deleteShiroSessionCookies(req, resp);

									String requestUri = req.getRequestURI();
									if (requestUri != null && requestUri.toLowerCase().endsWith("/sessionid")) {

										returnSessionId(resp, sessionId);

									} else {

										// redirect to next side.
										resp.sendRedirect(redirectUrl);
									}

									authenticated = true;

								} catch (Exception e) {
									throw new RuntimeException("Error authenticating to tribefire.", e);
								}
							} finally {
								popContext();
							}

						} else {
							logger.debug(() -> "User " + username + " is either not null or does not exist in the 'auth' Access.");
						}
					} else {
						logger.debug(() -> "Principal " + principal + " is not of the expected type.");
					}
				} // Iterator over all principals
			} else {
				logger.debug(() -> "No principals found");
			}
		} else {
			logger.debug(() -> "No subject found.");
		}

		if (!authenticated) {

			deleteShiroSessionCookies(req, resp);

			String continuePath = configuration.getUnauthenticatedUrl();
			if (!StringTools.isBlank(continuePath)) {
				logger.debug(() -> "No user has been authenticated. Redirecting to " + continuePath);
				resp.sendRedirect(continuePath);
			} else {
				logger.debug(() -> "No user has been authenticated. Showing the login page.");
				super.service(req, resp);
			}
		} else {
			logger.debug(() -> "A user has been authenticated. Hence, a message has been sent to the client already at this point.");
		}
	}

	private void logPrincipal(UserProfile cp, Map<String, Object> attributeMap, final String username, ShiroClient shiroClient) {
		if (logger.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder("Authenticated principal received: \n");
			sb.append("Id: " + cp.getId() + "\n");
			sb.append("Linked Id: " + cp.getLinkedId() + "\n");
			sb.append("Username: " + username + "\n");
			sb.append("Client: " + (shiroClient != null ? shiroClient.getName() : "No Client detected.") + "\n");
			sb.append(obfuscateAttributeMap(attributeMap, "\n"));
			logger.debug("\n" + StringTools.asciiBoxMessage(sb.toString(), -1));
		}
	}

	protected boolean acceptUsername(String username) {
		if ((userAcceptList == null || userAcceptList.isEmpty()) && (userBlockList == null || userBlockList.isEmpty())) {
			return true;
		}
		if (username == null) {
			return false;
		}
		// If there is no accept list, we just check the block list
		// If there IS an accept list, the user has to be accepted by this list
		boolean accepted = userAcceptList == null || userAcceptList.isEmpty();
		if (userAcceptList != null) {
			for (String wl : userAcceptList) {
				if (wl != null) {
					if (username.matches(wl) || username.equals(wl)) {
						accepted = true;
						break;
					}
				}
			}
		}
		if (!accepted) {
			logger.debug(() -> "The user " + username + " is not accepted by the accept-list.");
			return false;
		}
		if (userBlockList != null) {
			for (String bl : userBlockList) {
				if (bl != null) {
					if (username.matches(bl) || username.equals(bl)) {
						accepted = false;
						break;
					}
				}
			}
		}
		if (!accepted) {
			logger.debug(() -> "The user " + username + " is not accepted by the blocklist.");
		}
		return accepted;
	}

	private ShiroClient getShiroClient(UserProfile cp) {
		String clientName = cp.getClientName();
		if (!StringTools.isBlank(clientName)) {
			for (ShiroClient client : configuration.getClients()) {
				if (client.getName() != null && client.getName().equals(clientName)) {
					logger.debug(() -> "For the client name " + clientName + " we identified the client: " + client);
					return client;
				}
			}
		}
		logger.debug(() -> "Could not find a client for name " + clientName);
		return null;
	}

	private void returnSessionId(HttpServletResponse resp, String sessionId) {
		resp.setContentType("text/plain");
		try {
			resp.getWriter().write(sessionId);
		} catch (IOException e) {
			logger.error("Could not send session id", e);
		}
	}

	private void deleteShiroSessionCookies(HttpServletRequest req, HttpServletResponse resp) {
		Pair<String, String> domainAndPath = null;
		Cookie[] cookies = req.getCookies();
		if (cookies != null)
			for (Cookie cookie : cookies) {
				Cookie rmCookie = null;

				String cookieName = cookie.getName();
				if (cookieName.equalsIgnoreCase("JSESSIONID")) {
					rmCookie = new Cookie(cookieName, "");
					if (domainAndPath == null) {
						domainAndPath = getDomainAndPathFromPublicServicesUrl();
					}
					rmCookie.setPath(domainAndPath.second);
				} else if (cookieName.equalsIgnoreCase("pac4jCsrfToken")) {
					rmCookie = new Cookie(cookieName, "");
					rmCookie.setPath("/");
				}

				if (rmCookie != null) {
					if (domainAndPath == null) {
						domainAndPath = getDomainAndPathFromPublicServicesUrl();
					}
					rmCookie.setDomain(domainAndPath.first);
					rmCookie.setMaxAge(0);
					resp.addCookie(rmCookie);
				}

			}
	}

	private Pair<String, String> getDomainAndPathFromPublicServicesUrl() {
		if (!StringTools.isBlank(publicServicesUrl)) {
			try {
				URL url = new URI(publicServicesUrl).toURL();
				String host = url.getHost();
				String path = url.getPath();
				return new Pair<>(host, path);
			} catch (Exception e) {
				logger.trace(() -> "Could not get domain name from public services URL " + publicServicesUrl, e);
			}
		}
		return new Pair<>("", "/tribefire-services");
	}

	private boolean ensureUser(String username, ShiroClient shiroClient, Map<String, Object> attributeMap) {
		User user = userService.findUser(User.name, username);

		if (user == null) {
			if (!createUsers) {
				logger.debug(() -> "User " + username + " does not exist. The configuration is set to NOT create a new user on the fly.");
				return false;
			}

			logger.debug(() -> "User " + username + " does not yet exist but we will create it now.");

			user = User.T.create();
			user.setName(username);
			enrichUser(shiroClient, user, attributeMap);

			handleUserRoles(shiroClient, user, attributeMap, true);

		} else {
			user = cloneUser(user);

			checkExistingUserUpdates(shiroClient, user, attributeMap);

			handleUserRoles(shiroClient, user, attributeMap, false);

		}

		userService.ensureUser(user);

		return true;
	}

	private User cloneUser(User user) {
		User result = User.T.create();
		copySimplePropsAndLocalizedStrings(user, result);
		return result;
	}

	private <E extends GenericEntity> void copySimplePropsAndLocalizedStrings(E source, E target) {
		for (Property p : source.entityType().getProperties()) {
			if (p.getType().isSimple()) {
				if (!p.isPartition() && !p.isGlobalId())
					p.set(target, p.get(source));
				continue;
			}

			if (LocalizedString.T.isAssignableFrom(p.getType())) {
				LocalizedString ls = p.get(source);
				if (ls != null) {
					LocalizedString newLs = LocalizedString.T.create();
					newLs.setLocalizedValues(ls.getLocalizedValues());
				}
			}
		}
	}

	private void handleUserRoles(ShiroClient shiroClient, User user, Map<String, Object> attributeMap, boolean isNewUser) {

		logger.debug(() -> "Starting to handle roles for user " + user.getName());

		HasRolesField rolesProvider = (shiroClient instanceof HasRolesField) ? (HasRolesField) shiroClient : null;
		if (rolesProvider != null) {
			logger.debug(() -> "The client " + shiroClient.getName() + " is a roles provider.");

			Set<String> roles = new HashSet<>();

			List<String> externalRoles = getRolesFromExternal(rolesProvider, attributeMap);
			if (externalRoles != null && !externalRoles.isEmpty()) {
				logger.debug(() -> "The authentication of the user resulted in these roles from the external system: " + externalRoles);
				roles.addAll(externalRoles);
			} else {
				logger.debug(() -> "The authentication of the user resulted in NO roles.");
			}

			boolean exclusive = rolesProvider.getExclusiveRoleProvider() != null ? rolesProvider.getExclusiveRoleProvider() : false;
			logger.debug(() -> "The role provider (client) is set to be the only source of roles: " + exclusive + "; is it a new user: " + isNewUser);

			if (!exclusive && isNewUser) {
				Set<String> internallyProvidedRoles = newUserRoleProvider != null ? newUserRoleProvider.apply(user) : null;
				if (internallyProvidedRoles != null && !internallyProvidedRoles.isEmpty()) {
					logger.debug(() -> "The internal role provider for new users returned: " + internallyProvidedRoles);
					roles.addAll(internallyProvidedRoles);
				}
			}

			if (exclusive) {
				logger.debug(() -> "Resetting the roles of the user " + user.getName() + " because the provider (" + shiroClient.getName()
						+ ") is set to be the exclusive source.");
				user.getRoles().clear();
			} else {
				logger.debug(() -> "Keeping the existing roles of the user " + user.getName() + " because the provider (" + shiroClient.getName()
						+ ") is not set to be the exclusive source.");
			}
			if (externalRoles != null && !externalRoles.isEmpty()) {
				logger.debug(() -> "Got roles " + roles + " from the providers.");
				ensureRoles(user, roles);
			}

		} else {
			logger.debug(() -> "The client " + shiroClient.getName() + " is not a roles provider. Is it a new user: " + isNewUser);

			if (isNewUser) {
				Set<String> roles = newUserRoleProvider != null ? newUserRoleProvider.apply(user) : null;
				if (roles != null && !roles.isEmpty()) {
					logger.debug(() -> "Got roles " + roles + " from the provider.");
					ensureRoles(user, roles);
				} else {
					logger.debug(() -> "Got no roles from provider: " + newUserRoleProvider);
				}
			}
		}
	}

	private void ensureRoles(User user, Collection<String> roles) {
		if (isEmpty(roles))
			return;

		for (String newRole : roles) {
			Role role = Role.T.create();
			role.setName(newRole);
			user.getRoles().add(role);
		}
	}

	private List<String> getRolesFromExternal(HasRolesField rolesProvider, Map<String, Object> attributeMap) {

		return getRolesFromExternal(rolesProvider.getRolesField(), rolesProvider.getRolesFieldEncoding(), attributeMap);

	}

	protected List<String> getRolesFromExternal(String pattern, FieldEncoding fieldEncoding, Map<String, Object> attributeMap) {

		List<String> allRolesCombined = new ArrayList<>();

		if (!StringTools.isBlank(pattern)) {
			logger.debug(() -> "Trying to find " + pattern + " in attribute map.");

			String[] components = pattern.split(",");

			for (String part : components) {

				part = part.trim();

				logger.debug("Trying to find part " + part + " in attribute map.");

				List<String> rolesCollection = null;

				Object rolesObject = attributeMap.get(part);
				if (rolesObject instanceof List) {
					// We got it directly from a claim (within a token)
					rolesCollection = (List<String>) rolesObject;
					logger.debug("Found list " + part + " in attribute map: " + rolesCollection);
					allRolesCombined.addAll(rolesCollection);
				} else {

					if (rolesObject instanceof String) {
						String rolesString = (String) rolesObject;
						rolesCollection = parseCollection(rolesString, fieldEncoding);
						logger.debug("Found plain " + part + " in attribute map: " + rolesCollection);

					} else {
						rolesCollection = readRolesWithPattern(part, fieldEncoding, attributeMap);
						logger.debug("Found pattern " + part + " in attribute map: " + rolesCollection);
					}
				}
				if (rolesCollection != null) {
					allRolesCombined.addAll(rolesCollection);
				}

			}

		}
		return allRolesCombined;
	}

	protected List<String> readRolesWithPattern(final String pattern, FieldEncoding fieldEncoding, final Map<String, Object> attributeMap) {
		int start = pattern.indexOf("{");
		int stop = pattern.indexOf("}", start + 1);
		if (start == -1 || stop == -1) {
			logger.debug(() -> pattern + " does not seem to be a pattern");
			return Collections.EMPTY_LIST;
		}
		String prefix = pattern.substring(0, start);
		String postFix = pattern.substring(stop + 1);
		String rawPattern = pattern.substring(start, stop + 1);

		String rolesString = StringTools.patternFormat(rawPattern, attributeMap, "");
		List<String> rolesCollection = null;
		if (!StringTools.isBlank(rolesString)) {
			rolesCollection = parseCollection(rolesString, fieldEncoding);
		}
		if (prefix.length() > 0 && rolesCollection != null) {
			rolesCollection = rolesCollection.stream().map(r -> prefix + r).collect(Collectors.toList());
		}
		if (postFix.length() > 0 && rolesCollection != null) {
			rolesCollection = rolesCollection.stream().map(r -> r + postFix).collect(Collectors.toList());
		}
		if (rolesCollection == null) {
			return Collections.EMPTY_LIST;
		}
		return rolesCollection;
	}

	private void enrichUser(ShiroClient shiroClient, User user, Map<String, Object> attributeMap) {

		String pattern = shiroClient.getUserMailField();
		if (!StringTools.isBlank(pattern)) {
			user.setEmail(StringTools.patternFormat(pattern, attributeMap));
		}
		pattern = shiroClient.getUserDescriptionPattern();
		if (!StringTools.isBlank(pattern)) {
			user.setDescription(I18nTools.createLs(StringTools.patternFormat(pattern, attributeMap)));
		}
		pattern = shiroClient.getFirstNamePattern();
		if (!StringTools.isBlank(pattern)) {
			user.setFirstName(StringTools.patternFormat(pattern, attributeMap));
		}
		pattern = shiroClient.getLastNamePattern();
		if (!StringTools.isBlank(pattern)) {
			user.setLastName(StringTools.patternFormat(pattern, attributeMap));
		}

		String userIconUrl = getUserIconUrl(shiroClient, attributeMap);

		if (!StringTools.isBlank(userIconUrl)) {
			// TODO support user icon
			// String url = StringTools.patternFormat(userIconUrl, attributeMap);
			// if (!StringTools.isBlank(url)) {
			//
			// String extension = FileTools.getExtension(url);
			// if (StringTools.isBlank(extension)) {
			// // We have to assume something
			// extension = "jpg";
			// } else if (extension.length() < 3) {
			// extension = "jpg";
			// }
			//
			// CloseableHttpClient httpClient = null;
			// CloseableHttpResponse response = null;
			// try {
			// httpClient = httpClientProvider.provideHttpClient();
			// HttpGet get = new HttpGet(url);
			// response = httpClient.execute(get);
			// if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			// logger.debug("Got a non-200 response from " + url + ": " + response);
			// } else {
			// Resource uploadedResource = null;
			// try (InputStream is = new ResponseEntityInputStream(response)) {
			// String normalizedUserName = FileTools.normalizeFilename(user.getName(), '_');
			// uploadedResource = session.resources().create().name("image-" + normalizedUserName + "." + extension).store(is);
			// }
			//
			// SimpleIcon picture = session.create(SimpleIcon.T);
			// picture.setImage(uploadedResource);
			// user.setPicture(picture);
			//
			// }
			// } catch (Exception e) {
			// logger.error("Could not download user icon at: " + url, e);
			// } finally {
			// IOTools.closeCloseable(response, logger);
			// IOTools.closeCloseable(httpClient, logger);
			// }
			// }
		}
	}

	protected String getUserIconUrl(ShiroClient shiroClient, Map<String, Object> map) {
		String iconUrl = shiroClient.getUserIconUrl();
		if (!StringTools.isBlank(iconUrl)) {
			return iconUrl;
		}

		if (shiroClient instanceof ShiroInstagramOAuth20Client) {
			iconUrl = externalIconUrlHelper.getIconUrlFromInstagram((ShiroInstagramOAuth20Client) shiroClient, map);
		}
		return iconUrl;
	}

	private List<String> parseCollection(String spec, FieldEncoding fieldEncoding) {
		if (StringTools.isBlank(spec)) {
			return null;
		}
		if (fieldEncoding == null) {
			fieldEncoding = FieldEncoding.PLAIN;
		}
		switch (fieldEncoding) {
			case CSV:
				if (spec.startsWith("[") && spec.endsWith("]")) {
					spec = StringTools.removeFirstAndLastCharacter(spec);
				}
				String[] rolesStrings = StringTools.splitCommaSeparatedString(spec, true);
				return CollectionTools2.asLinkedList(rolesStrings);
			case JSON:
				JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
				JSONArray array;
				try {
					List<String> result = new ArrayList<>();
					array = (JSONArray) parser.parse(spec);
					for (int i = 0; i < array.size(); ++i) {
						String entry = (String) array.get(i);
						result.add(entry);
					}
					return result;
				} catch (ParseException e) {
					logger.warn("Error while trying to parse " + spec + " as a JSON structure.", e);
				}
				break;
			case PLAIN:
				return CollectionTools2.asLinkedList(spec);
			default:
				break;

		}
		return null;
	}

	private static void setStringIfUpdated(Consumer<String> toSet, Supplier<String> oldValueSupplier, String newValue) {
		if (newValue != null) {
			String oldValue = oldValueSupplier.get();
			if (oldValue == null || !newValue.equals(oldValue)) {
				toSet.accept(newValue);
			}
		}
	}

	private void checkExistingUserUpdates(ShiroClient shiroClient, User user, Map<String, Object> attributeMap) {

		String pattern = shiroClient.getUserMailField();
		if (!StringTools.isBlank(pattern)) {
			String newEmail = StringTools.patternFormat(pattern, attributeMap);
			setStringIfUpdated(user::setEmail, user::getEmail, newEmail);
		}

		pattern = shiroClient.getUserDescriptionPattern();
		if (!StringTools.isBlank(pattern)) {
			LocalizedString newLocalizedString = I18nTools.createLs(StringTools.patternFormat(pattern, attributeMap));
			if (newLocalizedString != null && user.getDescription() == null) {
				user.setDescription(newLocalizedString);
			}
		}

		pattern = shiroClient.getFirstNamePattern();
		if (!StringTools.isBlank(pattern)) {
			String newFirstName = StringTools.patternFormat(pattern, attributeMap);
			setStringIfUpdated(user::setFirstName, user::getFirstName, newFirstName);
		}
		pattern = shiroClient.getLastNamePattern();
		if (!StringTools.isBlank(pattern)) {
			String newLastName = StringTools.patternFormat(pattern, attributeMap);
			setStringIfUpdated(user::setLastName, user::getLastName, newLastName);
		}

		// TODO support user icons
		// Icon oldPicture = user.getPicture();
		// String oldMd5 = null;
		// if (oldPicture instanceof SimpleIcon) {
		// SimpleIcon si = (SimpleIcon) oldPicture;
		// Resource resource = si.getImage();
		// if (resource != null) {
		// oldMd5 = resource.getMd5();
		// }
		// }

		pattern = shiroClient.getUserIconUrl();
		if (!StringTools.isBlank(pattern)) {

			String url = StringTools.patternFormat(pattern, attributeMap);
			if (!StringTools.isBlank(url)) {
				// TODO support user icons
				// String extension = FileTools.getExtension(url);
				// if (StringTools.isBlank(extension)) {
				// // We have to assume something
				// extension = "jpg";
				// }
				//
				// CloseableHttpClient httpClient = null;
				// CloseableHttpResponse response = null;
				// MemoryThresholdBuffer buffer = new MemoryThresholdBuffer(Numbers.KILOBYTE * 20);
				// try {
				// String normalizedUserName = FileTools.normalizeFilename(user.getName(), '_');
				//
				// httpClient = httpClientProvider.provideHttpClient();
				// HttpGet get = new HttpGet(url);
				// response = httpClient.execute(get);
				// if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				// logger.debug("Got a non-200 response from " + url + ": " + response);
				// } else {
				// Resource uploadedResource = null;
				// MessageDigest md = MessageDigest.getInstance("MD5");
				//
				// try (InputStream is = new DigestInputStream(new ResponseEntityInputStream(response), md)) {
				// IOTools.pump(is, buffer);
				// }
				// String newMd5 = digest(md);
				//
				// if (oldMd5 == null || !oldMd5.equals(newMd5)) {
				//
				// try (InputStream in = buffer.openInputStream(true)) {
				//
				// uploadedResource = session.resources().create().name("image-" + normalizedUserName + "." + extension).store(in);
				//
				// SimpleIcon picture = session.create(SimpleIcon.T);
				// picture.setImage(uploadedResource);
				// user.setPicture(picture);
				// }
				//
				// }
				// }
				// } catch (Exception e) {
				// logger.error("Could not download user icon at: " + url, e);
				// } finally {
				// buffer.delete();
				// IOTools.closeCloseable(response, logger);
				// IOTools.closeCloseable(httpClient, logger);
				// }
			}
		}
	}

	@SuppressWarnings("unused")
	private static String digest(MessageDigest md) {
		return convertToHex(md.digest());
	}

	private static String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	private String getUsername(ShiroClient shiroClient, Map<String, Object> attributeMap) {

		List<String> usernamePatterns = shiroClient.getUsernamePatterns();
		String username = null;

		logger.debug(() -> "Username patterns: " + usernamePatterns);

		if (usernamePatterns == null || usernamePatterns.isEmpty()) {
			// This is the fallback mechanism that is not going to work for all clients
			Object value = attributeMap.get("email");
			if (value instanceof String) {
				username = (String) value;
				if (logger.isDebugEnabled())
					logger.debug("Fallback value for email: " + username);
			}
			if (StringTools.isBlank(username)) {
				value = attributeMap.get("name");
				if (value instanceof String) {
					username = (String) value;
					if (logger.isDebugEnabled())
						logger.debug("Fallback value for name: " + username);
				}
			}
		} else {

			for (String usernamePattern : usernamePatterns) {
				try {
					username = StringTools.patternFormat(usernamePattern, attributeMap);
					if (logger.isDebugEnabled())
						logger.debug("Username derived from pattern: " + username);
					break;
				} catch (IllegalArgumentException iae) {
					logger.debug("Could not derive a username from pattern '" + usernamePattern + "' with properties: "
							+ obfuscateAttributeMap(attributeMap, ","));
				}
			}
		}
		if (StringTools.isBlank(username)) {
			username = null;
		}
		if (logger.isDebugEnabled())
			logger.debug("Username: " + username);

		return username;
	}

	private String handleUrl(String sessionId, String continuePath) throws CodecException {
		String redirectUrl;
		int separatorIdx = continuePath.indexOf("?");
		String continueQuery = null;
		if (separatorIdx > 0) {
			continueQuery = continuePath.substring(separatorIdx + 1);
			continuePath = continuePath.substring(0, separatorIdx);
		}

		Map<String, String> continueQueryMap = getUrlParamCodec().decode(continueQuery);
		continueQueryMap.put(WebSecurityConstants.REQUEST_PARAM_SESSIONID, sessionId);
		continueQuery = getUrlParamCodec().encode(continueQueryMap);

		redirectUrl = continuePath + ((continueQuery.isEmpty()) ? "" : "?" + continueQuery);
		return redirectUrl;
	}

	public Codec<Map<String, String>, String> getUrlParamCodec() {
		if (urlParamCodec == null) {
			MapCodec<String, String> mapCodec = new MapCodec<String, String>();
			mapCodec.setEscapeCodec(new UrlEscapeCodec());
			mapCodec.setDelimiter("&");
			this.urlParamCodec = mapCodec;
		}
		return urlParamCodec;
	}

	protected void initializeContext(HttpServletRequest httpRequest) {
		AttributeContext currentContext = AttributeContexts.peek();
		AttributeContext derivedContext = currentContext.derive().set(RequestedEndpointAspect.class, httpRequest.getRequestURL().toString())
				.set(RequestorAddressAspect.class, getClientRemoteInternetAddress(httpRequest)).build();

		AttributeContexts.push(derivedContext);
	}

	protected void popContext() {
		try {
			AttributeContexts.pop();
		} catch (Exception e) {
			logger.error("Failed to pop the service context" + (e.getMessage() != null ? ": " + e.getMessage() : ""), e);
		}
	}

	private void enrichOpenUserSessionRequest(OpenUserSession authRequest, ShiroClient shiroClient, Map<String, Object> attributeMap) {

		Set<String> propertyNames = shiroClient.getSessionPropertyNames();
		if (propertyNames.isEmpty()) {
			return;
		}
		logger.debug(() -> "Configured session property names: " + propertyNames);

		if (propertyNames.contains("*")) {
			for (Map.Entry<String, Object> entry : attributeMap.entrySet()) {
				String propName = entry.getKey();
				String attributeAsString = "" + entry.getValue();
				logger.debug(
						() -> "Setting open session request property: " + propName + "=" + obfuscateAttrValueIfNeeded(attributeAsString, propName));
				authRequest.getProperties().put(propName, attributeAsString);
			}
		} else {
			for (String propName : propertyNames) {
				Object attribute = attributeMap.get(propName);
				if (attribute != null) {
					String attributeAsString = attribute.toString();
					logger.debug(() -> "Setting open session request property: " + propName + "=" + obfuscateAttrValueIfNeeded(attribute, propName));
					authRequest.getProperties().put(propName, attributeAsString);
				} else {
					logger.debug(() -> "Could not find profile property: " + propName);
				}
			}
		}
	}

	protected OpenUserSession createOpenUserSessionRequest(String user, HttpServletRequest request) {
		UserSession userSession = systemAttributeContextSupplier.get().findOrDefault(UserSessionAspect.class, null);
		if (userSession == null)
			throw new IllegalStateException("System UserSession not found!");

		String existingSessionId = userSession.getSessionId();

		ExistingSessionCredentials existing = ExistingSessionCredentials.T.create();
		existing.setReuseSession(false);
		existing.setExistingSessionId(existingSessionId);

		GrantedCredentials credentials = GrantedCredentials.T.create();
		credentials.setGrantingCredentials(existing);

		UserNameIdentification identification = UserNameIdentification.T.create();
		identification.setUserName(user);
		credentials.setUserIdentification(identification);

		OpenUserSession authReq = OpenUserSession.T.create();
		authReq.setCredentials(credentials);

		RemoteClientAddressResolver resolver = getRemoteAddressResolver();
		try {
			RemoteAddressInformation remoteAddressInformation = resolver.getRemoteAddressInformation(request);
			String remoteAddress = remoteAddressInformation.getRemoteIp();
			logger.info("Received an authentication request for user '" + user + "' from [" + remoteAddress + "]. Remote Address Information: "
					+ remoteAddressInformation.toString());
		} catch (Exception e) {
			String message = "Could not use the client address resolver to get the client's IP address. User: '" + user + "'";
			logger.info(message);
			if (logger.isDebugEnabled())
				logger.debug(message, e);
		}

		return authReq;
	}

	private UserSession authenticate(HttpServletResponse resp, OpenUserSession authRequest) throws AuthenticationException {
		AttributeContexts.push(AttributeContexts.derivePeek().set(Waypoint.class, "platform-login").build());

		try {
			EvalContext<? extends OpenUserSessionResponse> responseContext = authRequest.eval(evaluator);

			Maybe<? extends OpenUserSessionResponse> responseMaybe = responseContext.getReasoned();
			if (responseMaybe.isSatisfied())
				return responseMaybe.get().getUserSession();

			logger.debug(() -> "Could not authenticate the user: " + responseMaybe.whyUnsatisfied().asString());

			buildResponseMessage(resp, "Invalid authentication!");

			return null;

		} catch (Exception e) {
			throw new AuthenticationException("Error while trying to evaluate the authentication request: " + authRequest, e);
		} finally {
			AttributeContexts.pop();
		}
	}

	private void buildResponseMessage(HttpServletResponse resp, String message) throws IOException, UnsupportedEncodingException {
		String redirectUrl = publicServicesUrl + "/" + pathIdentifier + "?message=" + URLEncoder.encode(message, "UTF-8");
		resp.sendRedirect(redirectUrl);
	}

	/**
	 * Uses {@link Base64}
	 */
	@Override
	protected VelocityContext createContext(HttpServletRequest request, HttpServletResponse repsonse) {

		String continueParameter = request.getParameter(WebSecurityConstants.REQUEST_PARAM_CONTINUE);
		String urlEncodedContinueParameter = null;
		if (!StringTools.isBlank(continueParameter)) {
			try {
				urlEncodedContinueParameter = URLEncoder.encode(continueParameter, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.info("The continue parameter " + continueParameter + " could not be URL-encoded.", e);
			}
		}

		Map<String, String> authUrls = new LinkedHashMap<>();
		Map<String, String> authImageUrls = new LinkedHashMap<>();
		Map<String, String> authEmbeddedImages = new LinkedHashMap<>();
		for (ShiroClient client : configuration.getClients()) {
			String clientName = client.getName();
			String authUrl = publicServicesUrl + "/" + pathIdentifier + "/auth/" + clientName.toLowerCase();
			if (urlEncodedContinueParameter != null) {
				authUrl = authUrl.concat("?").concat(WebSecurityConstants.REQUEST_PARAM_CONTINUE).concat("=").concat(urlEncodedContinueParameter);
			}
			authUrls.put(clientName, authUrl);

			// tribefire-services/static/tribefire.extension.shiro.shiro-cartridge/login-images/google.png
			String imageUrl = publicServicesUrl + staticImagesRelativePath + clientName.toLowerCase() + ".png";
			authImageUrls.put(clientName, imageUrl);

			Resource icon = client.getLoginIcon();
			if (icon != null) {
				// TODO support LoginIcons if needed

				// String iconContent = iconContentCache.computeIfAbsent(icon.getId(), id -> {
				// try (InputStream in = cortexSession.resources().openStream(icon)) {
				// byte[] imageBytes = IOTools.slurpBytes(in);
				// String base64Image = Base64.getEncoder().encodeToString(imageBytes);
				// String fullData = "data:".concat(icon.getMimeType()).concat(";base64,").concat(base64Image);
				// return fullData;
				//
				// } catch (Exception e) {
				// logger.warn(() -> "Could not load icon " + icon, e);
				// }
				// return null;
				// });
				// if (iconContent != null) {
				// authEmbeddedImages.put(clientName, iconContent);
				// }
			}
		}

		VelocityContext context = new VelocityContext();
		context.put("applicationName", applicationName);
		context.put("continue", request.getParameter(WebSecurityConstants.REQUEST_PARAM_CONTINUE));
		context.put("message", request.getParameter(WebSecurityConstants.REQUEST_PARAM_MESSAGE));
		context.put("messageStatus", request.getParameter(WebSecurityConstants.REQUEST_PARAM_MESSAGESTATUS));
		context.put("authUrls", authUrls);
		context.put("authImageUrls", authImageUrls);
		context.put("authEmbeddedImages", authEmbeddedImages);
		context.put("showStandardLoginForm", showStandardLoginForm);
		context.put("showTextLinks", showTextLinks);

		if (offerStaySigned) {
			context.put("offerStaySigned", Boolean.TRUE);
		} else {
			context.put("offerStaySigned", Boolean.FALSE);
		}

		context.put("publicServicesUrl", publicServicesUrl);

		return context;
	}

	/**
	 * <p>
	 * Retrieves the client's remote Internet protocol address.
	 * 
	 * @param request
	 *            The request from the client.
	 * @return The remote address of the client.
	 */
	private String getClientRemoteInternetAddress(HttpServletRequest request) {
		return getRemoteAddressResolver().getRemoteIpLenient(request);
	}

	private String acquireContinuePath(HttpServletRequest req) {
		String continuePath = req.getParameter(WebSecurityConstants.REQUEST_PARAM_CONTINUE);
		if (continuePath == null) {
			String defaultRedirectUrl = configuration.getDefaultRedirectUrl();
			if (!StringTools.isBlank(defaultRedirectUrl)) {
				continuePath = defaultRedirectUrl;
			} else {
				continuePath = publicServicesUrl; // Default page after successful sign-in.
			}
		}
		return continuePath;
	}

	private Map<String, Object> getAttributeMap(UserProfile cp) {
		Map<String, Object> map = new LinkedHashMap<>(cp.getAttributes());
		map.putIfAbsent("id", cp.getId());
		map.putIfAbsent("linkedId", cp.getLinkedId());

		Map<String, Object> attributes = cp.getAttributes();

		if (cp instanceof OidcProfile op) {
			// This was added because we moved from buji-pack4j#7.0.0 in Cortex to buji-pack4j#9.0.0 (because we moved to jakarta servlet-api)
			// In the older version, the UserProgile.getAttributes() contained this AccessToken, but it is no only a String that cannot be parsed
			// So we use this alternative way to get hold of AccessToken
			AccessToken accessToken = op.getAccessToken();
			if (accessToken != null)
				attributes.putIfAbsent("oidc_profile__access_token", accessToken.getValue());
		}

		for (Map.Entry<String, Object> entry : attributes.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (key.toLowerCase().endsWith("token")) {

				String token = null;

				if (value instanceof String) {
					token = (String) value;
				} else if (value instanceof AccessToken) {
					AccessToken bac = (AccessToken) value;
					token = bac.getValue();
				}

				if (!StringTools.isBlank(token)) {
					logger.debug("Looking at potential JWT token: " + key + "=" + obfuscateAttrValueIfNeeded(token, key));

					int i = token.lastIndexOf('.');
					if (i > 0 && StringTools.countOccurrences(token, ".") == 2) {

						TokenContent tokenWithoutValidation = shiroTools.parseTokenWithoutValidation(token, true);
						if (tokenWithoutValidation != null) {
							Claims body = tokenWithoutValidation.body();

							for (Map.Entry<String, Object> claim : body.entrySet()) {
								String claimKey = claim.getKey();
								if (!map.containsKey(claimKey)) {
									Object claimValue = claim.getValue();
									map.put(claimKey, claimValue);
									logger.debug(() -> "Added claim: " + claimKey + " = " + claimValue);
								}
							}
						}
					}
				}
			}
		}

		return map;
	}

	private String obfuscateAttributeMap(Map<String, Object> attributeMap, String entryDelimiter) {
		StringJoiner sj = new StringJoiner(entryDelimiter);

		for (Map.Entry<String, Object> entry : attributeMap.entrySet()) {
			String key = entry.getKey();
			Object value = obfuscateAttrValueIfNeeded(entry.getValue(), key);
			sj.add(key + "=" + value);
		}

		return sj.toString();
	}

	private String obfuscateAttrValueIfNeeded(Object value, String key) {
		if (value != null && obfuscateLogOutput && isConfidentialAttr(key))
			return "*****";
		else
			return "" + value;
	}

	private boolean isConfidentialAttr(String key) {
		return key.equalsIgnoreCase("id_token") || key.equalsIgnoreCase("access_token");
	}

	private RemoteClientAddressResolver getRemoteAddressResolver() {
		if (remoteAddressResolver == null) {
			remoteAddressResolver = DefaultRemoteClientAddressResolver.getDefaultResolver();
		}
		return remoteAddressResolver;
	}

	// @formatter:off
	@Required     public void setApplicationName(String applicationName) { this.applicationName = applicationName; }
	@Required     public void setEvaluator(Evaluator<ServiceRequest> evaluator) { this.evaluator = evaluator; }
	@Required     public void setUserService(UserService userService) { this.userService = userService; }
	@Required     public void setCookieHandler(CookieHandler cookieHandler) { this.cookieHandler = cookieHandler; }
	@Required     public void setExternalIconUrlHelper(ExternalIconUrlHelper externalIconUrlHelper) { this.externalIconUrlHelper = externalIconUrlHelper; }
	@Required     public void setPublicServicesUrl(String publicServicesUrl) { this.publicServicesUrl = publicServicesUrl; }
	@Required     public void setPathIdentifier(String pathIdentifier) { this.pathIdentifier = pathIdentifier; }
	@Required     public void setShiroTools(ShiroTools shiroTools) { this.shiroTools = shiroTools; }
	@Required     public void setStaticImagesRelativePath(String staticImagesRelativePath) { this.staticImagesRelativePath = staticImagesRelativePath; }
	@Required     public void setSystemAttributeContextSupplier(Supplier<AttributeContext> satc) { this.systemAttributeContextSupplier = satc; }
	@Configurable public void setAddSessionParameter(boolean addSessionParameter) { this.addSessionUrlParameter = addSessionParameter; }
	@Configurable public void setConfiguration(ShiroAuthenticationConfiguration configuration) { this.configuration = configuration; }
	@Configurable public void setCreateUsers(boolean createUsers) { this.createUsers = createUsers; }
	@Configurable public void setHttpClientProvider(HttpClientProvider httpClientProvider) { this.httpClientProvider = httpClientProvider; }
	@Configurable public void setNewUserRoleProvider(NewUserRoleProvider newUserRoleProvider) { this.newUserRoleProvider = newUserRoleProvider; }
	@Configurable public void setRemoteAddressResolver(RemoteClientAddressResolver remoteAddressResolver) { this.remoteAddressResolver = remoteAddressResolver; }
	@Configurable public void setShowStandardLoginForm(boolean showStandardLoginForm) { this.showStandardLoginForm = showStandardLoginForm; }
	@Configurable public void setShowTextLinks(boolean showTextLinks) { this.showTextLinks = showTextLinks; }
	@Configurable public void setUrlParamCodec(Codec<Map<String, String>, String> urlParamCodec) { this.urlParamCodec = urlParamCodec; }
	@Configurable public void setUserAcceptList(Set<String> userAcceptList) { this.userAcceptList = userAcceptList; }
	@Configurable public void setUserBlockList(Set<String> userBlockList) { this.userBlockList = userBlockList; }
	// @formatter:on

	@Configurable
	public void setObfuscateLogOutput(Boolean obfuscateLogOutput) {
		if (obfuscateLogOutput != null) {
			this.obfuscateLogOutput = obfuscateLogOutput;
		}
	}

}
