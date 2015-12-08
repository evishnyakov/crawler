package org.company.crawler.web.link;

import java.net.URI;

import org.apache.commons.httpclient.util.URIUtil;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * @author Evgeniy Vishnyakov
 */
public class WebPageLink implements IWebPageLink {

	private String url;

	public WebPageLink(String url) {
		this.url = Preconditions.checkNotNull(url);
	}
	
	@Override
	public String getURI() {
		return url;
	}
	
	@Override
	public String getHost() {
		try {
			return Strings.nullToEmpty(URI.create(URIUtil.encodeQuery(url, "UTF-8")).getHost());
//			String host = Strings.nullToEmpty(URI.create(URIUtil.encodeQuery(url, "UTF-8")).getHost());
//			int ix1 = host.lastIndexOf('.');
//			int ix2 = ix1 > 0 ? host.lastIndexOf('.', ix1 - 1) : -1;
//			if(ix2 > -1) {
//				host = host.substring(ix2+1);
//			}
//			return host;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(!(obj instanceof WebPageLink)) {
			return false;
		}
		WebPageLink other = (WebPageLink)obj;
		return url.equals(other.url);
	}
	
	@Override
	public int hashCode() {
		return url.hashCode();
	}

}
