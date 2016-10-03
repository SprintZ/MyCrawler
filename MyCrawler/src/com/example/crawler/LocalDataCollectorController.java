package com.example.crawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class LocalDataCollectorController {

	// # fetches attempted
	private static int fetches_attempted = 0;
	//# fetches succeeded:
	private static int fetches_succeeded = 0;
	// # fetches aborted
	private static int fetches_aborted = 0;
	// # fetches failed
	private static int fetches_failed = 0;
	// Total URLs extracted
	private static int total_urls_extracted = 0;
	// # unique URLs extracted
	private static int unique_urls_extracted = 0;
	// # unique URLs within News Site
	private static int unique_urls_within = 0;
	// # unique URLs outside News Site
	private static int unique_url_outside = 0;
	// status code
	private static int OK = 0;
	private static int Moved_Permanently = 0;
	private static int Unauthorized = 0;
	private static int Forbidden = 0;
	private static int Not_Found = 0;
	// File size
	private static int less_1KB;
	private static int less_10KB;
	private static int less_100KB;
	private static int less_1MB;
	private static int large_1MB;
	// Content Types
	private static int text_html;
	private static int image_gif;
	private static int image_jpeg;
	private static int image_png;
	private static int application_pdf;

	private static final Logger logger = LoggerFactory.getLogger(LocalDataCollectorController.class);

	public static void main(String[] args) throws Exception {
		// if (args.length != 2) {
		// logger.info("Needed parameters: ");
		// logger.info("\t rootFolder (it will contain intermediate crawl
		// data)");
		// logger.info("\t numberOfCralwers (number of concurrent threads)");
		// return;
		// }

		String domain = "http://www.espn.com/";
		String rootFolder = "data/crawl";
		int numberOfCrawlers = 10;

		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(rootFolder);
		config.setMaxPagesToFetch(100);
		config.setPolitenessDelay(100);
		//fetch binary content
		config.setIncludeBinaryContentInCrawling(true);
		//process binary content
		config.setIncludeBinaryContentInCrawling(true);

		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

		controller.addSeed(domain);
		controller.start(LocalDataCollectorCrawler.class, numberOfCrawlers);

		List<Object> crawlersLocalData = controller.getCrawlersLocalData();
		long totalLinks = 0;
		long totalTextSize = 0;
		int totalProcessedPages = 0;
		for (Object localData : crawlersLocalData) {
			CrawlStat stat = (CrawlStat) localData;
			totalLinks += stat.getTotalLinks();
			totalTextSize += stat.getTotalTextSize();
			totalProcessedPages += stat.getTotalProcessedPages();
		}

		// write fetch_NewsSite1.csv
		try {
			File writename = new File("fetch_NewsSite1.csv");
			writename.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(writename));
			for (Object localData : crawlersLocalData) {
				CrawlStat stat = (CrawlStat) localData;
				for (MyPage p : stat.fetchPageLists) {
					out.write(p.getUrl());
					out.write(",");
					out.write(p.getStatusCode() + "");
					out.write("\n");
				}
			}
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// write visit_NewsSite1.csv
		try {
			File writename = new File("visit_NewsSite1.csv");
			writename.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(writename));
			for (Object localData : crawlersLocalData) {
				CrawlStat stat = (CrawlStat) localData;
				for (MyPage p : stat.visitPageLists) {
					out.write(p.getUrl());
					out.write(",");
					out.write(p.getSize() + "");
					out.write(",");
					out.write(p.getNumberOfOutLinks() + "");
					out.write(",");
					out.write(p.getType());
					out.write("\n");
				}
			}
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// write urls_Newsite1.csv
		try {
			File writename = new File("urls_Newsite1.csv");
			writename.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(writename));
			for (Object localData : crawlersLocalData) {
				CrawlStat stat = (CrawlStat) localData;
				for (MyPage p : stat.urlLists) {
					out.write(p.getUrl());
					out.write(",");
					if (p.getUrl().startsWith(domain)) {
						out.write("OK");
					} else {
						out.write("N_OK");
					}
					out.write("\n");
				}
			}
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("Aggregated Statistics:");
		logger.info("\tProcessed Pages: {}", totalProcessedPages);
		logger.info("\tTotal Links found: {}", totalLinks);
		logger.info("\tTotal Text Size: {}", totalTextSize);

		// CrawlReport
		//fetches_attempted += fetches_aborted
		HashSet<String> hs = new HashSet<String>();
		for (Object localData : crawlersLocalData) {
			CrawlStat stat = (CrawlStat) localData;
			fetches_aborted += stat.getFetchError();
			for (MyPage p : stat.fetchPageLists) {
				fetches_attempted++;
				int code = p.getStatusCode();
				if(code >= 200 && code < 300) {
					fetches_succeeded++;
				}
				else if(p.getStatusCode() >= 300) {
					fetches_failed++;
				}
				if(code == 200) {
					OK++;
				}
				else if(code == 301) {
					Moved_Permanently++;
				}
				else if(code == 401) {
					Unauthorized++;
				}
				else if(code == 403) {
					Forbidden++;
				}
				else if(code == 404) {
					Not_Found++;
				}
				
				int size = p.getSize();
				if(size > 0 && size < 1) {
					less_1KB++;
				}
				else if(size < 10) {
					less_10KB++;
				}
				else if(size < 100) {
					less_100KB++;
				}
				else if(size < 1024) {
					less_1MB++;
				}
				else if(size >= 1024) {
					large_1MB++;
				}
				
				String[] str = p.getType().split(";");
				if(str[0].equals("text/html")) {
					text_html++;
				}
				else if(str[0].equals("image/jpeg")) {
					image_jpeg++;
				}
				else if(str[0].equals("image/png")) {
					image_png++;
				}
				else if(str[0].equals("application/pdf")) {
					application_pdf++;
				}	
			}
		}
		fetches_attempted += fetches_aborted;

		unique_urls_extracted = hs.size();
		for(String str: hs) {
			if(str.startsWith(domain)) {
				unique_urls_within++;
			}
			else {
				unique_url_outside++;
			}
		}
		
		//System.out
		System.out.println("# fetches attempted: " + fetches_attempted);
		System.out.println("# fetches succeeded: " + fetches_succeeded);
		System.out.println("# fetches aborted: " + fetches_aborted);
		System.out.println("# fetches failed: " + fetches_failed);
		System.out.println("Total URLs extracted: " + total_urls_extracted);
		System.out.println("# unique URLs extracted: " + unique_urls_extracted);
		System.out.println("# unique URLs within News Site " + unique_urls_within);
		System.out.println("# unique URLs outside News Site: " + unique_url_outside);
		System.out.println("200 OK: " + OK);
		System.out.println("301 Moved Permanently: " + Moved_Permanently);
		System.out.println("401 Unauthorized: " + Unauthorized);
		System.out.println("403 Forbidden: " + Forbidden);
		System.out.println("404 Not Found: " + Not_Found);
		System.out.println("< 1KB: " + less_1KB);
		System.out.println("1KB ~ <10KB: " + less_10KB);
		System.out.println("10KB ~ <100KB: " + less_100KB);
		System.out.println("100KB ~ <1MB: " + less_1MB);
		System.out.println(">= 1MB: " + large_1MB);
		System.out.println("text/html: " + text_html);
		System.out.println("image/gif: " + image_gif);
		System.out.println("image/jpeg: " + image_jpeg);
		System.out.println("image/png: " + image_png);
		System.out.println("application/pdf: " + application_pdf);
	}
}
