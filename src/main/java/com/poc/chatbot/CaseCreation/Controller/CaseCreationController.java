package com.poc.chatbot.CaseCreation.Controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.poc.chatbot.CaseCreation.Service.CaseCreationService;

@RestController
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:3001","https://cm-assistant-bot.eu-gb.mybluemix.net/"})
public class CaseCreationController {

	@Autowired
	CaseCreationService caseCreationService;

	@GetMapping("/create")
	public String caseCreation(@RequestParam("claimNumber") String claimNumber) {
		String caseId = caseCreationService.createCase(claimNumber);
		return caseId;
	}
	
	@GetMapping("/validate")
	public String validateClaim(@RequestParam("claimNumber") String claimNumber) {
		String validateFlag = caseCreationService.validateClaim(claimNumber);
		return validateFlag;
	}

	@PostMapping("/upload")
	@ResponseBody
	public String uploadDocument(@RequestParam("uploadFile") MultipartFile file, @RequestParam("claimNumber") String claimNumber,@RequestParam("fileName") String fileName) {
		String result = null;
		try {
			result = caseCreationService.uploadFile(file, claimNumber,fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
