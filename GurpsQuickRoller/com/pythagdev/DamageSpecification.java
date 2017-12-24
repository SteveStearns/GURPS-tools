package com.pythagdev;

public class DamageSpecification {
	public final int dice;	//number of dice to roll
	public final int flat;	//flat modifier
	public DamageSpecification(int dice, int flat) {
		this.dice = dice;
		this.flat = flat;
	}
}