package com.poc.chatbot.CaseCreation.Constants;

public final class ApplicationConstants {
	
	public static final String uri ="uri";
	public static final String username = "username";
	public static final String password = "password";
	public static final String tos = "tos";
	
	public static final String claimNumberProperty = "DM_ClaimNumber";
	
	public static final String docClass = "DM_Demo_Doc_Class";
	
	public static final String caseType = "DM_Demo_CT";
	
	public static final String jaasStanzaName = "FileNetP8WSI";
	
	public static final String prefix = "demo";
	
	public static final String validateClaimQuery = "SELECT [This] FROM [DM_Demo_CT] WHERE [DM_Demo_Doc_Class] = claim";
	public static final String uploadDocumentQuery = "SELECT TOP 1 [This] FROM [DM_Demo_CT] WHERE [DM_Demo_Doc_Class] = claim";
	public static final String searchDocumentQuery = "SELECT [This] FROM [DM_Demo_CT] WHERE [DM_Demo_Doc_Class] = claim";
	
	public static final String documentUrl = "https://ibmbaw:9443/navigator/bookmark.jsp?desktop=baw&repositoryId=icmtos&docid={docId}&template_name=Document&version=released&vsId={versionId}";
	
}
