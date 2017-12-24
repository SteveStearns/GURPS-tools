package com.pythagdev;

import java.util.Random;

public class Dice {
	private final Random random = new Random();

	public int nextRoll() {
		return random.nextInt(6) + random.nextInt(6) + random.nextInt(6) + 3;
	}

	public int rollDamage(DamageSpecification spec) {
		int result = spec.flat + spec.dice;
		for (int n = 0; n < spec.dice; ++n) {
			result += random.nextInt(6);
		}
		if (result < 0) {
			return 0;
		} else {
			return result;
		}
	}

	public boolean nextBoolean() {
		return random.nextBoolean();
	}

	public int nextD6() {
		return random.nextInt(6) + 1;
	}
}