package MWProject;

import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.awt.Color;

public class BoomDefuse {
    private MineSweeper minesweeper;
    private static long startTime;  // 타이머 시작 시간
    private static long targetTime; // 목표 시간
    private static boolean gameRunning = false; // 게임 진행 여부
    private static JButton stopButton;
    private static JLabel messageLabel;
    private static JLabel timerLabel; // 경과 시간을 표시할 레이블
    private static Timer timer; // 타이머
    static JFrame frame = new JFrame("Timer Game");
    
    static App app;
    
    public BoomDefuse(App app) {
    	this.app = app;
    }

    public static void createAndShowGUI() {
    	ImageIcon image = new ImageIcon("imgs/clock.png");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 475);
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setIconImage(image.getImage());

        frame.getContentPane().setBackground(Color.decode("#A1887F"));
        
        messageLabel = new JLabel("버튼을 눌러서 타이머를 시작하세요!", SwingConstants.CENTER);
        frame.add(messageLabel, BorderLayout.CENTER);

        timerLabel = new JLabel("경과 시간: 0.000초", SwingConstants.CENTER);
        frame.add(timerLabel, BorderLayout.NORTH);
        frame.setBackground(Color.WHITE);

        stopButton = new JButton("타이머 멈추기");
        stopButton.setEnabled(false); // 초기에는 비활성화
        frame.add(stopButton, BorderLayout.SOUTH);

        stopButton.addActionListener(e -> handleStopButton());

        JButton startButton = new JButton("타이머 시작");
        frame.add(startButton, BorderLayout.WEST);
        frame.setBackground(Color.GRAY);
        startButton.addActionListener(e -> startTimer());

        frame.setVisible(true);
    }

    static void startTimer() {
        if (gameRunning) return; // 이미 게임이 진행 중일 경우 무시

        Random random = new Random();
        int randomSeconds = random.nextInt(16) + 5; // 5~20초 사이 랜덤 시간 설정
        targetTime = randomSeconds * 1000; // 밀리초로 변환
        startTime = System.currentTimeMillis(); // 현재 시간 기록

        messageLabel.setText("타이머가 시작되었습니다. " + randomSeconds + "초 안에 멈추세요!");
        stopButton.setEnabled(true); // 멈추기 버튼 활성화
        gameRunning = true;

        // 타이머 설정 (1밀리초마다 경과 시간을 업데이트)
        timer = new Timer(1, e -> updateTimer());
        timer.start(); // 타이머 시작
    }

    private static void updateTimer() {
        if (gameRunning) {
            long elapsedTime = System.currentTimeMillis() - startTime; // 경과 시간 계산
            timerLabel.setText(String.format("경과 시간: %.3f초", elapsedTime / 1000.0)); // 경과 시간을 소수점 3자리로 표시
            timerLabel.setBackground(Color.GRAY);
            
            if (elapsedTime >= targetTime) { // 목표 시간에 도달하면 타이머 멈추기
                timer.stop();
                handleStopButton();
            }
        }
    }

    private static void handleStopButton() {
        if (!gameRunning) return; // 게임이 시작되지 않았으면 무시

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime; // 경과 시간 계산

        long timeDifference = Math.abs(elapsedTime - targetTime); // 목표 시간과의 차이

        timer.stop();
        	
        if (timeDifference <= 200 && targetTime > elapsedTime) { // 오차범위가 0.2초 이내일 경우 성공
            messageLabel.setText("성공! 오차: " + timeDifference / 1000.0 + "초");
            String[] responses = {"예","아니요"};
            ImageIcon icon = new ImageIcon(" ");
            int result = JOptionPane.showOptionDialog(null, "게임을 계속하시겠습니까?", "quetion", JOptionPane.YES_NO_OPTION, 
            		JOptionPane.QUESTION_MESSAGE, icon, responses, 0);
            if (result == JOptionPane.YES_OPTION) {
                StartMemu startMenu = new StartMemu(mode -> {
                    MineSweeper newGame = new MineSweeper(app,mode);
                });
                startMenu.showMenu();
                frame.dispose(); // 창 닫기
            } 
            else if (result == JOptionPane.NO_OPTION) {
                frame.dispose(); // 창 닫기
            } 
            else {
                frame.dispose(); // 창 닫기
            }
        }
        else{
            messageLabel.setText("실패! 오차: " + timeDifference / 1000.0 + "초");
            String[] responses = {"예","아니요"};
            ImageIcon icon = new ImageIcon(" ");
            int result = JOptionPane.showOptionDialog(null, "게임을 계속하시겠습니까?", "quetion", JOptionPane.YES_NO_OPTION, 
            		JOptionPane.QUESTION_MESSAGE, icon, responses, 0);
            if (result == JOptionPane.YES_OPTION) {
                StartMemu startMenu = new StartMemu(mode -> {
                    MineSweeper newGame = new MineSweeper(app,mode);
                });
                startMenu.showMenu();
                frame.dispose(); // 창 닫기
            } 
            else if (result == JOptionPane.NO_OPTION) {
                frame.dispose(); // 창 닫기
            } 
            else {
                frame.dispose(); // 창 닫기
            }
        }

        stopButton.setEnabled(false); // 멈추기 버튼 비활성화
        gameRunning = false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BoomDefuse::createAndShowGUI);
    }
}
