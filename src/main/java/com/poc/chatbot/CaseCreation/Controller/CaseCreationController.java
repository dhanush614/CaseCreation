package com.poc.chatbot.CaseCreation.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.chatbot.CaseCreation.Constants.ApplicationConstants;
import com.poc.chatbot.CaseCreation.Pojo.DocumentLink;
import com.poc.chatbot.CaseCreation.Service.CaseCreationService;

@RestController
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:3001","https://cm-assistant-bot.eu-gb.mybluemix.net/"})
public class CaseCreationController {

	@Autowired
	CaseCreationService caseCreationService;

	@PostMapping("/create")
	public String caseCreation(HttpServletRequest httpRequest,@RequestParam("claimNumber") String claimNumber,@RequestParam("propertyData") String propertyData) {
		String caseId = null;
		try {
			caseId = caseCreationService.createCase(httpRequest,claimNumber,propertyData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return exceptionHandler(e);
		}
		return caseId;
	}
	
	@PostMapping("/validate")
	public String validateClaim(HttpServletRequest httpRequest,@RequestParam("claimNumber") String claimNumber,@RequestParam("propertyData") String propertyData){
		
		String validateFlag = null;
		try {
			validateFlag = caseCreationService.validateClaim(httpRequest,claimNumber,propertyData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return exceptionHandler(e);
		}
		return validateFlag;
	}

	@PostMapping("/upload")
	@ResponseBody
	public String uploadDocument(HttpServletRequest httpRequest,@RequestParam("uploadFile") MultipartFile file, @RequestParam("claimNumber") String claimNumber,@RequestParam("fileName") String fileName,@RequestParam("propertyData") String propertyData) {
		String result = null;
		try {
			result = caseCreationService.uploadFile(httpRequest,file, claimNumber,fileName,propertyData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return exceptionHandler(e);
		}
		return result;
	}
	
	
	@PostMapping("/documentSearch")
	public List<DocumentLink> documentSearch(HttpServletRequest httpRequest,@RequestParam("claimNumber") String claimNumber,@RequestParam("propertyData") String propertyData) throws Exception {
		List<DocumentLink> documentLinkList = new ArrayList<DocumentLink>();
		try {
			documentLinkList = caseCreationService.documentSearch(httpRequest,claimNumber,propertyData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception(e.getMessage());
			//return exceptionHandler(e);
		}
		return documentLinkList;
	}
	
	@PostMapping("/search")
	public List<Map<String,String>> search(HttpServletRequest httpRequest,
			@RequestParam("claimNumber") String claimNumber, @RequestParam("searchAction") String actionTaken,@RequestParam("propertyData") String propertyData) throws Exception {
		List<Map<String,String>> propertyValuesMap = new ArrayList<Map<String,String>>();
		try {
			System.out.println(actionTaken);
			propertyValuesMap = caseCreationService.search(httpRequest, claimNumber, actionTaken, propertyData);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception(e.getMessage());
			// return exceptionHandler(e);
		}
		return propertyValuesMap;
	}
	
	public String exceptionHandler(Exception e) {
		if(e.getMessage().contains(ApplicationConstants.userAuthenticateException) || e.getMessage().contains(ApplicationConstants.passwordEmptyException)){
			return "Error from service:You don't have permission to do this action";
		}
		else {
			return "Error from service:Something went wrong, Please try again later..!!";
		}

	}
}
