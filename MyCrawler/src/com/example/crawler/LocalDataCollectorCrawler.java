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
	public int fetchError = 0;

	private static final Pattern FILTERS = Pattern
			.compile(".*(\\.(css|js|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v"
					+ "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

	CrawlStat myCrawlStat;

	public LocalDataCollectorCrawler() {
		myCrawlStat = new CrawlStat();
	}
	
	/**
     * This function is called once the header of a page is fetched. It can be
     * overridden by sub-classes to perform custom logic for different status
     * codes. For example, 404 pages can be logged, etc.
     *
     * @param webUrl WebUrl containing the statusCode
     * @param statusCode Html Status Code number
     * @param statusDescription Html Status COde description
     */
    protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
        // Do nothing by default
        // Sub-classed can override this to add their custom functionality
    	//fetch url
    	MyPage p = new MyPage(webUrl.getURL(), statusCode);
    	myCrawlStat.fetchPageLists.add(p);
    }

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		return !FILTERS.matcher(href).matches() && href.startsWith("http://www.espn.com");
	}

	@Override
	public void visit(Page page) {
		logger.info("Visited: {}", page.getWebURL().getURL());
		myCrawlStat.incProcessedPages();
		MyPage p = new MyPage();
		// status code
		p.setStatusCode(page.getStatusCode());
		// URL
		p.setUrl(page.getWebURL().getURL());
		// out links false
		p.setOutlink(false);
		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData parseData = (HtmlParseData) page.getParseData();
			Set<WebURL> links = parseData.getOutgoingUrls();
			for (WebURL w : links) {
				MyPage tmp = new MyPage();
				tmp.setUrl(w.getURL());
				tmp.setOutlink(true);
				myCrawlStat.pageLists.add(tmp);
			}
			// content-type
			p.setType(page.getContentType());
			myCrawlStat.incTotalLinks(links.size());
			// outLinks
			p.setSize(links.size());
			try {
				myCrawlStat.incTotalTextSize(parseData.getText().getBytes("UTF-8").length);
				// size
				p.setSize(parseData.getText().getBytes("UTF-8").length);
			} catch (UnsupportedEncodingException ignored) {
				// Do nothing
			}
		}
		// We dump this crawler statistics after processing every 50 pages
		if ((myCrawlStat.getTotalProcessedPages() % 50) == 0) {
			dumpMyData();
		}
//		myCrawlStat.pageLists.add(p);
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

	protected void onContentFetchError(WebURL webUrl) {
		logger.warn("Can't fetch content of: {}", webUrl.getURL());
		myCrawlStat.fetchErrorPages();
		// Do nothing by default (except basic logging)
		// Sub-classed can override this to add their custom functionality
	}

}
