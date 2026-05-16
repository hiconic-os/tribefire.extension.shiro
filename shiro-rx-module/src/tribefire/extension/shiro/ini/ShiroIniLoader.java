package tribefire.extension.shiro.ini;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/**
 * @author peter.gazdik
 */
public class ShiroIniLoader {

	public static String loadIniTempalte() {
		try (InputStream is = ShiroIniLoader.class.getResourceAsStream("shiro.ini.vm")) {
			return new String(is.readAllBytes(), StandardCharsets.UTF_8.name());
		} catch (IOException e) {
			throw new UncheckedIOException("Error while loading 'shiro.ini.vm' from classpath.", e);
		}
	}

}
