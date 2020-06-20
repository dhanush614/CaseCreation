package com.poc.chatbot.CaseCreation.PropertyReader;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class PropertyFileReader {
	
	public Properties getProperties(){
		Resource resource = new FileSystemResource("C:\\PropertyFiles\\sample.properties");
		Properties props = null;
		try {
			props = PropertiesLoaderUtils.loadProperties(resource);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return props;
	}

}
