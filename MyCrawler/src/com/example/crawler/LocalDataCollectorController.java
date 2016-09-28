package com.example.crawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class LocalDataCollectorController {

	private static final Logger logger = LoggerFactory.getLogger(LocalDataCollectorController.class);

	public static void main(String[] args) throws Exception {
//		if (args.length != 2) {
//			logger.info("Needed parameters: ");
//			logger.info("\t rootFolder (it will contain intermediate crawl data)");
//			logger.info("\t numberOfCralwers (number of concurrent threads)");
//			return;
//		}

		String domain = "http://www.ics.uci.edu/";
		String rootFolder = "data/crawl";
		int numberOfCrawlers = 5;

		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(rootFolder);
		config.setMaxPagesToFetch(3);
		config.setPolitenessDelay(1000);

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
		
		//write fetch_NewsSite1.csv
		try {
			File writename = new File("fetch_NewsSite1.csv");
	        writename.createNewFile();
	        BufferedWriter out = new BufferedWriter(new FileWriter(writename));  
	        for(Object localData: crawlersLocalData) {
	        	CrawlStat stat = (CrawlStat) localData;
	        	for(MyPage p: stat.pageLists) {
	        		out.write(p.getUrl());
		        	out.write(",");
		        	out.write(p.getStatusCode()+"");
		        	out.write("\n");
	        	}        	
	        }
	        out.flush(); 
	        out.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		//write visit_NewsSite1.csv
		try {
			File writename = new File("visit_NewsSite1.csv");
	        writename.createNewFile();
	        BufferedWriter out = new BufferedWriter(new FileWriter(writename));  
	        for(Object localData: crawlersLocalData) {
	        	CrawlStat stat = (CrawlStat) localData;
	        	for(MyPage p: stat.pageLists) {
	        		if(!(p.getStatusCode() == 200)) {
	        			continue;
	        		}
	        		out.write(p.getUrl());
		        	out.write(",");
		        	out.write(p.getSize()+"");
		        	out.write(",");
		        	out.write(p.getOutlinks()+"");
		        	out.write(",");
		        	out.write(p.getType());
		        	out.write("\n");
	        	}        	
	        }
	        out.flush(); 
	        out.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		//write urls_Newsite1.csv
		try {
			File writename = new File("urls_Newsite1.csv");
	        writename.createNewFile();
	        BufferedWriter out = new BufferedWriter(new FileWriter(writename));  
	        for(Object localData: crawlersLocalData) {
	        	CrawlStat stat = (CrawlStat) localData;
	        	for(MyPage p: stat.pageLists) {
	        		out.write(p.getUrl());
		        	out.write(",");
		        	if(p.getUrl().startsWith(domain)) {
		        		out.write("OK");
		        	}
		        	else {
		        		out.write("N_OK");
		        	}
		        	out.write("\n");
	        	}        	
	        }
	        out.flush(); 
	        out.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
		logger.info("Aggregated Statistics:");
		logger.info("\tProcessed Pages: {}", totalProcessedPages);
		logger.info("\tTotal Links found: {}", totalLinks);
		logger.info("\tTotal Text Size: {}", totalTextSize);
	}
}
