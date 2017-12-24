package com.pythagdev;

public class AttackResults {
	private final String message;

	public AttackResults(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return message;
	}
}