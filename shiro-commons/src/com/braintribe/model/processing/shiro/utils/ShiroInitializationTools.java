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
package com.braintribe.model.processing.shiro.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.braintribe.utils.StringTools;

public class ShiroInitializationTools {

	public static Map<String, String> decodeMap(String listString) {
		if (!StringTools.isBlank(listString)) {
			String[] mapEntries = StringTools.splitSemicolonSeparatedString(listString, true);
			Map<String, String> result = new HashMap<>();
			for (String mapEntry : mapEntries) {
				int index = mapEntry.indexOf('=');
				if (index != -1) {
					String key = mapEntry.substring(0, index).trim();
					String value = mapEntry.substring(index + 1).trim();

					result.put(key, value);
				}
			}
			return result;
		} else {
			return null;
		}
	}

	public static Set<String> parseCollection(String listString) {
		if (!StringTools.isBlank(listString)) {
			String[] splitCommaSeparatedString = StringTools.splitCommaSeparatedString(listString, true);
			if (splitCommaSeparatedString != null && splitCommaSeparatedString.length > 0) {
				Set<String> set = new HashSet<>(Arrays.asList(splitCommaSeparatedString));
				return set;
			}
		}
		return null;
	}

}
