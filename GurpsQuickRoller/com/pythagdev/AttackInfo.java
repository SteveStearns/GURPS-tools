package com.pythagdev;

import com.pythagdev.AttackResults;
import com.pythagdev.DamageSpecification;
import com.pythagdev.Dice;
// import com.pythagdev.AttackInfo.DiceResult;

public class AttackInfo {
	public String attackName = "attacker";
	public int attackerSkill = 10;
	public DamageSpecification attackDamage;
	//TODO - if ranged: distance, cover
	//TODO - hit location attempt?
	public String defendName = "defender";
	public int defenderSkill = 10;
	//TODO - isUnarmedWithParry?
	public int damageResistance = 0;

	//TODO - have AttackResults carry/display more info
	public AttackResults run(Dice random) {
		int attackRoll = random.nextRoll();
		switch (getEffect(attackRoll, attackerSkill)) {
		case CRITICAL_SUCCESS:
			return new AttackResults("Critical Hit (p202) for attacker!",  getCriticalSuccessString(random));
		case CRITICAL_FAILURE:
			return new AttackResults("Critical Miss (p202) for attacker! ", getCriticalFailureString(random));
		case SUCCESS:
			int defenseRoll = random.nextRoll();
			switch (getEffect(defenseRoll, defenderSkill)) {
			case CRITICAL_SUCCESS:
				return new AttackResults("Active Defense - Critical Success! Attacker has a Critical Miss (p202) "
						+ "(ignore this if the attack was ranged)!", getCriticalFailureString(random));
			case CRITICAL_FAILURE:	//p110
				//TODO - p110 - Maybe 17 is always critical failure???
				int damage2 = random.rollDamage(attackDamage);
				return new AttackResults("Active Defense - Critical Failure (p110)!", "Defender: if dodging, falls over; "
						+ "if blocking, shield needs to be readied next turn before being used for an active defense; "
						+ "if parrying, defender rolls against Critical Miss table (p202). Also, attack did "
						+ modForDR(damage2) + " damage.");
			case SUCCESS:
				return new AttackResults("Attack blocked.", null);
			case FAILURE:
				int damage = random.rollDamage(attackDamage);
				return new AttackResults("Attack did " + modForDR(damage) + " damage.", null);
			}
			return new AttackResults("OK. Seriously? How did we get a null effect from a defenseRoll of " + defenseRoll + "?", null);
		case FAILURE:
			return new AttackResults("Attack failed.", null);
		}
		return new AttackResults("OK. Seriously? How did we get a null effect from an attackRoll of " + attackRoll + "?", null);
	}

	private String getCriticalSuccessString(Dice random) {	//Page 202, Critical Success Table
		int critEffect = random.nextRoll();
		int damage = random.rollDamage(attackDamage);
		switch (critEffect) {
		case 3:
		case 18:
			return "If the blow hit the torso, it does " + modForDR(damage) + " damage and the foe "
					+ "is knocked unconscious. Roll vs. HT every 30 mintues to recover. Otherwise, "
					+ "it does " + modForDR(3*damage) + " damage (3x).";
		case 4:
		case 13:
			return "The blow <i>bypasses all armor</i> and does " + damage + " damage.";
		case 5:
		case 17:
			return "The blow does " + modForDR(3*damage) + " damage (3x).";
		case 6:
		case 16:
			return "The blow does " + modForDR(2*damage) + " damage (2x).";
		case 7:
			return "The foe is stunned until he makes his HT roll, and takes " + modForDR(damage) + " damage.";
		case 8:
		case 12:
			return "The foe takes " + modForDR(damage) + " damage. If the blow hit an arm, leg, hand, or foot AND said "
					+ "appendage is not crippled, \"funny-bone\" injury cripples said limb (regardless of damage) "
					+ "for 6 turns.";
		case 9:
		case 10:
		case 11:
			return "The foe takes " + modForDR(damage) + " damage. (No unusual effect.)";
		case 14:
			return "If the blow hit an arm, leg, hand, or foot, it does " + modForDR(damage) + " damage, and that "
					+ "body part is <i>crippled</i> regardless of the amount of damage done. Otherwise, "
					+ "the blow does " + modForDR(2*damage) + " damage (2x).";
		case 15:
			return "Enemy's weapon is droped, <i>and</i> he takes " + modForDR(damage) + " damage.";
		default:
			return "Critical roll of " + critEffect + " should be impossible... ??? :P";
		}
	}

