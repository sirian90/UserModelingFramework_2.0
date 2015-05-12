package org.rm3umf.net.twitter;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import twitter4j.IDs;
import twitter4j.Paging;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.internal.logging.Logger;
import util.LoadProperties;

/**
 * Questa classe permette di inoltrare delle richieste a Twitter per recuperare le informazioni relative agli
 * utenti di Twitter identificati dall'id. In particolare è possibile recuperare gli insiemi di follower e di 
 * followed(following) per un utente. 
 * Viene gestito il limite di richieste attendendo quando si raggiunge il limite.
 * 
 * @author giulz, diabolik84
 *
 */

public class FacadeTwitter4j {
	
	
	private final long serialVersionUID = 1L;
	private final String BadRequest = "400";
	private final String Unauthorized = "401";
	private final String Forbidden = "403";
	private final String NotFound = "404";
	private final String NotAccettable = "406";
	private final String CalmDown = "88";
	private final String ServerError = "500";
	private final String BadGatway = "502";
	private final String ServiceUnavailable = "503";
	
	private final String followersID = "/followers/ids";
	private final String friendsID = "/friends/ids";
	private final String rateLimitApp = "/application/rate_limit_status";
	private static final String OAUTH_KEY = LoadProperties.getConsumerKey();
	private static final String OAUTH_SECRET = LoadProperties.getConsumerSecret();
	private static final String ACCESS_TOKEN = LoadProperties.getAccessToken();
	private static final String TOKEN_SECRET = LoadProperties.getTokenSecret();
	
	int twitterUpgrade = 1; // minutes to sleep for twitter upgrade;
	int rateLimit = 15; // minutes to sleep for twitter rate limit;
	
	private static final Logger logger=Logger.getLogger(FacadeTwitter4j.class);
	
	private static FacadeTwitter4j instance;
	private int count;
	//private final int LIMIT=150;
	private Twitter twitter;
	private int secondsReset;
	
	public static FacadeTwitter4j getInstace() throws IOException, TwitterException{
		if(instance==null){
			instance=new FacadeTwitter4j();
		}
		return instance;
	}
	
