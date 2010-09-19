package retweeter;

import java.io.IOException;
import java.util.Arrays;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.lang.StringUtils;

import com.google.appengine.api.datastore.Key;

@SuppressWarnings("serial")
public class SetupAction extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Key userKey = (Key) req.getSession().getAttribute("user");
		PersistenceManager pm = DB.getPersistenceManager();
		try {
			User user = userKey==null ? null : pm.getObjectById(User.class, userKey); 
			if(user == null || req.getParameter("changeAccount") != null) {
				TwitterLogin.requestLogin(req, resp);
				return;
			}
			
			maybeUpdateAccountsToWatch(user, req.getParameter("accountsToWatch"));
			maybeUpdateHashTagsToWatch(user, req.getParameter("hashTagsToWatch"));
			
			// Go back to the setup form
			resp.sendRedirect("/");
		} finally {
			pm.close();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
	
	private void maybeUpdateHashTagsToWatch(User user, String hashTagsToWatch) {
		if(hashTagsToWatch != null && !hashTagsToWatch.equals(StringUtils.join(user.getHashTagsToWatch(), ", "))) {
			user.setHashTagsToWatch(Arrays.asList(hashTagsToWatch.split("\\W+")));
		}
	}

	private void maybeUpdateAccountsToWatch(User user, String accountsToWatch) {
		if(accountsToWatch != null && !accountsToWatch.equals(StringUtils.join(user.getAccountsToWatch(), ", "))) {
			user.setAccountsToWatch(Arrays.asList(accountsToWatch.split("\\W+")));
		}
	}
}
