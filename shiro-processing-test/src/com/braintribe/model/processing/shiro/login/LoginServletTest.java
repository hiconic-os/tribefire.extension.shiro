// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
//
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
package com.braintribe.model.processing.shiro.login;

import static com.braintribe.utils.lcd.CollectionTools2.asSet;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.braintribe.model.shiro.deployment.FieldEncoding;

public class LoginServletTest {

	@Test
	public void testAcceptAndBlockListsPlain() throws Exception {

		LoginServlet ls = new LoginServlet();

		// Nothing is set, all users get accepted

		assertThat(ls.acceptUsername("hello")).isTrue();
		assertThat(ls.acceptUsername("world")).isTrue();

		// Just set an accept list... only those users should be accepted

		ls.setUserAcceptList(asSet("hello"));

		assertThat(ls.acceptUsername("hello")).isTrue();
		assertThat(ls.acceptUsername("world")).isFalse();
		assertThat(ls.acceptUsername("hello,world")).isFalse();
		assertThat(ls.acceptUsername(null)).isFalse();

		// Just set a black list... only those users should be rejected

		ls.setUserAcceptList(null);
		ls.setUserBlockList(asSet("world"));

		assertThat(ls.acceptUsername("hello")).isTrue();
		assertThat(ls.acceptUsername("world")).isFalse();
		assertThat(ls.acceptUsername("hello,world")).isTrue();
		assertThat(ls.acceptUsername(null)).isFalse();

		// Set both accept and block list

		ls.setUserAcceptList(asSet("hello"));
		ls.setUserBlockList(asSet("world"));

		assertThat(ls.acceptUsername("hello")).isTrue();
		assertThat(ls.acceptUsername("world")).isFalse();
		assertThat(ls.acceptUsername("hello,world")).isFalse();
		assertThat(ls.acceptUsername(null)).isFalse();

		// Put same user in accept and block list... should be rejected

		ls.setUserAcceptList(asSet("hello", "world"));
		ls.setUserBlockList(asSet("world"));

		assertThat(ls.acceptUsername("hello")).isTrue();
		assertThat(ls.acceptUsername("world")).isFalse();
		assertThat(ls.acceptUsername("hello,world")).isFalse();
		assertThat(ls.acceptUsername(null)).isFalse();

	}

	@Test
	public void testAcceptAndBlockListsRegex() throws Exception {

		LoginServlet ls = new LoginServlet();

		// Just set a accept list... only those users should be accepted

		ls.setUserAcceptList(asSet("hel.*o"));

		assertThat(ls.acceptUsername("hello")).isTrue();
		assertThat(ls.acceptUsername("world")).isFalse();
		assertThat(ls.acceptUsername("hello,world")).isFalse();
		assertThat(ls.acceptUsername(null)).isFalse();

		// Just set a block list... only those users should be rejected

		ls.setUserAcceptList(null);
		ls.setUserBlockList(asSet("w.*rld"));

		assertThat(ls.acceptUsername("hello")).isTrue();
		assertThat(ls.acceptUsername("world")).isFalse();
		assertThat(ls.acceptUsername("hello,world")).isTrue();
		assertThat(ls.acceptUsername(null)).isFalse();

		// Set both accept and block list

		ls.setUserAcceptList(asSet("he.lo"));
		ls.setUserBlockList(asSet("wo..d"));

		assertThat(ls.acceptUsername("hello")).isTrue();
		assertThat(ls.acceptUsername("world")).isFalse();
		assertThat(ls.acceptUsername("hello,world")).isFalse();
		assertThat(ls.acceptUsername(null)).isFalse();

		// Put same user in accept and block list... should be rejected

		ls.setUserAcceptList(asSet("hello", "wor.*"));
		ls.setUserBlockList(asSet(".orld"));

		assertThat(ls.acceptUsername("hello")).isTrue();
		assertThat(ls.acceptUsername("world")).isFalse();
		assertThat(ls.acceptUsername("hello,world")).isFalse();
		assertThat(ls.acceptUsername(null)).isFalse();
	}

	@Test
	public void someRealLifeTests() throws Exception {

		LoginServlet ls = new LoginServlet();

		ls.setUserAcceptList(asSet(".*@braintribe.com"));

		assertThat(ls.acceptUsername("roman.kurmanowytsch@braintribe.com")).isTrue();
		assertThat(ls.acceptUsername("roman.kurmanowytsch@gmail.com")).isFalse();

		// Just set a black list... only those users should be rejected

		ls.setUserAcceptList(null);
		ls.setUserBlockList(asSet(".*@gmail.com"));

		assertThat(ls.acceptUsername("roman.kurmanowytsch@braintribe.com")).isTrue();
		assertThat(ls.acceptUsername("roman.kurmanowytsch@gmail.com")).isFalse();

	}

