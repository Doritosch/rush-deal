package com.rushcrew.order_service.global.util;

public class RoleChecker {

	private RoleChecker() {}

	public static void checkRole(String role, String... allowedRoles) {
		if (role == null) {
			throw new RuntimeException("권한 정보가 없습니다.");
		}

		for (String r : allowedRoles) {
			if (role.equals(r)) return;
		}

		throw new RuntimeException("권한이 없습니다. role=" + role);
	}
}
