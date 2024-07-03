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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;

import com.braintribe.exception.Exceptions;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;

public class SerializableCodeVerifier extends CodeVerifier implements Serializable {

	private static final long serialVersionUID = -8223681020765931679L;

	public SerializableCodeVerifier(String value) {
		super(value);
	}

	public static SerializableCodeVerifier create(CodeVerifier source) {
		return new SerializableCodeVerifier(source.getValue());
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		int len = in.readInt();
		byte[] valueBytes = new byte[len];
		in.readFully(valueBytes);
		setValue(valueBytes);

		long expDateTime = in.readLong();
		if (expDateTime != -1) {
			Date expDate = new Date(expDateTime);
			setExpirationDate(expDate);
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		byte[] valueBytes = super.getValueBytes();
		int len = valueBytes.length;

		out.writeInt(len);
		out.write(valueBytes);

		Date expirationDate = super.getExpirationDate();
		if (expirationDate == null) {
			out.writeLong(-1L);
		} else {
			out.writeLong(expirationDate.getTime());
		}
	}

	private void setValue(byte[] newValue) {
		try {
			Field field = Secret.class.getDeclaredField("value");
			field.setAccessible(true);
			field.set(this, newValue);
		} catch (Exception e) {
			throw Exceptions.unchecked(e, "Could not deserialize a CodeVerifier");
		}
	}
	private void setExpirationDate(Date newValue) {
		try {
			Field field = Secret.class.getDeclaredField("expDate");
			field.setAccessible(true);
			field.set(this, newValue);
		} catch (Exception e) {
			throw Exceptions.unchecked(e, "Could not deserialize a CodeVerifier");
		}
	}
}
