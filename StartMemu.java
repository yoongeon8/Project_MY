package MWProject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class StartMemu{
    private final java.util.function.Consumer<String> onStartGame; // 게임 시작 콜백, 선택된 모드를 전달
    private String selectedMode = null; // 선택된 모드 저장

    // 생성자에서 콜백을 전달받음
    public StartMemu(java.util.function.Consumer<String> onStartGame) {
        this.onStartGame = onStartGame;
    }

    // StartMenu를 표시하는 메서드
    public void showMenu() {
        JFrame frame = new JFrame("Memu display");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 900);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);

        // 이미지 경로 설정
        String imagePath = "imgs/Final.png";
        ImageIcon icon = new ImageIcon(imagePath);
        Image scaledImage = icon.getImage().getScaledInstance(900, 900, Image.SCALE_SMOOTH);
        JLabel backgroundLabel = new JLabel(new ImageIcon(scaledImage));
        backgroundLabel.setBounds(0, 0, 900, 900);

        // 버튼 생성
        JButton startButton = new JButton("GAME START");
        startButton.setFont(new Font("Arial", Font.BOLD, 25));
        startButton.setFocusPainted(false);
        startButton.setBackground(Color.decode("#90EE90"));
        startButton.setForeground(Color.WHITE);
        startButton.setBounds(375, 500, 200, 50);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton ranking = new JButton("RANKING");
        ranking.setFont(new Font("Arial", Font.BOLD, 25));
        ranking.setFocusPainted(false);
        ranking.setBackground(Color.decode("#424242"));
        ranking.setForeground(Color.WHITE);
        ranking.setBounds(375, 550, 200, 50);
        ranking.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton easy = new JButton("EASY");
        easy.setFont(new Font("Arial", Font.BOLD, 25));
        easy.setFocusPainted(false);
        easy.setBackground(Color.GRAY);
        easy.setForeground(Color.WHITE);
        easy.setBounds(600, 600, 250, 50);

        JButton normal = new JButton("NORMAL");
        normal.setFont(new Font("Arial", Font.BOLD, 25));
        normal.setFocusPainted(false);
        normal.setBackground(Color.GRAY);
        normal.setForeground(Color.WHITE);
        normal.setBounds(600, 650, 250, 50);

        JButton hard = new JButton("HARD");
        hard.setFont(new Font("Arial", Font.BOLD, 25));
        hard.setFocusPainted(false);
        hard.setBackground(Color.GRAY);
        hard.setForeground(Color.WHITE);
        hard.setBounds(600, 700, 250, 50);

        // 시작 버튼 클릭 이벤트
        startButton.addActionListener((ActionEvent e) -> {
            if (selectedMode == null) {
                JOptionPane.showMessageDialog(frame, "Please select a mode before starting!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            frame.dispose(); // 시작 화면 닫기
            onStartGame.accept(selectedMode); // 선택된 모드 전달
        });

        // 모드 버튼 클릭 이벤트
        easy.addActionListener((ActionEvent e) -> {
            selectedMode = "EASY";
            easy.setBackground(Color.decode("#90EE90")); // 선택
            normal.setBackground(Color.GRAY); // 다른 모드 비활성화
            hard.setBackground(Color.GRAY);
        });

        normal.addActionListener((ActionEvent e) -> {
            selectedMode = "NORMAL";
            normal.setBackground(Color.decode("#90EE90")); // 선택
            easy.setBackground(Color.GRAY); // 다른 모드 비활성화
            hard.setBackground(Color.GRAY);
        });

        hard.addActionListener((ActionEvent e) -> {
            selectedMode = "HARD";
            hard.setBackground(Color.decode("#90EE90")); // 선택
            easy.setBackground(Color.GRAY); // 다른 모드 비활성화
            normal.setBackground(Color.GRAY);
        });

        // 레이어드 패널로 배경과 버튼 겹치기
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 900, 900);
        layeredPane.add(backgroundLabel, Integer.valueOf(0));
        layeredPane.add(startButton, Integer.valueOf(1));
        layeredPane.add(ranking, Integer.valueOf(2));
        layeredPane.add(easy, Integer.valueOf(3));
        layeredPane.add(normal, Integer.valueOf(4));
        layeredPane.add(hard, Integer.valueOf(5));

        frame.add(layeredPane);
        frame.setVisible(true);
    }
}
