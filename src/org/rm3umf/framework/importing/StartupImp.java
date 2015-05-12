package org.rm3umf.framework.importing;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.rm3umf.net.downloader.QueueException;
import org.rm3umf.persistenza.PersistenceException;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import util.LoadProperties;


public class StartupImp {
	
	public static void main(String[] args) throws DatasetException, PersistenceException, QueueException, IOException, InterruptedException, TwitterException{

		Logger root = Logger.getRootLogger();
		BasicConfigurator.configure();
		root.setLevel(Level.INFO);
		Importer importing = new Importer();
		//System.out.println(java.lang.Runtime.getRuntime().maxMemory()); 
		importing.start();
	}

}
