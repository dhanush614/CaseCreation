package com.poc.chatbot.CaseCreation.Service;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.filenet.api.collection.ContentElementList;
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

@Service
public class CaseCreationService {

	@Autowired
	CaseManagerConnection caseManagerConnection;

	public String createCase(String claimNumber) {
		ObjectStore targetOs = CaseManagerConnection.getConnection();
		ObjectStoreReference targetOsRef = new ObjectStoreReference(targetOs);
		CaseType caseType = CaseType.fetchInstance(targetOsRef, "DM_Demo_CT");
		Case pendingCase = Case.createPendingInstance(caseType);
		pendingCase.getProperties().putObjectValue("DM_ClaimNumber", claimNumber);
		pendingCase.save(RefreshMode.REFRESH, null, ModificationIntent.MODIFY);
		String caseId = pendingCase.getId().toString();
		return caseId;

	}

	public String validateClaim(String claimNumber) {
		String flag=null;
		ObjectStore targetOs = CaseManagerConnection.getConnection();
		SearchSQL sql = new SearchSQL();
		String query="SELECT [This] FROM [DM_Demo_CT] WHERE [DM_ClaimNumber] = claim";
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
	public String uploadFile(MultipartFile file, String claimNumber,String filName) throws IOException {
		/*
		 * byte[] bytes = file.getBytes(); Path path = Paths.get("C://" +
		 * file.getOriginalFilename()); Files.write(path, bytes);
		 */
		System.out.println(filName+" FileName");
		ObjectStore targetOs = CaseManagerConnection.getConnection();
		SearchSQL sql = new SearchSQL();
		String query="SELECT TOP 1 [This] FROM [DM_Demo_CT] WHERE [DM_ClaimNumber] = claim";
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
			String docClass = "DM_Demo_Doc_Class";

			Document doc = Factory.Document.createInstance(targetOs, docClass);
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
}
