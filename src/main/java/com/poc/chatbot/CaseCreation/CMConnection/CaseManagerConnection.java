package com.poc.chatbot.CaseCreation.CMConnection;

import java.io.IOException;

import javax.security.auth.Subject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

@Component
public class CaseManagerConnection {
	
	@Autowired
	PropertyFileReader propertyFileReader;

	public ObjectStore getConnection() throws IOException {
		String uri = propertyFileReader.getProperties().getProperty(ApplicationConstants.prefix+"."+ApplicationConstants.uri);
		String username = propertyFileReader.getProperties().getProperty(ApplicationConstants.prefix+"."+ApplicationConstants.username);
		String password = propertyFileReader.getProperties().getProperty(ApplicationConstants.prefix+"."+ApplicationConstants.password);
		String TOS = propertyFileReader.getProperties().getProperty(ApplicationConstants.prefix+"."+ApplicationConstants.tos);
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
			System.out.println(e);
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