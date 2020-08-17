package com.poc.chatbot.CaseCreation.Service;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.DocumentSet;
import com.filenet.api.collection.RepositoryRowSet;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.property.Properties;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.query.RepositoryRow;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.ibm.casemgmt.api.Case;
import com.ibm.casemgmt.api.CaseType;
import com.ibm.casemgmt.api.constants.ModificationIntent;
import com.ibm.casemgmt.api.objectref.ObjectStoreReference;
import com.poc.chatbot.CaseCreation.CMConnection.CaseManagerConnection;
import com.poc.chatbot.CaseCreation.Constants.ApplicationConstants;
import com.poc.chatbot.CaseCreation.Pojo.DocumentLink;
import com.poc.chatbot.CaseCreation.PropertyReader.PropertyFileReader;

@Service
public class CaseCreationService {

	@Autowired
	public CaseManagerConnection caseManagerConnection;
	private JsonNode jsonNode = null;

	public String createCase(HttpServletRequest httpRequest,String claimNumber, String propertyData) throws Exception {
		ObjectStore targetOs = caseManagerConnection.getConnection(httpRequest,getJsonNode(propertyData));
		ObjectStoreReference targetOsRef = new ObjectStoreReference(targetOs);
		CaseType caseType = CaseType.fetchInstance(targetOsRef, jsonNode.get(ApplicationConstants.caseType).asText());
		Case pendingCase = Case.createPendingInstance(caseType);
		pendingCase.getProperties().putObjectValue(jsonNode.get(ApplicationConstants.claimNumberProperty).asText(), claimNumber);
		pendingCase.save(RefreshMode.REFRESH, null, ModificationIntent.MODIFY);
		String caseId = pendingCase.getId().toString();
		return caseId;

	}

	public String validateClaim(HttpServletRequest httpRequest,String claimNumber, String propertyData) throws Exception {
		String flag=null;
		ObjectStore targetOs = caseManagerConnection.getConnection(httpRequest,getJsonNode(propertyData));
		SearchSQL sql = new SearchSQL();
		String query=jsonNode.get(ApplicationConstants.validateClaimQuery).asText();
		query=query.replace("claim", claimNumber);
		sql.setQueryString(query);
		SearchScope scope = new SearchScope(targetOs);
		RepositoryRowSet fetchRows = scope.fetchRows(sql, (Integer) null,(PropertyFilter) null, new Boolean(true));
		Iterator iterator = fetchRows.iterator();
		if(iterator.hasNext())
		{
			flag="Yes";
		}else {
			flag="No";
		}
		return flag;

	}	
	@SuppressWarnings("unchecked")
	public String uploadFile(HttpServletRequest httpRequest,MultipartFile file, String claimNumber,String filName, String propertyData) throws Exception {

		ObjectStore targetOs = caseManagerConnection.getConnection(httpRequest,getJsonNode(propertyData));
		SearchSQL sql = new SearchSQL();
		String query=jsonNode.get(ApplicationConstants.uploadDocumentQuery).asText();
		query=query.replace("claim", claimNumber);
		sql.setQueryString(query);
		SearchScope scope = new SearchScope(targetOs);
		RepositoryRowSet fetchRows = scope.fetchRows(sql, (Integer) null,(PropertyFilter) null, new Boolean(true));
		Iterator iterator = fetchRows.iterator();
		Folder folder = null;
		String docId=null;
		String fileName = null;
		while (iterator.hasNext()) {

			RepositoryRow row = (RepositoryRow) iterator.next();
			folder = (Folder) row.getProperties().getObjectValue("This");
			fileName = filName;

			Document doc = Factory.Document.createInstance(targetOs, jsonNode.get(ApplicationConstants.docClass).asText());
			if (file != null) {
				ContentTransfer contentTransfer = Factory.ContentTransfer.createInstance();
				contentTransfer.setCaptureSource(file.getInputStream());
				ContentElementList contentElementList=Factory.ContentElement.createList();
				contentElementList.add(contentTransfer);
				doc.set_ContentElements(contentElementList);
				contentTransfer.set_RetrievalName(fileName);                       
				doc.set_MimeType(file.getContentType());
			}


			//Check-in the doc
			doc.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY,CheckinType.MAJOR_VERSION);                   
			//Get and put the doc properties
			Properties p = doc.getProperties();
			p.putValue("DocumentTitle",fileName);


			doc.save(RefreshMode.REFRESH);
			//Stores above content to the folder
			docId=doc.get_Id().toString();
			ReferentialContainmentRelationship rc = folder.file(doc,
					AutoUniqueName.AUTO_UNIQUE,
					fileName,
					DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
			rc.save(RefreshMode.REFRESH);


		}
		// Create Document
		return docId;

	}