	public FacadeTwitter4j() throws IOException, TwitterException{
		this.count=0;
		
		this.twitter = new TwitterFactory().getInstance();
		this.twitter.setOAuthConsumer(OAUTH_KEY, OAUTH_SECRET);
		AccessToken arg0 = new AccessToken(ACCESS_TOKEN, TOKEN_SECRET);
		this.twitter.setOAuthAccessToken(arg0);

	}
	
//	/**
//	 * Restituisce l'insieme di following dell'utente identificato da idUser
//	 * @param idUser
//	 * @return setFollowed - l'insieme di utenti di follower
//	 * @throws TwitterException
//	 */
//	public  Set<Long> getFolloweds(long idUser) throws TwitterException{
//		Set<Long> listaFriends=new HashSet<Long>();
//		long cursor = -1;
//		IDs ids;
//		do{ count++;
//			logger.info("("+this.count+")recupero followeds di user "+idUser);	
//			//verifico se ho ragiunto il limite
//			aspetta();
//			ids=twitter.getFriendsIDs(idUser,cursor);
//			for (long id : ids.getIDs()) {
//				listaFriends.add(id);
//			}
//		} while ((cursor = ids.getNextCursor()) != 0);
//		return listaFriends;
//	}
	
//	/**
//	 * Restituisce l'insieme di follower  dell'utente identificato da idUser
//	 * @param idUser
//	 * @return setFollower 
//	 * @throws TwitterException
//	 */
//	public  Set<Long> getFollowers(long idUser) throws TwitterException{
//		Set<Long> listaFriends=new HashSet<Long>();
//		long cursor = -1;
//		IDs ids;
//		do{ count++;
//			logger.info("("+this.count+")recupero followers di user "+idUser);
//			//verifico se ho ragiunto il limite
//			aspetta();
//			ids=twitter.getFollowersIDs(idUser,cursor);
//			for (long id : ids.getIDs()) {
//				listaFriends.add(id);
//			}
//		} while ((cursor = ids.getNextCursor()) != 0);
//		return listaFriends;
//	}
	
//	/**
//	 * Questo metodo è necessario per rispettare i limiti di richiesta da inoltrare alle
//	 * Twitter API
//	 * @throws TwitterException
//	 */
//	private void aspetta() throws TwitterException{
//		//verifico se mi devo fermare
//		try{
//			
//			int remainigHits=twitter.getRateLimitStatus().getRemainingHits();
//			logger.info("remaining hits:"+remainigHits);
//			
//			while(remainigHits==0){
//					logger.info("aspetto 1 minuto ");
//					Thread.sleep(60000);
//					remainigHits=twitter.getRateLimitStatus().getRemainingHits();
//					logger.info("remaining hits:"+remainigHits);
//				}
//				//this.count=0;
//			
//		}catch(InterruptedException e){
//			throw new TwitterException("errore durante l'attesa dell'ora");
//		}
//		
//	}
	
	
	public ResponseList<Status> getUserTimeline(String username,long lastTweetId) {
		System.out.print("Getting Timeline...");
		ResponseList <Status> st = null;
		int i=1;
		
		try {
			if (lastTweetId != 0){
			Paging p = new Paging();
			p.setCount(200);
			p.setSinceId(lastTweetId);
			st = twitter.getUserTimeline(username,p);
			}
			else {
				//GETTING ALL USER TIMELINE
				Paging p = new Paging();
				p.setCount(200);
				p.setPage(i);
				st = twitter.getUserTimeline(username,p);
				
				while(st != null && i<17){
				System.out.print(".");
				i++;
				p.setPage(i);
				ResponseList<Status> aa = twitter.getUserTimeline(username,p);
				if(aa != null && !aa.isEmpty())
				st.addAll(aa);
				else
					break;		
				}
			}
			
		} catch (Exception ex) {
			if (ex.getMessage().contains(Unauthorized) || ex.getMessage().contains(NotFound)) {
				System.out.println("401/404 ignored");
			}
			
			else if(ex.getMessage().contains(ServerError) || ex.getMessage().contains(BadGatway) || ex.getMessage().contains(ServiceUnavailable)){
				try {
					System.out.println("Server Unavailable or Twitter Upgrading.. Sleeping for some minutes and try again.." + new Date());
					Thread.sleep(1000 * 60 * twitterUpgrade);
					return getUserTimeline(username,lastTweetId);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}					
			}
			
			else if(ex.getMessage().contains(BadRequest) || ex.getMessage().contains(CalmDown)){
				try {
					System.out.println("Error / Rate limit reached, sleeping for pre-set time." + new Date() + " "
							+ ex.getMessage());
					Thread.sleep(1000 * 60 * rateLimit);
					return getUserTimeline(username,lastTweetId);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
	
			}
			
			else {
				System.out.println("Generic Error " + new Date() + ex.getMessage());
			}
		}
		return st;
	}
	
	
	public IDs getFriends(long id, long cursor) throws InterruptedException {
		IDs amici = null;

		int secondsReset = 0;
		try{
			System.out.println("GETTING FRIENDS...");
			RateLimitStatus status = getRateLimit(friendsID);
			if(status != null){
			System.out.println(" REMAINING HITS: " + status.getRemaining());
			secondsReset = status.getSecondsUntilReset();
			System.out.println("SECOND FOR RESET:"+secondsReset);
			}

			amici = twitter.getFriendsIDs(id,cursor);
		} 
		catch (Exception ex) {
			if (ex.getMessage().contains(Unauthorized) || ex.getMessage().contains(NotFound)) {
				System.out.println("401/404 ignored");
//				Thread.sleep(1000 * 30);
//				return getFriends(id,cursor);
				return null;
			}

				else if(ex.getMessage().contains(ServerError) || ex.getMessage().contains(BadGatway) || ex.getMessage().contains(ServiceUnavailable)){
					
						System.out.println("Server Unavailable or Twitter Upgrading.. Sleeping for some minutes and try again.." + new Date());
//						Thread.sleep(1000 * 60 * twitterUpgrade);
//						return getFriends(id,cursor);
						return null;
									
				}
				
				else if(ex.getMessage().contains(BadRequest) || ex.getMessage().contains(CalmDown)){
					try {
						System.out.println("Error / Rate limit reached, sleeping for pre-set time." + new Date() + " "
								+ ex.getMessage());
						secondsReset = (int) Math.pow(secondsReset, 2);
						secondsReset = (int) Math.sqrt(secondsReset);
						Thread.sleep(1000*(secondsReset));
						return getFriends(id,cursor);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
		
				}
				
				else {
					System.out.println("Generic Error " + new Date() + ex.getMessage());
					return null;
				}
			}	

		return amici;
	}
	
	
	RateLimitStatus getRateLimit(String endpoint){
		try {
		
		Map<String ,RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus();
        RateLimitStatus status = rateLimitStatus.get(endpoint);
           
        return status;
	}
	catch (Exception e) {
		System.err.println("Rate limit is not available at the moment..");
		return null;
	}

}
	
	public ResponseList<User> lookupUser(long[] utenti) {
		ResponseList<User> users = null;
		try {	
				users = twitter.lookupUsers(utenti);
		} catch (Exception ex) {
			if (ex.getMessage().contains(Unauthorized) || ex.getMessage().contains(NotFound)) {
				System.out.println("401/404 ignored");
			}
			
			else if(ex.getMessage().contains(ServerError) || ex.getMessage().contains(BadGatway) || ex.getMessage().contains(ServiceUnavailable)){
				try {
					System.out.println("Server Unavailable or Twitter Upgrading.. Sleeping for some minutes and try again.." + new Date());
					Thread.sleep(1000 * 60 * twitterUpgrade);
					return lookupUser(utenti);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}					
			}
			
			else if(ex.getMessage().contains(BadRequest) || ex.getMessage().contains(CalmDown)){
				try {
					System.out.println("Error / Rate limit reached, sleeping for pre-set time." + new Date() + " "
							+ ex.getMessage());
					Thread.sleep(1000 * 60 * rateLimit);
					return lookupUser(utenti);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
	
			}
			
			else {
				System.out.println("Generic Error " + new Date() + ex.getMessage());
			}
		}
		return users;		
	}
	
	
public IDs getFollowers(Long id_utente, long cursor) throws TwitterException {
		
    	
		IDs followers = null;
		try{
			System.out.println("GETTING FOLLOWERS...");
			RateLimitStatus status = getRateLimit(followersID);
			if(status != null){
			System.out.println(" REMAINING HITS: " + status.getRemaining());
			secondsReset = status.getSecondsUntilReset();
			}
			
			followers = twitter.getFollowersIDs(id_utente,cursor);				
		} catch (Exception ex) {
			if (ex.getMessage().contains(Unauthorized) || ex.getMessage().contains(NotFound)) {
				System.out.println("401/404 ignored");
				ex.printStackTrace();
				return null;
			}
			
			else if(ex.getMessage().contains(ServerError) || ex.getMessage().contains(BadGatway) || ex.getMessage().contains(ServiceUnavailable)){
					System.out.println("Server Unavailable or Twitter Upgrading.. Sleeping for some minutes and try again.." + new Date());
//					Thread.sleep(1000 * (secondsReset+5));
//					return getFollowers(id_utente,cursor);
					return null;
			}
			
			else if(ex.getMessage().contains(BadRequest) || ex.getMessage().contains(CalmDown)){
				try {
					System.out.println("Error / Rate limit reached, sleeping for pre-set time." + new Date() + " "
							+ ex.getMessage());
					Thread.sleep(1000 * 60 * rateLimit);
					return getFollowers(id_utente,cursor);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
	
			}
			
			else {
				System.out.println("Generic Error " + new Date() + ex.getMessage());
				return null;
			}
		}
		return followers;
	}
	
	 
}
