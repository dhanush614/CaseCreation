package com.poc.chatbot.CaseCreation.CMConnection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.filenet.api.constants.ClassNames;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.util.UserContext;
import com.ibm.casemgmt.api.context.CaseMgmtContext;
import com.ibm.casemgmt.api.context.SimpleP8ConnectionCache;
import com.ibm.casemgmt.api.context.SimpleVWSessionCache;
import com.poc.chatbot.CaseCreation.Constants.ApplicationConstants;
import com.poc.chatbot.CaseCreation.PropertyReader.PropertyFileReader;

//@PropertySource("file:C:/PropertyFiles/sample.properties")
@Component
public class CaseManagerConnection {
	

	public ObjectStore getConnection(HttpServletRequest httpRequest, JsonNode jsonNode) throws Exception {
		String authorization = httpRequest.getHeader("Authorization");
		String username="";
		String password="";
		System.out.println("Authorization ::"+authorization);

		 if (!authorization.equals("null") && !authorization.isEmpty()) {

		    // Authorization: Basic base64credentials

		    byte[] credDecoded = Base64.getDecoder().decode(authorization);

		    String credentials = new String(credDecoded, StandardCharsets.UTF_8);

		    // credentials = username:password

		    final String[] values = credentials.split(":", 2);
		    
		    username=values[0];
		    password=values[1];

		    System.out.println("User name ::"+username+" Password ::"+password);

		}
		
		String uri = jsonNode.get(ApplicationConstants.uri).asText();
		String TOS = jsonNode.get(ApplicationConstants.tos).asText();
		UserContext old = null;
		CaseMgmtContext oldCmc = null;
		ObjectStore targetOS = null;
		try {
			Connection conn = Factory.Connection.getConnection(uri);
			Subject subject = UserContext.createSubject(conn, username, password, ApplicationConstants.jaasStanzaName);
			UserContext.get().pushSubject(subject);
			Domain domain = Factory.Domain.fetchInstance(conn, null, null);
			targetOS = (ObjectStore) domain.fetchObject(ClassNames.OBJECT_STORE, TOS, null);
			SimpleVWSessionCache vwSessCache = new SimpleVWSessionCache();
			CaseMgmtContext cmc = new CaseMgmtContext(vwSessCache, new SimpleP8ConnectionCache());
			oldCmc = CaseMgmtContext.set(cmc);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
			
		} finally {
			if (oldCmc != null) {
				CaseMgmtContext.set(oldCmc);
			}
			if (old != null) {
				UserContext.set(old);
			}
		}
		return targetOS;

	}
}