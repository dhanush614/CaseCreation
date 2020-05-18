package com.poc.chatbot.CaseCreation.CMConnection;

import javax.security.auth.Subject;

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

@Component
public class CaseManagerConnection {

	public static ObjectStore getConnection() {
		String uri = "http://wf-dc-poc.ibm.edu:9080/wsi/FNCEWS40MTOM/";
		String username = "admin";
		String password = "passw0rd";
		String TOS = "tos";
		UserContext old = null;
		CaseMgmtContext oldCmc = null;
		ObjectStore targetOS = null;
		try {
			Connection conn = Factory.Connection.getConnection(uri);
			Subject subject = UserContext.createSubject(conn, username, password, "FileNetP8WSI");
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