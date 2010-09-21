package retweeter;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterAccount;

@SuppressWarnings("serial")
public class OAuthCallback extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String verifier = req.getParameter("oauth_verifier");
		TwitterLogin w = TwitterLogin.get(req);
		if(w==null || verifier==null) {
			TwitterLogin.requestLogin(req, resp);
			return;
		}
		w.completeAuth(verifier);
		
		
		Twitter t = new Twitter(null, w);
		
		TwitterAccount account = new TwitterAccount(t);
		Twitter.User twitterUser = account.verifyCredentials();
		
		// Get their screen name and look them up that way
		PersistenceManager pm = DB.getPersistenceManager();
		try {
			User user = User.getOrCreateWithScreenName(pm, twitterUser.getScreenName());
			user.setUserOAuthToken(w.getToken());
			user.setUserOAuthSecret(w.getTokenSecret());
	
			HttpSession session = req.getSession();
			session.setAttribute("user", user.getKey());
			
			resp.sendRedirect("/");
		} finally {
			pm.close();
		}
	}
}
