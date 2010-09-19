package retweeter;

import java.io.IOException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class CheckForUpdates extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		// 1. Iterate over the users in the DB
		// 2. Keep the screen name up-to-date for each user in case they changed it in twitter
		// 3. Do searches for things to re-tweet.
		
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world");
	}
}
