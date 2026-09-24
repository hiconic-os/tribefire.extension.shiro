package tribefire.extension.shiro;

/**
 * Constants of the Reflex flavour of the Shiro extension.
 * <p>
 * The classic module keeps its own in {@code ShiroConstants}, because it serves the same images through a different mechanism and at a different
 * path.
 */
public interface ShiroRxConstants {

	/** The top level classpath folder holding the login provider images, and the URL path they are served under. */
	String LOGIN_IMAGES = "shiro-login-images";

	/** The URL path the login pages build their image URLs from, relative to the public services URL. */
	String LOGIN_IMAGES_RELATIVE_PATH = "/" + LOGIN_IMAGES + "/";

}
