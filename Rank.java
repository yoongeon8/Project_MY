package MWProject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;

public class Rank {
	private static JFrame frame;
	private static JButton button;
	private static JLabel titlelabel;
	private MineSweeper minesweeper;
	private static List<Integer> easyTimes = new ArrayList<>();
    private static List<Integer> normalTimes = new ArrayList<>();
    private static List<Integer> hardTimes = new ArrayList<>();
    private static List<Integer> bestTimes = new ArrayList<>();
	static App app;
	
	public static void ShowRank() {
		frame = new JFrame("rank");
		frame.setSize(800, 800);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel easylabel = new JLabel("EASY");
		JLabel normallabel = new JLabel("NORMAL");
		JLabel hardlabel = new JLabel("HARD");
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.decode("#DCDCDC"));
		JPanel rankpanel = new JPanel();
		rankpanel.setLayout(new GridBagLayout());
		rankpanel.setBackground(Color.decode("#DCDCDC"));
		GridBagConstraints gbc = new GridBagConstraints();
	    gbc.fill = GridBagConstraints.HORIZONTAL;
	    gbc.insets = new Insets(10, 20, 10, 20);
	    
		button =  new JButton("Memu");
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setFont(new Font("Arial", Font.BOLD, 20));
		button.setBackground(new Color(0, 0, 70));
		button.setBorder(null);
		button.setForeground(Color.WHITE);
		button.addActionListener((ActionEvent e) -> {
			StartMemu startMenu = new StartMemu(mode -> {
                MineSweeper newGame = new MineSweeper(app,mode);
            });
            frame.dispose();
            startMenu.showMenu();
        });
		panel.add(button, BorderLayout.SOUTH);
		
		titlelabel = new JLabel("RANGKING");
		titlelabel.setFont(new Font("Arial", Font.BOLD, 35));
		titlelabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		Font modefont = new Font("Arial", Font.BOLD, 30);
		Font timefont = new Font("Arial", Font.BOLD, 25);
		
		loadTimes();
		
		easylabel.setFont(modefont);
		normallabel.setFont(modefont);
		hardlabel.setFont(modefont);
		
		easylabel.setHorizontalAlignment(JLabel.CENTER);
		normallabel.setHorizontalAlignment(JLabel.CENTER);
		hardlabel.setHorizontalAlignment(JLabel.CENTER);
		
		gbc.gridy = 0;
		gbc.gridx = 0;
		gbc.weightx = 1;
		rankpanel.add(easylabel, gbc);
		gbc.gridx = 1;
		rankpanel.add(normallabel, gbc);
		gbc.gridx = 2;
		rankpanel.add(hardlabel, gbc);
		
		JLabel bteasy = new JLabel(bestTimes.get(0) + " sec");
		JLabel btnormal = new JLabel(bestTimes.get(1) + " sec");
		JLabel bthard = new JLabel(bestTimes.get(2) + " sec");
		bteasy.setFont(timefont);
		btnormal.setFont(timefont);
		bthard.setFont(timefont);
		bteasy.setForeground(Color.decode("#F2AC2E"));
		btnormal.setForeground(Color.decode("#F2AC2E"));
		bthard.setForeground(Color.decode("#F2AC2E"));
		
		bteasy.setHorizontalAlignment(JLabel.CENTER);
		btnormal.setHorizontalAlignment(JLabel.CENTER);
		bthard.setHorizontalAlignment(JLabel.CENTER);
		
		gbc.gridy = 1;
		gbc.gridx = 0;
		rankpanel.add(bteasy, gbc);
		gbc.gridx = 1;
		rankpanel.add(btnormal, gbc);
		gbc.gridx = 2;
		rankpanel.add(bthard, gbc);
		
		int maxSize = Math.max(easyTimes.size(), Math.max(normalTimes.size(), hardTimes.size()));
        for (int i = 0; i < maxSize; i++) {
            gbc.gridy = i + 2;
            gbc.gridx = 0;
            rankpanel.add(new JLabel(i < easyTimes.size() ? easyTimes.get(i) + " sec" : "", JLabel.CENTER), gbc);

            gbc.gridx = 1;
            rankpanel.add(new JLabel(i < normalTimes.size() ? normalTimes.get(i) + " sec" : "", JLabel.CENTER), gbc);

            gbc.gridx = 2;
            rankpanel.add(new JLabel(i < hardTimes.size() ? hardTimes.get(i) + " sec" : "", JLabel.CENTER), gbc);
        }
		
		frame.add(panel);
		panel.add(titlelabel, BorderLayout.NORTH);
		panel.add(rankpanel, BorderLayout.CENTER);
		rankpanel.add(easylabel);
		rankpanel.add(normallabel);
		rankpanel.add(hardlabel);
		frame.setVisible(true);
	}
	private static void loadTimes() {
	    easyTimes.clear();
	    normalTimes.clear();
	    hardTimes.clear();
	    bestTimes.clear();
	    
        try (BufferedReader reader = new BufferedReader(new FileReader("times.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(": ");
                if (parts.length == 2) {
                    String mode = parts[0].trim();
                    String timeStr = parts[1].trim();
                    try {
                        int time = Integer.parseInt(timeStr);
                        if (mode.equals("EASY")) easyTimes.add(time);
                        else if (mode.equals("NORMAL")) normalTimes.add(time);
                        else if (mode.equals("HARD")) hardTimes.add(time);
                    } catch (NumberFormatException e) {
                        System.out.println("잘못된 형식: " + line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("game_time.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                bestTimes.add(Integer.parseInt(line.trim()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.sort(easyTimes);
        Collections.sort(normalTimes);
        Collections.sort(hardTimes);
    }
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(Rank::ShowRank);
	}
}
