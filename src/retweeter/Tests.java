package retweeter;

import static org.junit.Assert.*;

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
	
	@Test
	public void testSplit() throws Exception {
		
		assertEquals("foo", "\r\nfoo\r\nbar\r\nbaz".trim().split("\\W+")[0]);
		assertEquals("bar", "\r\nfoo\r\nbar\r\nbaz".trim().split("\\W+")[1]);
		assertEquals("baz", "\r\nfoo\r\nbar\r\nbaz".trim().split("\\W+")[2]);
		assertEquals(3, "\r\nfoo\r\nbar\r\nbaz\r\n\t".trim().split("\\W+").length);
		
		assertEquals("foo/bar", "foo/bar buzz".split("[^\\w/]+")[0]);
		assertEquals("buzz", "foo/bar buzz".split("[^\\w/]+")[1]);
		
	}
}
