package org.company.crawler.web;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Stream;

import org.company.crawler.parse.HttpURINormalizer;
import org.company.crawler.web.link.IWebPageLink;
import org.company.crawler.web.link.WebPageLink;

import com.google.common.collect.Iterators;

/**
 * @author Evgeniy Vishnyakov
 */
public class WebLinksInputStreamReader  {

	public Iterator<IWebPageLink> readLinks(InputStream in) {
		return Iterators.filter(Iterators.transform(new Scanner(in,"UTF-8").useDelimiter("\n"), 
			text-> {
				try {
					URI uri = URI.create(!text.startsWith("http") ? "http://" + text : text);
					return new WebPageLink(new HttpURINormalizer().normalize(uri));	
				} catch(Exception e) {
					return null;
				}
			}), Objects::nonNull);
	}
	
}
