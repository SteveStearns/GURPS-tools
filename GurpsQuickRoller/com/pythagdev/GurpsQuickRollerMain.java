package com.pythagdev;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GurpsQuickRollerMain {

	public static void main(String[] args) {
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
				//Get all info, and make sure we _have_ all info
				AttackInfo info = new AttackInfo();
				info.attackerSkill = parseInt(attackerSkill);
				info.attackDamage = new DamageSpecification(parseInt(attackDice), parseInt(attackFixed));
				info.defenderSkill = parseInt(defenderSkill);
				info.damageResistance = parseInt(defenderDR);
				
				int iterations = parseInt(runCount);
				
				Dice random = new Dice();
				
				//Now that we have everything collected, we can run the simulation and print results to screen
				results.addText("===Running " + iterations + " iterations; " + info + "===");
				results.addLevel();		// Nest these iterations together
				for (int n = 0; n < iterations; ++n) {
					AttackResults attackResults = info.run(random);
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