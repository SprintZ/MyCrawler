package com.example.crawler;

public class MyPage {
	private int statusCode;
	private String url;
	private int size;
	private int outlinks;
	private String type;
	
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getOutlinks() {
		return outlinks;
	}

	public void setOutlinks(int outlinks) {
		this.outlinks = outlinks;
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
}
