package MWProject;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class LightGame {
    private MineSweeper minesweeper;
    private static int lightPosition;  // 불빛의 위치 저장
    private static JButton[] buttons = new JButton[16];
    private static JFrame frame;
    private static boolean gameRunning = false; // 게임 진행 여부
    private static Timer timer; // 타이머 객체를 전역 변수로
    private static Timer gameTimer;  // 게임 전체 제한 시간 타이머
    private static JLabel timeLabel; // 남은 시간 표시 레이블
    private static int timeLeft = 10; // 남은 시간 초기값
    
    static App app;
    
    public LightGame(App app) {
    	this.app = app;	
    }
    
    public static void createAndShowGUI() {
    	ImageIcon icon = new ImageIcon("imgs/bulb.png");
    	frame = new JFrame("Light Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 525); // 시간을 표시하기 위해 높이 조정 원래 크기는 475임.
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setIconImage(icon.getImage());

        // 상단에 시간 표시 레이블 추가
        timeLabel = new JLabel(" time: " + timeLeft + " seconds", SwingConstants.LEFT);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(timeLabel, BorderLayout.NORTH);

        // 중앙에 4x4 버튼 그리드 추가
        JPanel gridPanel = new JPanel(new GridLayout(4, 4));
        Random random = new Random();
        lightPosition = random.nextInt(16);

        for (int i = 0; i < 16; i++) {
            buttons[i] = new JButton();
            buttons[i].setBackground(Color.WHITE);
            gridPanel.add(buttons[i]);

            final int idx = i;
            buttons[i].addActionListener(e -> handleButtonClick(idx));
        }

        // 첫 번째 불빛 위치 설정
        buttons[lightPosition].setBackground(Color.YELLOW);

        frame.add(gridPanel, BorderLayout.CENTER);
        frame.setVisible(true);

        startGame(); // 게임 시작
    }

    private static void startGame() {
        if (gameRunning) return; // 이미 게임이 진행 중일 경우 무시

        gameRunning = true; // 게임 진행 상태로 설정
        
        // 0.5초마다 불빛을 이동시키는 타이머 설정
        timer = new Timer(500, e -> moveLight());
        timer.start(); // 타이머 시작
        
        gameTimer = new Timer(1000, e -> updateTime());
        gameTimer.start(); // 제한 시간 타이머 시작
    }
    
    private static void updateTime() {
        timeLeft--; // 남은 시간 감소
        timeLabel.setText(" time: " + timeLeft + " seconds"); // 레이블 업데이트

        if (timeLeft <= 0) {
            endGameDueToTimeout(); // 시간 초과로 게임 종료
        }
    }

    private static void handleButtonClick(int idx) {
        if (!gameRunning) return; // 게임이 진행 중이 아닐 경우 무시

        if (idx == lightPosition) {
            showAutoCloseDialog("불빛을 맞췄습니다! 게임 종료.");
            endGame();
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
        else {
            showAutoCloseDialog("틀렸습니다. 다시 시도하세요.");
        }
    }

    private static void moveLight() {
        // 이전 불빛의 배경색을 원래대로
        buttons[lightPosition].setBackground(Color.WHITE);

        // 새 불빛 위치 설정
        Random random = new Random();
        lightPosition = random.nextInt(16);

        // 새 위치에 불빛 설정
        buttons[lightPosition].setBackground(Color.YELLOW);
    }
    
    private static void endGameDueToTimeout() {
        if (!gameRunning) return; // 이미 종료되었으면 무시

        timeLabel.setText("Time Over! Game END~!");
        endGame(); // 게임 종료
        endGame();
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
    
    private static void endGame() {
        gameRunning = false; // 게임 진행 상태 종료
        if (timer != null) timer.stop(); // 불빛 타이머 멈추기
        if (gameTimer != null) gameTimer.stop(); // 제한 시간 타이머 멈추기
    }

    private static void showAutoCloseDialog(String message) {
        JDialog dialog = new JDialog(frame, "알림", true); // 모달 대화상자 생성
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(frame); // 화면 중앙에 위치
        dialog.add(new JLabel(message, SwingConstants.CENTER));

        // 0.3초 후에 다이얼로그 자동으로 닫기
        new Timer(300, e -> dialog.dispose()).start();

        dialog.setVisible(true); // 다이얼로그 표시
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LightGame::createAndShowGUI);
    }
}
