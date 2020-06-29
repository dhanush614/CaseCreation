package com.poc.chatbot.CaseCreation.Constants;

public final class ApplicationConstants {
	
	public static final String uri ="uri";
	public static final String username = "username";
	public static final String password = "password";
	public static final String tos = "tos";
	
	public static final String claimNumberProperty = "claimnumberproperty";
	
	public static final String docClass = "docclass";
	
	public static final String caseType = "casetype";
	
	public static final String jaasStanzaName = "FileNetP8WSI";
	
	public static final String prefix = "demo";
	
	public static final String caseSearchHeader = "caseSearch.header";

	public static final String caseSearchSymbolicNames = "caseSearch.symbolicnames";

	
	public static final String validateClaimQuery = "SELECT [This] FROM [DM_Demo_CT] WHERE [DM_ClaimNumber] = claim";
	public static final String uploadDocumentQuery = "SELECT TOP 1 [This] FROM [DM_Demo_CT] WHERE [DM_ClaimNumber] = claim";
	public static final String searchCasesQuery = "SELECT [This] FROM [DM_Demo_CT] WHERE [DM_ClaimNumber] = claim";
	public static final String searchDocumentQuery = "SELECT [This] FROM [DM_Demo_CT] WHERE [DM_ClaimNumber] = claim";
	
	public static final String documentUrl = "https://ibmbaw:9443/navigator/bookmark.jsp?desktop=baw&repositoryId=icmtos&docid={docId}&template_name=Document&version=released&vsId={versionId}";
	
	public static final String userAuthenticateException = "FNRCE0040E: E_NOT_AUTHENTICATED";
	
	public static final String passwordEmptyException = "FNRCA0035E: PASSWORD_IS_EMPTY_ERROR";
	
	public static final String symbolicNames = "SYMBOLIC_NAME";
	
	public static final String query = "QUERY";
	
	public static enum caseStatus {New, Initializing, Working, Complete, Failed}
	
}
