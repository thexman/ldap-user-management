package com.a9ski.gerrit.serlvets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;

public class UserServiceServletTest {

	private UserServiceServlet servlet = null;
	
	@Before
	public void setup() {
		servlet = new UserServiceServlet();
	}
	
	@Test
	public void testParseIntParam() throws Exception {		
		//final UserServiceServlet s = PowerMockito.spy(new UserServiceServlet());
		
		assertEquals(1, Whitebox.invokeMethod(servlet, "parseIntParam", "1", 10));
		assertEquals(10, Whitebox.invokeMethod(servlet, "parseIntParam", (String)null, 10));
		
		try {
			Whitebox.invokeMethod(servlet, "parseIntParam", "q1", 10);
			fail("Expected NumberFormatException to be thrown");
		} catch (NumberFormatException ex){
			
		}		
	}
	
	@Test
	public void testParsePage() throws Exception {
		final HttpServletRequest requestMock = PowerMockito.mock(HttpServletRequest.class);
		PowerMockito.when(requestMock.getParameter("page")).thenReturn("42");
		
		assertEquals(42, Whitebox.invokeMethod(servlet, "parsePage", requestMock, 50));
		
		assertEquals(10, Whitebox.invokeMethod(servlet, "parsePage", requestMock, 10));
		
		PowerMockito.when(requestMock.getParameter("page")).thenReturn("-42");
		
		assertEquals(1, Whitebox.invokeMethod(servlet, "parsePage", requestMock, 10));
	}
	
	@Test
	public void testParseRowsPerPage() throws Exception {
		final HttpServletRequest requestMock = PowerMockito.mock(HttpServletRequest.class);
		
		PowerMockito.when(requestMock.getParameter("rows")).thenReturn("42");		
		assertEquals(42, Whitebox.invokeMethod(servlet, "parseRowsPerPage", requestMock));
		
		PowerMockito.when(requestMock.getParameter("rows")).thenReturn("0");
		assertEquals(10, Whitebox.invokeMethod(servlet, "parseRowsPerPage", requestMock));
		
		PowerMockito.when(requestMock.getParameter("rows")).thenReturn("xxx");		
		assertEquals(10, Whitebox.invokeMethod(servlet, "parseRowsPerPage", requestMock));
	}

}
