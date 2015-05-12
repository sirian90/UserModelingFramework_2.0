package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

public class LoadProperties {
	
	public static String getString(String str)  {
		Properties prop = new Properties ();
		InputStream is = null;
		
		try {
		is = new FileInputStream ("conf/prop.properties");
//		is = new LoadProperties().getClass().getClassLoader().getResourceAsStream("prop.properties");
		prop.load (is);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String str_ret = prop.getProperty(str);

		return str_ret;
	
	}
	
	
	public static String getCommitFrequency() {
		return getString("commit_frequency");
	}

	public static String getNomeUtente() {
		return getString("nome_utente");
	}

	public static String getPassword() {
		return getString("password");
	}

	public static String getUrl() {
		return getString("url_db");

	}

	public static String getNomeDb() {
		return getString("nome_db");
	}

	public static String getConsumerKey() {
		return getString("consumer_key");
	}

	public static String getConsumerSecret() {
		return getString("consumer_secret");
	}

	public static String getAccessToken() {
		return getString("access_token");
	}

	public static String getTokenSecret() {
		return getString("token_secret");
	}

	public static String getIndex_dir() {
		return getString("index_directory");
	}
	
	public static String getIndex_Filtrato() {
		return getString("index_filtrated");
	}
	
	public static String getProjectPath() {
		return getString("project_Path");
	}
	
	public static String getTaggerPath() {
		return getString("PosTagger_Path");
	}
	
	public static String getEnglishDictionary() {
		return getString("englishDictionary");
	}
	
	public static String getSubjCluesDictionary() {
		return getString("subjClues");
	}
	
	public static String getLIWCDictionary() {
		return getString("LIWCdict");
	}
	
	public static String getLIWCCategories() {
		return getString("LIWCcategories");
	}
	
	public static List<String> getFilterList() {
		List<String> listaFilter = new LinkedList<String>();
		String lista = getString("filterList");
		StringTokenizer tokenizer = new StringTokenizer(lista);
		
		while(tokenizer.hasMoreTokens()){
			listaFilter.add(tokenizer.nextToken());
		}
		return listaFilter;
	}
	
	public static List<String> getVectorList() {
		List<String> listaFilter = new LinkedList<String>();
		String lista = getString("vectorList");
		StringTokenizer tokenizer = new StringTokenizer(lista);
		
		while(tokenizer.hasMoreTokens()){
			listaFilter.add(tokenizer.nextToken());
		}
		return listaFilter;
	}

	public static String getStopword() {
		return getString("stopWord");
	}
	

}
