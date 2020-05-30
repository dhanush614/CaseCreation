package com.poc.chatbot.CaseCreation.Service;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.filenet.api.collection.ContentElementList;
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

	public String uploadDocument(MultipartFile file) {
		try {
			byte[] bytes = file.getBytes();
			Path path = Paths.get("C://" + file.getOriginalFilename());
			Files.write(path, bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "{B0541372-0000-CF14-920C-D2889394ECCE}";
	}
	
	@SuppressWarnings("unchecked")
	public String uploadFile(MultipartFile file) throws IOException {
		/*
		 * byte[] bytes = file.getBytes(); Path path = Paths.get("C://" +
		 * file.getOriginalFilename()); Files.write(path, bytes);
		 */
		System.out.println(file);
		ObjectStore targetOs = CaseManagerConnection.getConnection();
		String folderName = "/Demo_Doc";
		Folder folder=Factory.Folder.fetchInstance(targetOs, folderName, null);
		String fileName = file.getName();
		System.out.println(file.getContentType());
		// Create Document

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
		String docId=doc.get_Id().toString();
		ReferentialContainmentRelationship rc = folder.file(doc,
				AutoUniqueName.AUTO_UNIQUE,
				fileName,
				DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
		rc.save(RefreshMode.REFRESH);

		return docId;
	}
}
