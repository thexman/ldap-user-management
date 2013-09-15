package com.a9ski.gerrit.model;

import java.util.Comparator;

public class UserComparator implements Comparator<User> {

	public static enum Field {
		ID, FIRSTNAME, LASTNAME, EMAIL;
	}
	
	private Field field = Field.ID;
	
	private int compareStrings(String a, String b) {
		if (a != null && b != null) {
			return a.compareTo(b);
		} else if (a != null) {
			return 1;
		} else if (b != null) {
			return -1;
		} else {
			return 0;
		}
	}
	
	@Override
	public int compare(User u1, User u2) {
		int res = 0;
		if (u1 != null && u2 != null) {
			switch(field) {
				case ID: res = compareStrings(u1.getId(), u2.getId()); break;
				case FIRSTNAME: res = compareStrings(u1.getFirstName(), u2.getFirstName()); break;
				case LASTNAME: res = compareStrings(u1.getLastName(), u2.getLastName()); break;
				case EMAIL: res = compareStrings(u1.getEmail(), u2.getEmail()); break;
				default: res = u1.hashCode() - u2.hashCode();
			}
		} else if (u1 != null) {
			res = 1;
		} else if (u2 != null) {
			res = -1;
		} else {
			res = 0;
		}
		
		return res;
	}

}
