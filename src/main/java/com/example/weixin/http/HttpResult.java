package com.example.weixin.http;

public class HttpResult {
	private int statusCode;
	private String content;
	private boolean isError;

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isError() {
		return isError;
	}

	public void setError(boolean isError) {
		this.isError = isError;
	}

	public HttpResult(int statusCode, String content, boolean isError) {
		super();
		this.statusCode = statusCode;
		this.content = content;
		this.isError = isError;
	}

	public HttpResult() {
		super();
	}

	@Override
	public String toString() {
		return "HttpResponse [statusCode=" + statusCode + ", content=" + content + ", isError=" + isError + "]";
	}

}
