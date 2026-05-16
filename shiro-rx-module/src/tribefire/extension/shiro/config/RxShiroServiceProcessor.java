// ============================================================================
package tribefire.extension.shiro.config;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.shiro.deployment.ShiroAuthenticationConfiguration;

public interface RxShiroServiceProcessor extends GenericEntity {

	EntityType<RxShiroServiceProcessor> T = EntityTypes.T(RxShiroServiceProcessor.class);

	ShiroAuthenticationConfiguration getConfiguration();
	void setConfiguration(ShiroAuthenticationConfiguration configuration);

	String getPathIdentifier();
	void setPathIdentifier(String pathIdentifier);

	@Initializer("true")
	Boolean getObfuscateLogOutput();
	void setObfuscateLogOutput(Boolean obfuscateLogOutput);

}
