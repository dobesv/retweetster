<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="retweeter.DB" %>
<%@page import="com.google.appengine.api.datastore.Key"%>
<%@page import="javax.jdo.PersistenceManager"%>
<%@page import="retweeter.User"%>
<%@page import="retweeter.SetupAction"%>
<%@page import="org.apache.commons.lang.StringUtils"%><html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>ReTweetster</title>
  </head>

  <body>
    <h1>ReTweetster</h1>
    
    <p>
    	This app observes the tweets for a given list of users and re-tweets and of their
    	updates which contain any hashtag from a list of hash tags.
    </p>
    <p>
    	This can be useful for a company/team twitter account where you'd like to make it easy
    	for team members to put content onto the main twitter - just add them to the list and
    	tell them to use the appropriate hash tag.
    </p>
    <%
	Key userKey = (Key) request.getSession().getAttribute("user");
	PersistenceManager pm = DB.getPersistenceManager();
	try {
		User user = null;
		try {
			if(userKey!=null) user = pm.getObjectById(User.class, userKey); 
		} catch(Exception e) {
			// Oh well... probably not in the DB any more.
		}
		if(user == null) {
			%>
			<a href="/setup">Click here to login to twitter and get started!</a>
			<%
		} else { %>
    <p>
    	Welcome <a target="twitter" href="http://twitter.com/<%= user.getScreenName() %>">@<%= user.getScreenName() %></a>!  (<a href="/setup?changeAccount=true">change account</a>)
    </p>
    <form action="/setup" method="post">
    	Enter the twitter user names OR <span title="Specify a list like You/retweetster with the people to monitor everyone on that list">lists</apsn> you wish to monitor:<br/>
    	<textarea id="accountsToWatch" name="accountsToWatch" rows="5" cols="30"><%=StringUtils.join(user.getAccountsToWatch(), "\n")%></textarea><br/>
    	
    	Enter the #hashTags or keywords to look for:<br/>
    	<textarea id="hashTagsToWatch" name="hashTagsToWatch" rows="5" cols="30"><%=StringUtils.join(user.getHashTagsToWatch(), "\n")%></textarea><br/>
    	
    	<button type="submit">Save</button> <br/>
    </form>
    <% } 
    } finally { pm.close(); }%>
  </body>
</html>
