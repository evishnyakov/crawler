package org.company.crawler;

import java.net.URI;

import org.company.crawler.parse.HttpURINormalizer;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Evgeniy Vishnyakov
 */
public class HttpURINormalizerTest {

	private HttpURINormalizer httpURINormalizer = new HttpURINormalizer();
	
	@Test
	public void ordinaryLink() throws Exception {
		String result = httpURINormalizer.normalize(URI.create("http://www.test.net/"));
		Assert.assertEquals("http://www.test.net/", result);
	}

	@Test
	public void ordinaryLinkWithPath() throws Exception {
		String result = httpURINormalizer.normalize(URI.create("http://www.fake.com/page2.htm"));
		Assert.assertEquals("http://www.fake.com/page2.htm", result);
	}

	@Test
	public void ordinaryLinkWithRelativePath() throws Exception {
		String result = httpURINormalizer.normalize(URI.create("http://www.fake.com/../page1.htm"));
		Assert.assertEquals("http://www.fake.com/../page1.htm", result);
	}
	
	@Test
	public void ordinaryLinkWithFragment() throws Exception {
		String result = httpURINormalizer.normalize(URI.create("http://www.fake.com#heading1"));
		Assert.assertEquals("http://www.fake.com/", result);
	}
	
	@Test
	public void ordinaryLinkWithRelativePath2() throws Exception {
		String result = httpURINormalizer.normalize(URI.create("http://www.fake.com/./page2.htm"));
		Assert.assertEquals("http://www.fake.com/page2.htm", result);
	}

	@Test
	public void ordinaryLinkWithRelativePath3() throws Exception {
		String result = httpURINormalizer.normalize(URI.create("http://www.fake.com/./../page1.htm"));
		Assert.assertEquals("http://www.fake.com/../page1.htm", result);
	}

	@Test
	public void ordinaryLinkWithRelativePath4() throws Exception {
		String result = httpURINormalizer.normalize(URI.create("http://www.fake.com/.././page1.htm"));
		Assert.assertEquals("http://www.fake.com/../page1.htm", result);
	}
	
	@Test
	public void ordinaryLinkWithoutWWW() throws Exception {
		String result = httpURINormalizer.normalize(URI.create("http://test.net/"));
		Assert.assertEquals("http://www.test.net/", result);
	}
	
	@Test
	public void ordinaryHTTPSLinkWithoutWWW() throws Exception {
		String result = httpURINormalizer.normalize(URI.create("https://test.net/"));
		Assert.assertEquals("http://www.test.net/", result);
	}
	
	@Test
	public void ordinaryLinkWithQuery() throws Exception {
		String result = httpURINormalizer.normalize(URI.create("http://www.test.net/t1.html?a=1"));
		Assert.assertEquals("http://www.test.net/t1.html", result);
	}
	
	@Test
	public void ordinaryLinkWithPort() throws Exception {
		String result = httpURINormalizer.normalize(URI.create("http://www.test.net:8080/"));
		Assert.assertEquals("http://www.test.net:8080/", result);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void notHttpLink() throws Exception {
		httpURINormalizer.normalize(URI.create("mailto:nobody@html.net"));
	}
	
}