	public List<DocumentLink> documentSearch(HttpServletRequest httpRequest,String claimNumber, String propertyData) throws Exception{
		ObjectStore targetOs = caseManagerConnection.getConnection(httpRequest,getJsonNode(propertyData));
		SearchSQL sql = new SearchSQL();
		String query = jsonNode.get(ApplicationConstants.searchDocumentQuery).asText();
		query=query.replace("claim", claimNumber);
		sql.setQueryString(query);
		System.out.println(query);
		List<DocumentLink> documentDetailsList = new ArrayList<DocumentLink>();

		SearchScope scope = new SearchScope(targetOs);

		RepositoryRowSet fetchRows = scope.fetchRows(sql, (Integer) null,

				(PropertyFilter) null, new Boolean(true));
		Iterator iterator = fetchRows.iterator();


		while (iterator.hasNext()) {

			RepositoryRow row = (RepositoryRow) iterator.next();
			Folder folderObj = (Folder) row.getProperties().getObjectValue("This");
			System.out.println("Folder Name "+folderObj.get_Name());
			DocumentSet documents = folderObj.get_ContainedDocuments(); 
			Iterator it = documents.iterator();
			while(it.hasNext())
			{
				Document retrieveDoc= (Document)it.next();
				String name = retrieveDoc.get_Name();
				String docId=retrieveDoc.get_Id().toString();
				String versionId=retrieveDoc.get_VersionSeries().get_Id().toString();
				String documentUrl = jsonNode.get(ApplicationConstants.documentUrl).asText();
				documentUrl=documentUrl.replace("{docId}", docId);
				documentUrl=documentUrl.replace("{versionId}", versionId);
				DocumentLink documentLink = new DocumentLink(name, documentUrl);
				documentDetailsList.add(documentLink);
			}
		}
		return documentDetailsList;
	}

	public List<Map<String,String>> search(HttpServletRequest httpRequest, String claimNumber, String actionTaken, String propertyData) throws Exception {
		// TODO Auto-generated method stub
		ObjectStore targetOs = caseManagerConnection.getConnection(httpRequest,getJsonNode(propertyData));
		SearchSQL sql = new SearchSQL();
		String query = jsonNode.get(actionTaken+"_"+ApplicationConstants.query).asText();
		System.out.println(query);
		query = query.replace("claim", claimNumber);
		sql.setQueryString(query);
		int rowCount = 0;
		List<String> casePropertyValuesList = new ArrayList<String>();
		//Map<Integer, List<String>> propertiesRowMap = new HashMap<Integer, List<String>>();


		List<Map<String,String>> finalList=new ArrayList<Map<String,String>>();

		SearchScope scope = new SearchScope(targetOs);

		RepositoryRowSet fetchRows = scope.fetchRows(sql, (Integer) null,

				(PropertyFilter) null, new Boolean(true));
		Iterator iterator = fetchRows.iterator();

		while (iterator.hasNext()) {
			Map<String,String> rowMap=new HashMap<String,String>();
			RepositoryRow row = (RepositoryRow) iterator.next();
			Properties prop = row.getProperties();
			//System.out.println("Folder Name " + folderObj.get_Name());
			String[] casePropertySymbolicNames = jsonNode.get(actionTaken+"_"+ApplicationConstants.symbolicNames).asText()
					.split(",");
			for (String symbolicName : casePropertySymbolicNames) {
				//casePropertyValuesList.add(folderObj.getProperties().getObjectValue(symbolicName).toString());
				Object propValue=prop.getObjectValue(symbolicName);
				if(propValue!=null){
					if(symbolicName.equals("CmAcmCaseState")){
						rowMap.put(symbolicName,ApplicationConstants.caseStatus.values()[Integer.parseInt(propValue.toString())].toString());
					}else{
						rowMap.put(symbolicName,propValue.toString());
					}
				}else {
					rowMap.put(symbolicName,"");
				}
			}
			//propertiesRowMap.put(++rowCount, casePropertyValuesList);
			finalList.add(rowMap);
		}

		//return propertiesRowMap;
		//System.out.println(finalList);
		return finalList;
	}

	private JsonNode getJsonNode(String propertyData) throws JsonMappingException, JsonProcessingException {
		if(jsonNode==null) {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(propertyData);
			jsonNode = node;
			return jsonNode;
		}
		return jsonNode;
	}


}