	private String getCriticalFailureString(Dice random) {	//Page 202, Critical Failure Table
		int critEffect = random.nextRoll();
		switch (critEffect) {
		case 3:
		case 4:
		case 17:
		case 18:
			return "Your weapon breaks and is useless. Exception: Certain weapons are resistant to breakage. "
					+ "These include <i>maces, flails, mauls, metal bars</i>, and other solid \"crushing\" "
					+ "weapons; <i>magic weapons</i>; and other <i>finely-made</i> weapons. If you have a weapon "
					+ "like that, roll again. Only if you get a \"broken weapon\" result a second time does the "
					+ "weapon really break. If you get any other result, you drop the weapon instead. See "
					+ "<i>Broken Weapons</i>, p. 113.";
		case 5:
		case 6:
			String damagePoint = random.nextBoolean() ? "arm" : "leg";
			String wholeHalf = critEffect == 5 ? "full" : "half";
			return "You managed to hit <i>yourself</i> in the " + damagePoint + " for " + wholeHalf + " damage. "
					+ "Exception: If this was an impaling or ranged attack, roll again. It's hard to stab "
					+ "yourself, but it can be done. (If you get two \"hit yourself\" results in a row, count the "
					+ "second.)";
		case 7:
		case 13:
			return "You lose your balance. You can do nothing else until your next turn. "
					+ "All your active defences are at -2 until your next turn.";
		case 8:
		case 12:
			return "The weapon turns in your hand. Spend one extra turn to ready it before you use it again.";
		case 9:
		case 10:
		case 11:
			return "You drop the weapon. Exception: A <i>cheap</i> weapon <i>breaks</i>. See p. 113 for "
					+ "dropped/broken weapons.";
		case 14:
			int distance = random.nextD6();
			String direction = random.nextBoolean() ? "forward" : "back";
			return "Your weapon flies " + distance + " yards from your hand, straight " + direction + ". Anyone "
					+ "on the target spot must make their DX roll or take half damage from the falling weapon! "
					+ "Exception: if this was an impaling attack, you simply drop the weapon, as in #9 above. A "
					+ "missile weapon will not fly from your hand - it just drops.";
		case 15:
			return "You strained your shoulder! Your weapon arm is \"crippled\" for the rest of the encounter. "
					+ "You do not have to drop your weapon, but you cannot use it, either to attack or defend, "
					+ "for 30 minutes.";
		case 16:
			return "You fall down! (Ranged weapon users, instead use - \"You lose your balance. You can do nothing "
					+ "else until your next turn. All your active defences are at -2 until your next turn.\")";
		default:
			return "Critical roll of " + critEffect + " should be impossible... ??? :P";
		}
	}

	private int modForDR(int damage) {
		return Math.max(damage - damageResistance, 0);
	}

	private enum DiceResult {
		CRITICAL_SUCCESS,
		CRITICAL_FAILURE,
		SUCCESS,
		FAILURE
	}

	private DiceResult getEffect(int roll, int skill) {
		if (roll <= 4 || (roll <= 6 && roll <= skill - 10)) {	//Critical success
			return DiceResult.CRITICAL_SUCCESS;
		} else if (roll == 18 || (roll == 17 && skill < 16) || (roll - skill >= 10)) {	//Critical failure
			return DiceResult.CRITICAL_FAILURE;
		} else if (roll == 17 || roll > skill) {	//Fail
			return DiceResult.FAILURE;
		} else {	//Success
			return DiceResult.SUCCESS;
		}
	}
	
	@Override
	public String toString() {
		return attackName + " skill=" + attackerSkill + ", attack damage=" + attackDamage.dice + "d+" + attackDamage.flat +
				", " + defendName + " skill=" + defenderSkill + ", defender DR=" + damageResistance;
	}
}
