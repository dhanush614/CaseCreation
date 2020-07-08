package com.poc.chatbot.CaseCreation.Constants;

public final class ApplicationConstants {
	
	public static final String uri ="CASEMANAGER_URI";
	public static final String username = "USERNAME";
	public static final String password = "PASSWORD";
	public static final String tos = "TARGET_OBJECT_STORE";
	
	public static final String claimNumberProperty = "SYMBOLIC_NAME";
	
	public static final String docClass = "DOC_CLASS";
	
	public static final String caseType = "CASE_TYPE";
	
	public static final String jaasStanzaName = "FileNetP8WSI";
	
	public static final String prefix = "demo";
	
	public static final String caseManager = "CASEMANAGER";
	
	public static final String caseSearchHeader = "caseSearch.header";

	public static final String caseSearchSymbolicNames = "caseSearch.symbolicnames";

	
	public static final String validateClaimQuery = "VALIDATE_CLAIM_QUERY";
	public static final String uploadDocumentQuery = "UPLOAD_DOCUMENT_QUERY";
	public static final String searchCasesQuery = "SELECT [This] FROM [DM_Demo_CT] WHERE [DM_ClaimNumber] = claim";
	public static final String searchDocumentQuery = "DOCUMENTSEARCH_QUERY";
	
	public static final String documentUrl = "DOCUMENT_URI";
	
	public static final String userAuthenticateException = "FNRCE0040E: E_NOT_AUTHENTICATED";
	
	public static final String passwordEmptyException = "FNRCA0035E: PASSWORD_IS_EMPTY_ERROR";
	
	public static final String symbolicNames = "SYMBOLIC_NAME";
	
	public static final String query = "QUERY";
	
	public static enum caseStatus {New, Initializing, Working, Complete, Failed}
	
}
