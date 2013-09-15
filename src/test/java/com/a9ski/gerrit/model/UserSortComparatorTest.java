package com.a9ski.gerrit.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class UserSortComparatorTest {

	@Test
	public void testSort() {
		final List<User> users = new ArrayList<User>();
		for(int i = 0; i < 50; i++) {
			final String s = String.valueOf(i);
			users.add(new User("id" + s, "John" + s, "Doe", "john" +s + "@email.rar", "xxx"));
		}
		assertEquals(50, users.size());
		
		Collections.sort(users, new UserSortComparator());
		
		assertEquals(50, users.size());
		
		for(int i = 1; i < users.size(); i++) {
			assertTrue(users.get(i-1).getId().compareTo(users.get(i).getId()) < 0);
		}
		
	}
	
	
	@Test
	public void testCompare() {
		final User u1 = new User("1", "", "", "", "");
		final User u2 = new User("2", "", "", "", "");
		final UserSortComparator c = new  UserSortComparator();
		assertEquals(-1, c.compare(u1, u2));
		assertEquals( 1, c.compare(u2, u1));
		assertEquals( 0, c.compare(u1, u1));
	}

}
