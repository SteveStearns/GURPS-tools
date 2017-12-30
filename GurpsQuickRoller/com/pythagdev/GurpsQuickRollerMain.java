package com.pythagdev;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.*;
import com.pythagdev.JsonController;

public class GurpsQuickRollerMain {
	public static JsonController jCtrl = null;
	public static void main(String[] args) {
		if (args.length == 1) {
			jCtrl = new JsonController(args[0]);
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				//display screen
				TextFrame frame = new TextFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}

	/**Main Frame for the program*/
	private static class TextFrame extends JFrame implements ActionListener {
		private static final long serialVersionUID = 9206769183606775458L;
	
		private static BufferedImage icon = null;
		static {
			try {
				icon = ImageIO.read(new File("icon.png"));
			} catch (IOException e) {
			}
		}
		
		private final JLabel attackerSkillLabel = new JLabel("Attacker skill:");
		private final JTextField attackerSkill = new JTextField("10", 5);

		private final JLabel attackLabel = new JLabel("Attack damage:");
		private final JLabel attackDiceLabel = new JLabel("d+");
		private final JTextField attackDice = new JTextField("3", 5);
		private final JTextField attackFixed = new JTextField("0", 5);

		private final JLabel defenderSkillLabel = new JLabel("Defender skill:");
		private final JTextField defenderSkill = new JTextField("10", 5);

		private final JLabel defenderDRLabel = new JLabel("Defender DR:");
		private final JTextField defenderDR = new JTextField("5", 5);
		
		private final JTextField runCount = new JTextField("5", 5);
		private final JButton runButton = new JButton("Run");

		private final TextTree results = new TextTree();
		private final JScrollPane scrollPane = new JScrollPane(results.getTree());
		
		public TextFrame() {
			setTitle("GURPS - Quick Attack/Defense Roller");
			if (icon != null) {
				setIconImage(icon);
			}
			
			setSize(600, 400);
			setResizable(true);

			runCount.addActionListener(this);
			runButton.addActionListener(this);
			
			setLayout(new GridBagLayout());
			add(attackerSkillLabel, gbc(0, 0, 1, 1));
			add(attackerSkill, gbc(1, 0, 1, 1, GridBagConstraints.HORIZONTAL));

			add(attackLabel, gbc(0, 1, 1, 1));
			add(attackDice, gbc(1, 1, 1, 1, GridBagConstraints.HORIZONTAL));
			add(attackDiceLabel, gbc(2, 1, 1, 1));
			add(attackFixed, gbc(3, 1, 1, 1, GridBagConstraints.HORIZONTAL));

			GridBagConstraints constraints = gbc(4, 0, 1, 1);
			constraints.weightx = 5;
			add(new JPanel(), constraints);

			add(defenderSkillLabel, gbc(5, 0, 1, 1));
			add(defenderSkill, gbc(6, 0, 1, 1, GridBagConstraints.HORIZONTAL));

			add(defenderDRLabel, gbc(5, 1, 1, 1));
			add(defenderDR, gbc(6, 1, 1, 1, GridBagConstraints.HORIZONTAL));

			gbcAnchor = GridBagConstraints.CENTER;
			add(runCount, gbc(3, 2, 1, 1, GridBagConstraints.HORIZONTAL));
			add(runButton, gbc(4, 2, 1, 1));

			constraints = gbc(0, 3, 7, 1, GridBagConstraints.BOTH);
			constraints.weighty = 10;
			
			scrollPane.setHorizontalScrollBarPolicy(scrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			add(scrollPane, constraints);
		}

		private static GridBagConstraints gbc(int x, int y, int width, int height) {
			return gbc(x, y, width, height, GridBagConstraints.NONE);
		}

		private static int gbcAnchor = GridBagConstraints.FIRST_LINE_START;
		private static final GridBagConstraints GRID_BAG_CONSTRAINTS = new GridBagConstraints();
		private static GridBagConstraints gbc(int x, int y, int width, int height, int fill) {
			GRID_BAG_CONSTRAINTS.gridx = x;
			GRID_BAG_CONSTRAINTS.gridy = y;
			GRID_BAG_CONSTRAINTS.gridwidth = width;
			GRID_BAG_CONSTRAINTS.gridheight = height;
			GRID_BAG_CONSTRAINTS.fill = fill;
			GRID_BAG_CONSTRAINTS.ipadx = GRID_BAG_CONSTRAINTS.ipady = 2;
			GRID_BAG_CONSTRAINTS.weightx = GRID_BAG_CONSTRAINTS.weighty = 1;
			GRID_BAG_CONSTRAINTS.anchor = gbcAnchor;
			return GRID_BAG_CONSTRAINTS;
		}

		@Override
		public void actionPerformed(ActionEvent action) {
			try {
				String sAttack = null;
				String sDefend = null;
				int iterations = parseInt(runCount);
				// Here's the deal, we use a JSON file rather than a generic 10,
				// if and only if the json file is present.
				// We eventually have a Goon-generator and a Google-sheets reader to create json files.
				if (jCtrl != null) {
					sAttack = jCtrl.getString("AttackerFile");
					sDefend = jCtrl.getString("DefenderFile");
				}
				
				//Get all info, and make sure we _have_ all info
				ArrayList<AttackInfo> infoArray = new ArrayList<AttackInfo>();
				JsonController jAttack = null;
				JsonController jDefend = null;
				try {
					jAttack = new JsonController(sAttack);
					jAttack.setArray("Players");
				} catch (Exception e) {
					jAttack = null;
				}
				try {
					jDefend = new JsonController(sDefend);
					jDefend.setArray("Players");
				} catch (Exception e) {
					jDefend = null;
				}
//				The JSON files might not be as large as the runCount field. Avoid a crash.
				int maxIterate = iterations;
				if ((jAttack != null) && (jDefend != null))
					maxIterate = Integer.min(jAttack.getArray().size(), jDefend.getArray().size());
				else {
					if (jAttack != null) maxIterate = jAttack.getArray().size();
					if (jDefend != null) maxIterate = jDefend.getArray().size();
				}
				for (int i = 0; i < maxIterate; ++i) {
					AttackInfo info = new AttackInfo();
					int iFixed, iDice;
					if (jAttack == null) {
						info.attackerSkill = parseInt(attackerSkill);
						iFixed = parseInt(attackFixed);
						iDice = parseInt(attackDice);
					}
					else {
						info.attackName = jAttack.getString(i,"Attacker");
						info.attackerSkill = jAttack.getInt(i,"AttackSkill");
						iFixed = jAttack.getInt(i, "AttackFixed");
						iDice = jAttack.getInt(i, "AttackDice");
					} 
					info.attackDamage = new DamageSpecification(iDice, iFixed);
					if (jDefend == null) {
						info.defenderSkill = parseInt(defenderSkill);
						info.damageResistance = parseInt(defenderDR);						
					}
					else {
						info.defendName = jDefend.getString(i,"Defender");
						info.defenderSkill = jDefend.getInt(i, "DefendSkill");
						info.damageResistance = jDefend.getInt(i, "DR");
					}
					infoArray.add(info);
				}
				
				Dice random = new Dice();
				
				//Now that we have everything collected, we can run the simulation and print results to screen
				results.addText("===Running " + maxIterate + " iterations ===");
				results.addLevel();		// Nest these iterations together
				for (int n = 0; n < maxIterate; ++n) {
					results.addText("=== iteration; " + infoArray.get(n) + " ===");
					AttackResults attackResults = infoArray.get(n).run(random);
					results.addText("#" + n + ": " + attackResults.getMessage());
					results.addLevel();
					results.addText(attackResults.getInfo1());
					results.endLevel();
				}
				//textArea.append("\n");
				results.endLevel();
			} catch (NumberFormatException ignored) {	
			}
		}

		private int parseInt(JTextField textField) {
			textField.setBackground(Color.WHITE);
			try {
				return Integer.parseInt(textField.getText());
			} catch (NumberFormatException e) {
				textField.setBackground(Color.RED);
				throw e;
			}
		}
	}
}