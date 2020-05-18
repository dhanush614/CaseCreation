package com.poc.chatbot.CaseCreation.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
		return claimNumber;

	}

	public String uploadDocument(MultipartFile file) {
		try {
			byte[] bytes = file.getBytes();
			Path path = Paths.get("E://" + file.getOriginalFilename());
			Files.write(path, bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "File Uploaded Successfully..!!";
	}
}
