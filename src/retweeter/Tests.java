package retweeter;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

public class Tests {

	@Test
	public void testSerializable() throws Exception {
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bytesOut);
		TwitterLogin client = new TwitterLogin();
		client.authorizeUrl();
		oos.writeObject(client);
		oos.close();
		ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytesOut.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bytesIn);
		TwitterLogin client2 = (TwitterLogin) ois.readObject();
		assertNotNull(client2);
		client2.setAuthorizationCode("ABC");
	}
}
