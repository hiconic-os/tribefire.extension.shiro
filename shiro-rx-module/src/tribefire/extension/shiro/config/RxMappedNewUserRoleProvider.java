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
package tribefire.extension.shiro.config;

import java.util.List;
import java.util.Set;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.shiro.deployment.UserToRolesMapEntry;

public interface RxMappedNewUserRoleProvider extends RxNewUserRoleProvider {

	final EntityType<RxMappedNewUserRoleProvider> T = EntityTypes.T(RxMappedNewUserRoleProvider.class);
	
	Set<UserToRolesMapEntry> getMapping();
	void setMapping(Set<UserToRolesMapEntry> mapping);
	
	List<String> getFields();
	void setFields(List<String> field);

}
