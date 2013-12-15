package at.ac.tuwien.mnsa.ue1.nokiaterminalprovider;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import at.ac.tuwien.mnsa.ue1.protocol.SerialPacket;

public class NokiaProviderTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
//		SerialPacket sp = new SerialPacket(SerialPacket.TYPE_WAIT, (byte) 0x00, (short) 300, new byte[] {0x11, 0x22});
		
		int l = 0x000fffff;
		
		byte lnh = (byte) ((l & 0x0000ff00) >> 8);
		byte lnl = (byte) (l & 0x000000ff);
		
		System.out.println("As int: " + l);
		System.out.println(String.format("As int: 0x%X", l ));
		System.out.println("As unsigned short: " + (l & 0x0000ffff));
		System.out.println(String.format("High byte of unsigned short: 0x%X", lnh));
		System.out.println(String.format("Low byte of unsigned short: 0x%X", lnl));
		System.out.println(String.format("Reassembled LNH: 0x%X", (int) ((lnh << 8) & 0x0000ff00)));
		System.out.println(String.format("Reassembled LNH: 0x%X", (int) (lnh & 0x000000ff) << 8 ));
		System.out.println(String.format("Reassembled LNL: 0x%X", (int) (lnl) & 0x000000ff));
		System.out.println(String.format("Reassembled LNL: %d", lnl & 0x000000ff));
		System.out.println(String.format("LNL: %d", lnl));
		System.out.println(String.format("LNL as int: %d", (int) lnl));
		System.out.println(String.format("Reassembled to unsigned short: 0x%X", SerialPacket.getIntFromUnsignedShortBytes(lnh, lnl)));
		System.out.println(String.format("Reassembled to unsigned short: %d", SerialPacket.getIntFromUnsignedShortBytes(lnh, lnl)));
//		System.out.println((short) 0x8000);
//		fail("Not yet implemented");
	}

}
