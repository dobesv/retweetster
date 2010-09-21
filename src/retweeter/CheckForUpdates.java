package retweeter;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.datanucleus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import winterwell.jtwitter.TwitterList;
import winterwell.jtwitter.Twitter.Status;
import winterwell.jtwitter.TwitterException.E401;
import winterwell.jtwitter.TwitterException.RateLimit;
import winterwell.jtwitter.TwitterException.Repetition;

@SuppressWarnings("serial")
public class CheckForUpdates extends HttpServlet {
	final Logger logger = LoggerFactory.getLogger(getClass());
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		// 1. Iterate over the users in the DB
		// 2. Keep the screen name up-to-date for each user in case they changed it in twitter
		// 3. Do searches for things to re-tweet.
		
		PersistenceManager pm = DB.getPersistenceManager();
		try {
			Extent<User> extent = pm.getExtent(User.class, false);
			for(User u : extent) {
				if(u.getAccountsToWatch().isEmpty()) {
					logger.info("Account "+u.getScreenName()+" has no accounts to watch set.  Skipping.");
				} else if(u.getHashTagsToWatch().isEmpty()) {
					logger.info("Account "+u.getScreenName()+" has no hash tags to watch set.  Skipping.");
				} else if(StringUtils.isEmpty(u.getUserOAuthSecret()) || StringUtils.isEmpty(u.getUserOAuthToken())) {
					logger.info("Account "+u.getScreenName()+" has no valid OAuth token.  Skipping.");
				}
				try {
					Twitter t = new Twitter(u.getScreenName(), new TwitterLogin(u));
					HashSet<Long> retweetedAlready = new HashSet<Long>();
					for(String accountToWatch : expandLists(u.getAccountsToWatch(), t)) {
						if(StringUtils.isEmpty(accountToWatch))
							continue;
						for(String keyword : u.getHashTagsToWatch()) {
							if(StringUtils.isEmpty(keyword))
								continue;
							String query = keyword+" from:"+accountToWatch;
							Long lastStatusId = u.getLastStatusId(query);
							t.setSinceId(lastStatusId);
							t.setMaxResults(5); // Don't re-tweet more than 5 for a given combination
							logger.info(u.getScreenName()+": searching for '"+query+"' since ID "+lastStatusId);
							for(Status status : t.search(query, null, 5)) {
								// If the user put multiple hashtags that we are watching, only retweet the status once
								if(retweetedAlready.add(status.getId())) {
									// Re-tweet it!
									try {
										Status retweet = t.retweet(status);
										logger.info(u.getScreenName()+": RT @"+u.getScreenName()+" "+status+"  - http://twitter.com/"+u.getScreenName()+"/statuses/"+retweet.getId());
									} catch(Repetition re) {
										// Already tweeted, move along to the next one then
									}
								}
								
								// Don't update the saved last status unless the retweet is successful. 
								if(lastStatusId == null || status.getId() > lastStatusId.longValue()) {
									u.setLastStatusId(query, status.getId());
								}
							}
						}
					}
					logger.info("Successfully processed searches for @"+u.getScreenName());
				} catch(RateLimit rateLimit) {
					logger.warn("Hit rate limit when looking for tweets for "+u.getScreenName()+"; have to try again later.");
				} catch(E401 authError) {
					logger.warn("Authentication failed for "+u.getScreenName()+" - user with have to re-authenticate to activate the service again.");
					u.setUserOAuthSecret(null);
					u.setUserOAuthToken(null);
				} catch(TwitterException te) {
					logger.error("Error doing re-tweets for "+u.getScreenName(), te);
				}
				logger.info("Successfully processed all accounts");
			}
		} finally {
			pm.close();
		}
	}

	private Collection<String> expandLists(Collection<String> accountsToWatch, Twitter t) {
		TreeSet<String> newAccountsToWatch = new TreeSet<String>();
		for(String account : accountsToWatch) {
			String[] parts = account.split("/", 2);
			if(parts.length == 2) {
				TwitterList list = new TwitterList(parts[0], parts[1], t);
				for(Twitter.User member : list) {
					newAccountsToWatch.add(member.getScreenName());
				}
			} else {
				newAccountsToWatch.add(account);
			}
		}
		return newAccountsToWatch;
	}
}
