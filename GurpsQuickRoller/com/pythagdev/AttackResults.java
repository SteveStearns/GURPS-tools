package com.pythagdev;

public class AttackResults {
	private final String message, 
						 info1;

	public AttackResults(String message, String info1) {
		this.message = message;
		this.info1 = info1;
	}
	
	public String getMessage() {return message;}
	public String getInfo1() {return info1;}

	@Override
	public String toString() {
		return message + ": \n" + info1;
	}
}