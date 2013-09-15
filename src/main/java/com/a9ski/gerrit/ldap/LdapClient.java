package com.a9ski.gerrit.ldap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.a9ski.gerrit.model.User;

public class LdapClient {
	
	private static final Logger logger = LoggerFactory.getLogger(LdapClient.class);

	private LdapConnection connection;
	
	public LdapClient() {
	}
	
	public synchronized void connect() throws LdapException {
		if (connection != null || connection.isConnected()) {
			disconnect();
		}
		
		connection = new LdapNetworkConnection( "localhost", 389 );
		connection.bind();
		
		
		// Don't do that ! Password in clear text = danger !
		connection.bind( "ou=example, dc=com", "secret" );

		// The password is encrypted, but it does not protect against a MITM attack
		connection.bind( "ou=example, dc=com", "{crypt}wSiewPyxdEC2c" );
	}
	
	public void disconnect() {
		if (connection != null) {
			synchronized(this) {
				try {
					connection.unBind();
				} catch(final LdapException ex) {
					logger.error("Error disconnecting LDAP client", ex);
				}
				
				try {
					connection.close();
				} catch (final IOException ex) {
					logger.error("Error disconnecting LDAP client", ex);				
				}
				connection = null;
			}
		}
	}
	
	private synchronized void establishConnection() throws LdapException {
		if (connection == null || !connection.isConnected()) {
			connect();
		}
	}
	
	public synchronized List<User> getUsers() throws LdapException, CursorException {
		establishConnection();
		final EntryCursor cursor = connection.search( "ou=system", "(objectclass=*)", SearchScope.ONELEVEL, "*" );
		
		final List<User> users = new ArrayList<User>();
		
		while ( cursor.next() ) {
		    final Entry entry = cursor.get();
		    entry.get("ou");
		    // TODO: Process the entry
		}
		
		return users;
	}
}
