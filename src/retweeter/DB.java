package retweeter;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

public class DB {
	static final PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("transactions-optional");

	public static PersistenceManager getPersistenceManager() {
		return pmf.getPersistenceManager();
	}

	public static String getServerTimeZoneID() {
		return pmf.getServerTimeZoneID();
	}
	
}
