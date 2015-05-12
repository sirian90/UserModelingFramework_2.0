package org.rm3umf.framework.importing;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rm3umf.net.twitter.FacadeTwitter4j;

import twitter4j.IDs;
import twitter4j.TwitterException;

/**
 * Questa classe ha la responsabilità di arricchire il modello interrogando Twitter ottenendo le
 * informazioni aggiuntive
 * @author giulz
 *
 */
public class SocialEnricher {
	
	
	/**
	 * Questo metodo interroga Twitter chiedendogli i followers dell'utente identificato dall'id
	 * se il profilo è privato ritornerrà null
	 * @param userid
	 * @return listaFollower
	 * @throws IOException 
	 */
	
	public Set<Long> getFollower(long userid) throws IOException{
		Set<Long> listaFollower = new HashSet<Long>();
		
		try{
			
			IDs ids;
				ids = FacadeTwitter4j.getInstace().getFollowers(userid, -1);
				
				if (ids == null)
					return null;
				
				long[] arrayFollowersId = ids.getIDs();
				
				for (int i=0; i<arrayFollowersId.length; i++){
					listaFollower.add(arrayFollowersId[i]);
				}
			
		}catch(TwitterException e){
			System.err.println("impossibile recuperare di follower di "+userid);
			
		}
		return listaFollower;
	}
	
	

	/**
	 * Questo metodo interroga Twitter chiedendogli i followers dell'utente identificato dall'id
	 * se il profilo è privato ritornerrà null
	 * @param userid
	 * @return listaFollower
	 * @throws InterruptedException 
	 */
	
	public Set<Long> getFollowed(long userid) throws InterruptedException, TwitterException{
		Set<Long> listaFriends = new HashSet<Long>();
		try {
			
		
		IDs ids;
		ids = FacadeTwitter4j.getInstace().getFriends(userid, -1);
		if (ids == null)
			return null;
		
		long[] arrayFriendsID = ids.getIDs();
		
		for (int i=0; i<arrayFriendsID.length; i++){
			listaFriends.add(arrayFriendsID[i]);
		}
		
		} catch (Exception e) {
			System.err.println("impossibile recuperare di follower di "+userid);
		}
		return listaFriends;
	}

	
	
		
	

	
	
	

}
