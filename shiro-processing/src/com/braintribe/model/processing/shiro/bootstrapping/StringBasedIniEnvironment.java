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
package com.braintribe.model.processing.shiro.bootstrapping;

import java.util.function.Supplier;

import org.apache.shiro.config.Ini;

import io.buji.pac4j.env.Pac4jIniEnvironment;

public class StringBasedIniEnvironment extends Pac4jIniEnvironment {

	private Supplier<String> iniConfigSupplier;


	public void setIniConfigSupplier(Supplier<String> iniConfigSupplier) {
		this.iniConfigSupplier = iniConfigSupplier;
	}

	@Override
	protected Ini parseConfig() {
		Ini ini = new Ini();
		String iniConfig = iniConfigSupplier.get();
		if (iniConfig != null) {
			ini.load(iniConfig);
			super.setIni(ini);
		}

		return super.parseConfig();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Extension of Pac4jIniEnvironment with config supplier: "+iniConfigSupplier);
		sb.append(" (");
		sb.append(super.toString());
		sb.append(")");
		return sb.toString();
	}
}
