package retweeter;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import oauth.signpost.basic.DefaultOAuthProvider;
import winterwell.jtwitter.OAuthSignpostClient;

public class TwitterLogin extends OAuthSignpostClient implements Serializable {
	private static final long serialVersionUID = -486832029681039286L;
	
	public static final String CONSUMER_KEY = "AWlPsdJBGGymx4mY2JcEg";
	public static final String CONSUMER_SECRET = "3lI3YjG16Z21wM2EcugboPxOVr4tiABcdbg6MA4";
	
	final DefaultOAuthProvider provider = new DefaultOAuthProvider(
            "http://twitter.com/oauth/request_token",
            "http://twitter.com/oauth/access_token",
            "http://twitter.com/oauth/authorize");
	
    public void completeAuth(String verificationCode) {
    	this.setAuthorizationCode(verificationCode);
    }
    
	private static String getCallbackUrl(HttpServletRequest req) {
		String returnTo = "http://"+req.getServerName()+"/oauth-callback";
		return returnTo;
	}

	public static TwitterLogin get(HttpServletRequest req) {
		HttpSession session = req.getSession();
		TwitterLogin client = (TwitterLogin) session.getAttribute("oauth_client");
		return client;
	}
	public TwitterLogin(HttpServletRequest req) {
		super(CONSUMER_KEY, CONSUMER_SECRET, getCallbackUrl(req));
		HttpSession session = req.getSession();
		session.setAttribute("oauth_client", this);
	}

	public TwitterLogin() {
		super(CONSUMER_KEY, CONSUMER_SECRET, "http://retweetster.appspot.com/oauth-callback");
	}
	public TwitterLogin(User u) {
		super(CONSUMER_KEY, CONSUMER_SECRET, u.getUserOAuthToken(), u.getUserOAuthSecret());
	}

	public String getToken() {
		return getAccessToken()[0];
	}

	public String getTokenSecret() {
		return getAccessToken()[1];
	}

	public OAuthSignpostClient client() {
		return this;
	}

	public static void requestLogin(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		TwitterLogin twitterLogin = get(req);
		if(twitterLogin == null) twitterLogin = new TwitterLogin(req);
		resp.sendRedirect(twitterLogin.authorizeUrl().toString());
	}
}