	@Test
	public void testGetRolesFromExternal() throws Exception {
		LoginServlet ls = new LoginServlet();

		String pattern = "roles";
		Map<String, Object> map = new HashMap<>();
		map.put("hello", "world");
		map.put("roles", "role1, role2");
		List<String> list = ls.getRolesFromExternal(pattern, FieldEncoding.CSV, map);
		assertThat(list).hasSize(2);
		assertThat(list).containsExactly("role1", "role2");

		pattern = "{roles}";
		list = ls.getRolesFromExternal(pattern, FieldEncoding.CSV, map);
		assertThat(list).hasSize(2);
		assertThat(list).containsExactly("role1", "role2");

		pattern = "prefix-{roles}";
		list = ls.getRolesFromExternal(pattern, FieldEncoding.CSV, map);
		assertThat(list).hasSize(2);
		assertThat(list).containsExactly("prefix-role1", "prefix-role2");

		pattern = "{roles}-postfix";
		list = ls.getRolesFromExternal(pattern, FieldEncoding.CSV, map);
		assertThat(list).hasSize(2);
		assertThat(list).containsExactly("role1-postfix", "role2-postfix");

		pattern = "prefix-{roles}-postfix";
		list = ls.getRolesFromExternal(pattern, FieldEncoding.CSV, map);
		assertThat(list).hasSize(2);
		assertThat(list).containsExactly("prefix-role1-postfix", "prefix-role2-postfix");

		pattern = "prefix-{roles}, {hello}";
		list = ls.getRolesFromExternal(pattern, FieldEncoding.CSV, map);
		assertThat(list).hasSize(3);
		assertThat(list).containsExactly("prefix-role1", "prefix-role2", "world");

		pattern = "prefix-{roles}, hello";
		list = ls.getRolesFromExternal(pattern, FieldEncoding.CSV, map);
		assertThat(list).hasSize(3);
		assertThat(list).containsExactly("prefix-role1", "prefix-role2", "world");

		pattern = "prefix-{roles}, {hello}-postfix";
		list = ls.getRolesFromExternal(pattern, FieldEncoding.CSV, map);
		assertThat(list).hasSize(3);
		assertThat(list).containsExactly("prefix-role1", "prefix-role2", "world-postfix");

		pattern = "prefix-{roles}, {hello}-postfix";
		map.put("roles", "[role1, role2]");
		list = ls.getRolesFromExternal(pattern, FieldEncoding.CSV, map);
		assertThat(list).hasSize(3);
		assertThat(list).containsExactly("prefix-role1", "prefix-role2", "world-postfix");

	}

	@Test
	public void testReadRolesWithPattern() throws Exception {
		LoginServlet ls = new LoginServlet();

		String pattern = "{roles}";
		Map<String, Object> map = new HashMap<>();
		map.put("hello", "world");
		map.put("roles", "role1, role2");
		List<String> list = ls.readRolesWithPattern(pattern, FieldEncoding.CSV, map);
		assertThat(list).hasSize(2);
		assertThat(list).containsExactly("role1", "role2");

		pattern = "prefix-{roles}";
		map = new HashMap<>();
		map.put("hello", "world");
		map.put("roles", "role1, role2");
		list = ls.readRolesWithPattern(pattern, FieldEncoding.CSV, map);
		assertThat(list).hasSize(2);
		assertThat(list).containsExactly("prefix-role1", "prefix-role2");

		pattern = "{roles}-postfix";
		map = new HashMap<>();
		map.put("hello", "world");
		map.put("roles", "role1, role2");
		list = ls.readRolesWithPattern(pattern, FieldEncoding.CSV, map);
		assertThat(list).hasSize(2);
		assertThat(list).containsExactly("role1-postfix", "role2-postfix");

		pattern = "prefix-{roles}-postfix";
		map = new HashMap<>();
		map.put("hello", "world");
		map.put("roles", "role1, role2");
		list = ls.readRolesWithPattern(pattern, FieldEncoding.CSV, map);
		assertThat(list).hasSize(2);
		assertThat(list).containsExactly("prefix-role1-postfix", "prefix-role2-postfix");

		pattern = "prefix-{roles}-postfix";
		map = new HashMap<>();
		map.put("hello", "world");
		list = ls.readRolesWithPattern(pattern, FieldEncoding.CSV, map);
		assertThat(list).isEmpty();

	}
}
