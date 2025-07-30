package com.databasemeta.ahn.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import org.springframework.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * 스태틱 서블릿 접근 클래스.
 */
public class ServletRequestInfoUtil {
	
	/**
	 * Gets the request.
	 *
	 * @return the request
	 */
	public static HttpServletRequest getRequest() {
		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return req;
	}
	
	/**
	 * Gets the session.
	 *
	 * @param create the create
	 * @return the session
	 */
	public static HttpSession getSession(boolean create) {
		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return req.getSession(create);
	}

	/**
	 * Gets the request domain.
	 *
	 * @return the request domain
	 */
	public static String getRequestDomain() {
		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return StringUtils.hasText(req.getServerName()) ? req.getServerName() : "";
	}
	
	/**
	 * 접속 아이피 조회
	 *
	 * @return the request ip
	 */
	public static String getRequestIp() {
		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return StringUtils.hasText(req.getRemoteAddr()) ? req.getRemoteAddr() : "";
	}
	
	/**
	 * 접속 포트 조회
	 *
	 * @return the request port
	 */
	public static int getRequestPort() {
		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return req.getServerPort() ;
	}	
	
	/**
	 * Gets the request ssl.
	 *
	 * @return the request ssl
	 */
	public static String getRequestSSL() {
		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return String.valueOf(req.isSecure()) ;
	}	
	
	/**
	 * 접속 쿼리스트링 조회
	 *
	 * @return the query string
	 */
	public static String getQueryString() {
		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return String.valueOf(req.getQueryString()) ;
	}
	
	/**
	 * 접속 경로 조회
	 *
	 * @return the servlet path
	 */
	public static String getServletPath() {
		String result = "";
		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String  path = req.getServletPath();
		return path;
	}
	
	/**
	 * 접속 이전 경로 조회
	 *
	 * @return the referer
	 */
	public static String getReferer() {
		String result = "";
		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String  path = req.getHeader("referer");
		return path;
	}

}