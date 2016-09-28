package com.example.crawler;

import edu.uci.ics.crawler4j.crawler.WebCrawler;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class LocalDataCollectorCrawler extends WebCrawler {
	private static final Logger logger = LoggerFactory.getLogger(LocalDataCollectorCrawler.class);

	private static final Pattern FILTERS = Pattern
			.compile(".*(\\.(css|js|bmp|gif|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|pdf"
					+ "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

	CrawlStat myCrawlStat;

	public LocalDataCollectorCrawler() {
		myCrawlStat = new CrawlStat();
	}

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		return !FILTERS.matcher(href).matches() && href.startsWith("http://www.ics.uci.edu/");
	}

	@Override
	public void visit(Page page) {
		logger.info("Visited: {}", page.getWebURL().getURL());
		System.out.println(page.getWebURL().getURL());
		System.out.println(page.getStatusCode());
		myCrawlStat.incProcessedPages();
		MyPage p = new MyPage();
		//status code
		p.setStatusCode(page.getStatusCode());
		//URL
		p.setUrl(page.getWebURL().getURL());
		
		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData parseData = (HtmlParseData) page.getParseData();
			Set<WebURL> links = parseData.getOutgoingUrls();
			for(WebURL w:links) {
				MyPage tmp = new MyPage();
				tmp.setUrl(w.getURL());
				myCrawlStat.pageLists.add(tmp);
			}
			//content-type
			p.setType(page.getContentType());
			myCrawlStat.incTotalLinks(links.size());
			//outLinks
			p.setSize(links.size());
			try {
				myCrawlStat.incTotalTextSize(parseData.getText().getBytes("UTF-8").length);
				//size
				p.setSize(parseData.getText().getBytes("UTF-8").length);
			} catch (UnsupportedEncodingException ignored) {
				// Do nothing
			}
		}
		// We dump this crawler statistics after processing every 50 pages
		if ((myCrawlStat.getTotalProcessedPages() % 50) == 0) {
			dumpMyData();
		}
		myCrawlStat.pageLists.add(p);
	}

	/**
	 * This function is called by controller to get the local data of this
	 * crawler when job is finished
	 */
	@Override
	public Object getMyLocalData() {
		return myCrawlStat;
	}

	/**
	 * This function is called by controller before finishing the job. You can
	 * put whatever stuff you need here.
	 */
	@Override
	public void onBeforeExit() {
		dumpMyData();
	}

	public void dumpMyData() {
		int id = getMyId();
		// You can configure the log to output to file
		logger.info("Crawler {} > Processed Pages: {}", id, myCrawlStat.getTotalProcessedPages());
		logger.info("Crawler {} > Total Links Found: {}", id, myCrawlStat.getTotalLinks());
		logger.info("Crawler {} > Total Text Size: {}", id, myCrawlStat.getTotalTextSize());
	}
}
