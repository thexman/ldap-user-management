package com.a9ski.gerrit.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class CryptoUtilsTest {

	@Test
	public void testEncryptPassword() {		
		assertEquals("{SHA}FritstuVaHl3Cyrw9Y5hhEj/Eg0=", CryptoUtils.encryptPassword("alabalanica"));
		assertEquals("{SHA}aMpzJuIzzBmDU7ISZAL6ybBPzA4=", CryptoUtils.encryptPassword("алабаланица"));
		
	}

}
