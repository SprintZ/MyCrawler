package com.example.crawler;

import java.util.ArrayList;

public class MyPage {
	private int statusCode;
	private String url;
	private int size = 0;
	//whether is outlink
	private boolean outlink;
	//whether is visit
	private boolean visited;
	private String type = "";
	private int numberOfOutLinks;
	public ArrayList<MyPage> outlinks = new ArrayList<MyPage>();

	
	public int getNumberOfOutLinks() {
		return numberOfOutLinks;
	}

	public void setNumberOfOutLinks(int numberOfOutLinks) {
		this.numberOfOutLinks = numberOfOutLinks;
	}
	
	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public boolean isOutlink() {
		return outlink;
	}

	public void setOutlink(boolean outlink) {
		this.outlink = outlink;
	}
	
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public MyPage() {
		super();
		// TODO Auto-generated constructor stub	
	}
	
	public MyPage(String url) {
		this.url = url;
		this.outlink = false;
		this.visited = false;
	}
	
	public MyPage(String url, int code) {
		this.url = url;
		this.statusCode = code;
		this.outlink = false;
		this.visited = false;
	}
}
