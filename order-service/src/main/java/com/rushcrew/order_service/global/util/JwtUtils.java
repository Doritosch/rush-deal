package com.rushcrew.order_service.global.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public class JwtUtils {
	private JwtUtils() {} // 인스턴스 생성 방지

	/** 현재 로그인한 userId 가져오기 */
	public static Long getCurrentUserId() {
		Jwt jwt = getCurrentJwt();
		return Long.parseLong(jwt.getSubject());
	}

	/** 현재 로그인한 role 가져오기 */
	public static String getCurrentUserRole() {
		Jwt jwt = getCurrentJwt();
		return jwt.getClaim("role");
	}

	/** SecurityContext에서 Jwt 가져오기 */
	private static Jwt getCurrentJwt() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new RuntimeException("사용자 인증 정보가 없습니다.");
		}

		Object principal = authentication.getPrincipal();
		if (!(principal instanceof Jwt jwt)) {
			throw new RuntimeException("JwtAuthenticationToken이 아닙니다.");
		}

		return jwt;
	}
}
