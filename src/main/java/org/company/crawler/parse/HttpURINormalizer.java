package org.company.crawler.parse;

import java.net.URI;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * @author Evgeniy Vishnyakov
 */
public class HttpURINormalizer {

	private static final int DEFAULT_HTTP_PORT = 80;
	
	public String normalize(URI uri) {
		Preconditions.checkNotNull(uri);
		Preconditions.checkArgument(isHttpURI(uri), "URI must be http, uri = '" + uri + "'");
		URI normalizedUri = uri.normalize();
		String host = retrieveHost(normalizedUri);
		if(!host.startsWith("www")) {
			host = "www." + host;
		}
		String path = retrievePath(normalizedUri);
		if(path.isEmpty()) {
			path = "/";
		}
		int port = retrievePort(normalizedUri);
		StringBuilder sb = new StringBuilder().append("http://").append(host);
		if(port > 0) {
			sb.append(":").append(port);
		}
		return sb.append(path).toString();
	}
	
	public boolean isHttpURI(URI uri) {
		String scheme = retrieveScheme(uri);
		return scheme.isEmpty() || "http".equals(scheme) || "https".equals(scheme);
	}

	private String retrieveScheme(URI uri) {
		return Strings.nullToEmpty(uri.getScheme()).trim().toLowerCase();
	}

	private String retrieveHost(URI uri) {
		return Strings.nullToEmpty(uri.getHost()).trim().toLowerCase();
	}

	private String retrievePath(URI uri) {
		return Strings.nullToEmpty(uri.getPath()).trim().toLowerCase();
	}

	private int retrievePort(URI uri) {
		if(uri.getPort() == DEFAULT_HTTP_PORT) {
			return -1;
		}
		return uri.getPort();
	}

}
