/**
 * 
 */
package jp.co.myself.twutils.test;

import static org.junit.Assert.*;
import jp.co.myself.twutils.RequestTokenGetter;

import org.junit.Test;

/**
 * テストクラスです。
 */
public class RequestTokenGetterTest {

	/**
	 * Test method for {@link jp.co.myself.twutils.RequestTokenGetter#proc(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testProc() {
		
		RequestTokenGetter.proc("sampleKey",
				"sampleSecret",
				"http://callback.com");
		
		fail("Not yet implemented");
	}

}
