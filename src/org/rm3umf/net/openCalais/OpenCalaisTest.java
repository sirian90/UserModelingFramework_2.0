package org.rm3umf.net.openCalais;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import mx.bigdata.jcalais.CalaisClient;
import mx.bigdata.jcalais.CalaisObject;
import mx.bigdata.jcalais.CalaisResponse;
import mx.bigdata.jcalais.rest.CalaisRestClient;

/**
 * Scoperte tante cose interessati . attraverso il metodo getfield posso trovate tutti i tupi di richiesta che posso
 * fare a OpenCalais .Interessati:relevance, _typeGroup _type name persontype nationality commonname _typeReference instances relevance _uri
 * vedere un po tutte.
 * @author Giulz
 *
 */


public class OpenCalaisTest {
	
	 public static void main(String[] args) throws IOException{
	 CalaisClient client = new CalaisRestClient("za3j772ehvq3avmxdmjn5cds");
//	 CalaisResponse response = client.analyze("Kanye West should of jumped on stage when they said Beaver won.");
String[] array={
"Berlusconi e la Ferrari sono come Mulino Bianco. Ahaha!"};
for(String s:array){
    System.out.println(s);
	CalaisResponse response = client.analyze(s);
	 
	 for (CalaisObject entity : response.getEntities()) {
	      System.out.println(entity.getField("_type") + ":"+ entity.getField("name")+" "+entity.getField("_uri"));
	                       
	      Iterable<String> iter=entity.getFieldNames();
	      for(String ss :iter)
	    	  System.out.print(" "+ss);
	      System.out.println();
	      
	    }
	 
}
//	 
	 
	 for (CalaisObject topic : response.getTopics()) {
	      System.out.println(topic.getField("categoryName"));
	    }
//	 
//	 for (CalaisObject tags : response.getSocialTags()){
//	      System.out.println(tags.getField("_typeGroup") + ":" 
//	                         + tags.getField("name"));
//	    }

	 }
}