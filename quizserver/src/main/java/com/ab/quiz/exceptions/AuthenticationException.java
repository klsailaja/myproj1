package com.ab.quiz.exceptions;

public class AuthenticationException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public AuthenticationException(String message) {
		super(message);
	}
}
