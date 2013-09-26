package com.a9ski.gerrit.serlvets;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.eclipse.jgit.lib.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.a9ski.gerrit.exceptions.PluginException;
import com.a9ski.gerrit.exceptions.PluginRuntimeException;
import com.a9ski.gerrit.ldap.LdapClient;
import com.a9ski.gerrit.ldap.exceptions.InvalidPasswordException;
import com.a9ski.gerrit.model.User;
import com.a9ski.gerrit.model.UserSortComparator;
import com.a9ski.gerrit.utils.CryptoUtils;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.inject.Inject;
import com.google.inject.Singleton;
@Singleton
public class UserServiceServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1520121716712150812L;

	private static final Logger logger = LoggerFactory.getLogger(UserServiceServlet.class);
	
	private final Charset charset;
	
//	@Inject
//	private com.google.gerrit.server.config.PluginConfigFactory cfg;	
	
	private LdapClient ldap;
	
	
	@Inject
	public UserServiceServlet(@GerritServerConfig final Config config) {
		charset = Charset.forName("UTF-8");	
		final String accountBase = config.getString("ldap", null, "accountBase");
		final String groupBase = config.getString("ldap", null, "groupBase");
		final String server = config.getString("ldap", null, "server");
		final String login = config.getString("ldap", null, "username");
		final String password = config.getString("ldap", null, "password");
		
		try {
			ldap = new LdapClient(new URI(server), login, password, accountBase, groupBase);
		} catch (final URISyntaxException ex) {
			throw new PluginRuntimeException(String.format("Cannot connect to LDAP server %s", server), ex);
		}
	};
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handleRequest(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handleRequest(req, resp);
	}
	
	private List<User> getUsers() throws LdapException, CursorException {
		return ldap.getUsers();
//		final List<User> users = new ArrayList<User>();
//		for(int i = 0; i < 50; i++) {
//			String s = String.valueOf(i);
//			users.add(new User("id" + s, "John" + s, "Doe", "john" + s + "@email.rar", "xxx"));
//		}
//		return users;
	}
	
	private int parseIntParam(final HttpServletRequest request, final String paramName, final int defaultValue) {
		final String intParamStr = request.getParameter(paramName);		
		try {			
			return parseIntParam(intParamStr, defaultValue);
		} catch (final NumberFormatException ex) {
			logger.error(String.format("Invalid number %s for parameter", intParamStr, paramName), ex);
			return defaultValue;
		}
		
	}

	private int parseIntParam(final String strValue, final int defaultValue) {
		int intParam = defaultValue;
		if (strValue != null) {
			intParam = Integer.parseInt(strValue);
		}
		return intParam;
	}

	private void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {		
		// method could be null
		final String method = request.getParameter("method");
		
		if ("changePassword".equals(method)) {
			changePassword(request, response);
		} else {
			listUsers(request, response);
		}
	}


	private void changePassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String userId = request.getParameter("userId");		
		final String oldPassword = request.getParameter("oldPassword");
		final String newPassword = request.getParameter("newPassword");
		if (oldPassword == null) {
			writeResponse(response, createStatusJson(false, "Missing old password"));
		} else if (newPassword == null) {
			writeResponse(response, createStatusJson(false, "Missing new password"));
		} else if (newPassword.length() < 4) {
			writeResponse(response, createStatusJson(false, "New password is too short"));
		} else {
			changePassword(response, userId, CryptoUtils.encryptPassword(oldPassword), CryptoUtils.encryptPassword(newPassword));
		}
	}


	private void changePassword(HttpServletResponse response, final String userId, final String oldPassword, final String newPassword) throws IOException, ServletException {
		try {
			ldap.changePassword(userId, oldPassword, newPassword);
			writeResponse(response, createStatusJson(true, String.format("Successfully changes password for user '%s'", userId)));		
		} catch (final LdapException ex) {			
			throw new ServletException(String.format("Error changin password of users %s", userId), ex);
		} catch (final CursorException ex) {
			throw new ServletException(String.format("Error changin password of users %s", userId), ex);
		} catch (final PluginException ex) {
			writeResponse(response, createStatusJson(false, ex.getMessage()));
		}
	}
	
	private JsonObject createStatusJson(boolean success, String description) {
		final JsonObject json = new JsonObject();
		json.addProperty("success", success);
		json.addProperty("description", description);
		return json;
	}


	private void listUsers(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		try {
			int rowsPerPage = parseRowsPerPage(request);
			
			final List<User> users = getUsers();
			Collections.sort(users, new UserSortComparator());			
			final int pagesCount = users.size() / rowsPerPage + sgn(users.size() % rowsPerPage);
			final int page = parsePage(request, pagesCount);
			
			final JsonObject json = new JsonObject();
			json.addProperty("total", pagesCount); // total number of pages
			json.addProperty("records", users.size()); // total number of users
			json.addProperty("page", page); // current page
			
			final JsonArray rows = new JsonArray();
			
			int startI = (page - 1) * rowsPerPage;
			int endI = Math.min(startI + rowsPerPage, users.size());
			for(int i = startI; i < endI; i++) {			
				final JsonObject rowObj = toJson(users.get(i));
				rows.add(rowObj);
			}
			
			json.add("rows", rows);
			
			writeResponse(response, json);
		} catch (final LdapException ex) {			
			throw new ServletException("Error processing users list", ex);
		} catch (final CursorException ex) {
			throw new ServletException("Error processing users list", ex);
		}
	}

	private int sgn(int i) {
		if (i > 0) {
			return 1;
		} else if (i < 0) {
			return -1;
		} else {
			return 0;
		}
	}


	private int parseRowsPerPage(HttpServletRequest request) {
		int rowsPerPage = parseIntParam(request, "rows", 10);
		if (rowsPerPage < 1) {
			rowsPerPage = 10;
		}
		return rowsPerPage;
	}

	private void writeResponse(HttpServletResponse response, final JsonObject json) throws IOException {		
		response.setContentType("application/json");
		response.setCharacterEncoding(charset.name());
		
		boolean success = false;
		
		final OutputStream os = response.getOutputStream();
		try {
			final String data = json.toString();
			os.write(data.getBytes(charset));
			os.flush();
			success = true;
		} finally {
			if (!success) {
				IOUtils.closeQuietly(os);
			} else {
				os.close();
			}
		}
	}

	private int parsePage(HttpServletRequest request, int pagesCount) {
		int page = parseIntParam(request, "page", 1);		
		if (page > pagesCount) {
			if (pagesCount > 0) {
				page = pagesCount;
			} else {
				page = 1;
			}
		} else if (page < 1) {
			page = 1;
		}
		return page;
	}

	private JsonObject toJson(final User user) {
		final JsonObject rowObj = new JsonObject();
		rowObj.addProperty("id", user.getId());
		final JsonArray cells = new JsonArray();
		cells.add(new JsonPrimitive(user.getId()));
		cells.add(new JsonPrimitive(user.getFirstName()));
		cells.add(new JsonPrimitive(user.getLastName()));
		cells.add(new JsonPrimitive(user.getEmail()));
		cells.add(new JsonPrimitive(user.getId()));
		rowObj.add("cell", cells);
		return rowObj;
	}
	
}
