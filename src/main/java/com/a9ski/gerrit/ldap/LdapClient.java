package com.a9ski.gerrit.ldap;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.a9ski.gerrit.exceptions.PluginException;
import com.a9ski.gerrit.ldap.exceptions.InvalidPasswordException;
import com.a9ski.gerrit.ldap.exceptions.UserNotFoundException;
import com.a9ski.gerrit.model.User;
import com.a9ski.gerrit.utils.CryptoUtils;

public class LdapClient {
	
	private static final Logger logger = LoggerFactory.getLogger(LdapClient.class);

	private LdapConnection connection;
	
	private final String host;
	private final int port;
	
	private final String login;
	private final String password;
	
	private final String accountBase;	
	private final String groupBase;
	
	public LdapClient(final URI uri, final String login, final String password, final String accountBase, final String groupBase) {
		if (uri.getHost() != null) {
			this.host = uri.getHost();
		} else {
			this.host = "localhost";
		}
		if (uri.getPort() != -1) {
			this.port = uri.getPort();
		} else {
			this.port = 389;
		}
		this.login = login;
		this.password = password;
		this.accountBase = accountBase;
		this.groupBase = groupBase;		
		
		if (logger.isDebugEnabled()) {
			logger.debug("LDAP Host:" + host);
			logger.debug("LDAP Port:" + port);
			logger.debug("LDAP Login:" + login);			
			logger.debug("LDAP Account base:" + accountBase);
			logger.debug("LDAP Group base:" + groupBase);
		}		
	}
	
	public synchronized void connect() throws LdapException {
		if (connection != null && connection.isConnected()) {
			disconnect();
		}
		
		connection = new LdapNetworkConnection(host, port);
		// Password in clear text = danger !
		connection.bind(login, password);
		
		// The password is encrypted, but it does not protect against a MITM attack
		// connection.bind( "ou=example, dc=com", "{crypt}wSiewPyxdEC2c" );
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
	
	private String getAttr(Entry entry, String key, String defaultValue) {
		final Attribute attr = entry.get(key);
		if (attr != null) {
			try {
				return attr.getString();
			} catch (final LdapInvalidAttributeValueException ex) {
				logger.error(String.format("Cannot get attribute '%s' of entry '%s'", key, entry.getDn()), ex);
			}
		}
		return defaultValue;
	}
	
	public synchronized List<User> getUsers() throws LdapException, CursorException {
		establishConnection();
		try {
			//final EntryCursor cursor = connection.search( "ou=system", "(objectclass=*)", SearchScope.ONELEVEL, "*" );
			final EntryCursor cursor = connection.search( accountBase, "(objectclass=*)", SearchScope.ONELEVEL, "*" );
			
			final List<User> users = new ArrayList<User>();
			
			while ( cursor.next() ) {
			    final Entry entry = cursor.get();
	//		    if (entry.hasObjectClass("person")) {
	//		    	
	//		    }
			    final String id = getAttr(entry, "uid", "");
			    final String firstName = getAttr(entry, "givenname", "");
			    final String lastName = getAttr(entry, "sn", "");
			    final String email = getAttr(entry, "mail", "");
			    final String password = getAttr(entry, "userpassword", "");
			    final User user = new User(id, firstName, lastName, email, password);
			    users.add(user);
			}
			
			return users;
		} finally {
			disconnect();
		}
	}

	public void forceChangePassword(String userId, String newPassword) throws LdapException, CursorException {
		establishConnection();
		try {
			final String filter = String.format("(uid=%s)", userId);
			final EntryCursor cursor = connection.search( accountBase, filter, SearchScope.ONELEVEL, "*" );
			if(cursor.next()) {
				final Entry entry = cursor.get();
				entry.put("userpassword", newPassword);
				connection.modify(entry, ModificationOperation.REPLACE_ATTRIBUTE);				
			}		
		} finally {
			disconnect();
		}				
	}
	
	public void changePassword(String userId, String oldPassword, String newPassword) throws LdapException, CursorException, PluginException{
		establishConnection();
		try {
			final String filter = String.format("(uid=%s)", userId);
			final EntryCursor cursor = connection.search( accountBase, filter, SearchScope.ONELEVEL, "*" );
			if(cursor.next()) {
				final Entry entry = cursor.get();
				if (!oldPassword.equals(getAttr(entry, "userpassword", ""))) {
					throw new InvalidPasswordException("Old password doesn't match");
				}
				entry.put("userpassword", newPassword);
				connection.modify(entry, ModificationOperation.REPLACE_ATTRIBUTE);				
			} else {
				throw new UserNotFoundException(String.format("User with ID '%s' not found. Filter is [%s]", userId, filter));
			}
		} finally {
			disconnect();
		}				
	}
}
