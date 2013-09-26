package com.a9ski.gerrit.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.directory.api.util.Base64;

import com.a9ski.gerrit.exceptions.PluginRuntimeException;

public class CryptoUtils {

	private CryptoUtils() {
		// TODO Auto-generated constructor stub
	}
	
	public static String encryptPassword(String clearTextPassword) {		
	    try {
	    	final MessageDigest md = MessageDigest.getInstance("SHA-1");
	    	final byte[] sha = md.digest(clearTextPassword.getBytes("UTF-8"));
	    	final String shaPassword = "{SHA}" + new String(Base64.encode(sha));
	    	return shaPassword;
	    } catch(final NoSuchAlgorithmException ex) {
	        throw new PluginRuntimeException("Cannot encrypt password", ex);
	    } catch (final UnsupportedEncodingException ex) {
	    	throw new PluginRuntimeException("Cannot encrypt password", ex);
		} 
	}	
}
