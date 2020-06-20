package com.poc.chatbot.CaseCreation.Pojo;

public class DocumentLink {
	
	private String documentName;
	private String documentUrl;
	
	public DocumentLink() {
		super();
	}

	public DocumentLink(String documentName, String documentUrl) {
		super();
		this.documentName = documentName;
		this.documentUrl = documentUrl;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getDocumentUrl() {
		return documentUrl;
	}

	public void setDocumentUrl(String documentUrl) {
		this.documentUrl = documentUrl;
	}

	@Override
	public String toString() {
		return "DocumentLink [documentName=" + documentName + ", documentUrl=" + documentUrl + "]";
	}

}
