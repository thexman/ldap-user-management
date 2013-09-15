package com.a9ski.gerrit.serlvets;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.a9ski.gerrit.ldap.LdapClient;
import com.a9ski.gerrit.model.User;
import com.a9ski.gerrit.model.UserComparator;
import com.google.gerrit.extensions.annotations.Export;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.inject.Singleton;

@Export("/userManagement")
@Singleton
public class UserServiceServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1520121716712150812L;

	private static final Logger logger = LoggerFactory.getLogger(UserServiceServlet.class);
	
	private final Charset charset;
	
	private LdapClient ldap = new LdapClient();
	
	public UserServiceServlet() {
		charset = Charset.forName("UTF-8");
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handleRequest(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handleRequest(req, resp);
	}
	
	private List<User> getUsers() {
		//return ldap.getUsers();
		return Arrays.asList(new User("1", "John", "Doe", "john@email.rar", "xxx"),
				new User("2", "John2", "Doe2", "john2@email.rar", "xxx"));
	}
	
	private int parseIntParam(final HttpServletRequest request, final String paramName) {
		final String intParamStr = request.getParameter(paramName);
		int intParam = 10;
		if (intParamStr != null) {
			try {
				intParam = Integer.parseInt(intParamStr);
				if (intParam < 1) {
					intParam = 10;
				}
			} catch (final NumberFormatException ex) {
				logger.error(String.format("Invalid number %s for parameter", intParamStr, paramName), ex);
			}
		}
		return intParam;
	}

	private void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
		/*	  $page = $this->input->post('page');
		      $limit = $this->input->post('rows'); // get how many rows we want to have into the grid
		      $sidx = $this->input->post('sidx'); // get index row - i.e. user click to sort
		      $sord = $this->input->post('sord'); // get the direction
	    */
		
		int rowsPerPage = parseIntParam(request, "rows");
		
		final List<User> users = getUsers();
		Collections.sort(users, new UserComparator());
		final int pagesCount = users.size() / rowsPerPage;
		final int page = parseCurrentPage(request, pagesCount);
		
		final JsonObject json = new JsonObject();
		json.addProperty("total", pagesCount); // total number of pages
		json.addProperty("records", users.size()); // total number of users
		json.addProperty("page", page); // current page
		
		final JsonArray rows = new JsonArray();
		
		int startI = page * rowsPerPage;
		int endI = Math.min(startI + rowsPerPage, users.size());
		for(int i = startI; i < endI; i++) {			
			final JsonObject rowObj = toJson(users.get(i));
			rows.add(rowObj);
		}
		
		writeResponse(response, json);
		/*
		 * {
  "page": "1",
  "records": "10",
  "total": "2",
  "rows": [
      {
          "id": 3,
          "cell": [
              3,
              "cell 1",
              "2010-09-29T19:05:32",
              "2010-09-29T20:15:56",
              "hurrf",
              0 
          ] 
      },
      {
          "id": 1,
          "cell": [
              1,
              "teaasdfasdf",
              "2010-09-28T21:49:21",
              "2010-09-28T21:49:21",
              "aefasdfsadf",
              1 
          ] 
      } 
  ]
}
		 */
		
		
		
	}

	private void writeResponse(HttpServletResponse response, final JsonObject json) throws IOException {		
		response.setContentType("application/json");
		response.setCharacterEncoding(charset.name());
		
		boolean success = false;
		
		final OutputStream os = response.getOutputStream();
		try {
			final String data = json.getAsString();
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

	private int parseCurrentPage(HttpServletRequest request, int pagesCount) {
		int page = parseIntParam(request, "page");		
		if (page > pagesCount) {
			if (pagesCount > 0) {
				page = pagesCount - 1;
			} else {
				page = 0;
			}
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
		rowObj.add("cell", cells);
		return rowObj;
	}
	
}
