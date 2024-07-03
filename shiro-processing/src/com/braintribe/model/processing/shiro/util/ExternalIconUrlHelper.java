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
package com.braintribe.model.processing.shiro.util;

import java.io.InputStream;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import com.braintribe.cfg.Configurable;
import com.braintribe.cfg.Required;
import com.braintribe.logging.Logger;
import com.braintribe.model.shiro.deployment.client.ShiroInstagramOAuth20Client;
import com.braintribe.transport.http.HttpClientProvider;
import com.braintribe.transport.http.ResponseEntityInputStream;
import com.braintribe.utils.IOTools;
import com.braintribe.utils.StringTools;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

public class ExternalIconUrlHelper {

	private static Logger logger = Logger.getLogger(ExternalIconUrlHelper.class);

	private HttpClientProvider httpClientProvider;

	public String getIconUrlFromInstagram(ShiroInstagramOAuth20Client client, Map<String, Object> map) {
		String userInfoUrl = client.getUserInformationUrl();
		if (StringTools.isBlank(userInfoUrl)) {
			return null;
		}

		String url = StringTools.patternFormat(userInfoUrl, map);

		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		try {
			httpClient = httpClientProvider.provideHttpClient();
			HttpGet get = new HttpGet(url);
			response = httpClient.execute(get);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				logger.debug("Got a non-200 response from " + url + ": " + response);
			} else {
				try (InputStream is = new ResponseEntityInputStream(response)) {
					String jsonString = IOTools.slurp(is, "UTF-8");
					if (!StringTools.isBlank(jsonString)) {
						logger.trace(() -> "Received user information: " + jsonString);
						String userIconUrl = acquireUserIconUrl(jsonString);
						return userIconUrl;
					}
				}
			}
		} catch (Exception e) {
			logger.error("Could not download user information at: " + url, e);
		} finally {
			IOTools.closeCloseable(response, logger);
			IOTools.closeCloseable(httpClient, logger);
		}
		return null;
	}

	private String acquireUserIconUrl(String jsonString) {
		JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
		try {
			JSONObject map = (JSONObject) parser.parse(jsonString);
			JSONObject graphql = (JSONObject) map.get("graphql");
			JSONObject user = (JSONObject) graphql.get("user");
			String profilePicUrl = user.getAsString("profile_pic_url");
			return profilePicUrl;
		} catch (Exception e) {
			logger.warn(() -> "Could not parse JSON " + jsonString, e);
		}
		return null;
	}

	@Configurable
	@Required
	public void setHttpClientProvider(HttpClientProvider httpClientProvider) {
		this.httpClientProvider = httpClientProvider;
	}

}
