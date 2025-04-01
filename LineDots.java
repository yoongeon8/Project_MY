package MWProject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class LineDots extends JPanel {
    private MineSweeper minesweeper;
    static JFrame frame = new JFrame("LineDots");

    // 고정된 점 좌표
    private int[][] points = {
        {100, 100}, {100, 175}, {100, 225}, {100, 275}, {100, 325}, // 왼쪽 열 점들
        {350, 100}, {350, 175}, {350, 225}, {350, 275}, {350, 325}  // 오른쪽 열 점들
    };

    // 선을 저장할 리스트 (각 선은 두 개의 점 인덱스를 가짐)
    private ArrayList<int[]> lines = new ArrayList<>();
    private ArrayList<Boolean> linesVisible = new ArrayList<>();
    private Color[] fixedColors = {Color.BLUE, Color.RED, Color.YELLOW, Color.GREEN, Color.BLACK};
    private ArrayList<Color> lineColors = new ArrayList<>();

    // 타이머 관련 변수
    private int timeSecond = 8; // 남은 시간
    private Timer timer;
    private boolean gameOver = false; // 게임 종료 여부
    
    static App app;

    public LineDots(App app) {
    	this.app = app;
        Random rand = new Random(); //랜덤 값

        int[] leftIndices = {0, 1, 2, 3, 4};
        ArrayList<Integer> availableRightIndices = new ArrayList<>();
        for (int i = 5; i <= 9; i++) {
            availableRightIndices.add(i);
        }

        ArrayList<Color> availableColors = new ArrayList<>();
        Collections.addAll(availableColors, fixedColors);
        Collections.shuffle(availableColors);

        for (int leftPoint : leftIndices) {
            int randomIndex = rand.nextInt(availableRightIndices.size());
            int rightPoint = availableRightIndices.get(randomIndex);

            lines.add(new int[]{leftPoint, rightPoint});
            linesVisible.add(true);

            Color lineColor = availableColors.remove(0);
            lineColors.add(lineColor);

            availableRightIndices.remove(randomIndex);
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (gameOver) return;

                int mouseX = e.getX();
                int mouseY = e.getY();

                for (int i = 0; i < lines.size(); i++) {
                    if (linesVisible.get(i)) {
                        int[] line = lines.get(i);
                        if (isPointLine(points[line[0]][0], points[line[0]][1],
                                            points[line[1]][0], points[line[1]][1],
                                            mouseX, mouseY)) {
                            linesVisible.set(i, false);
                            repaint();
                        }
                    }
                }
            }
        });

        // 타이머 초기화
        timer = new Timer(800, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameOver) {
                    timer.stop();
                    return;
                }
                timeSecond--;
                if (timeSecond <= 0) {
                    gameOver = true;
                    timer.stop();
                }
                repaint();
            }
        });
        timer.start();
    }

    private boolean isPointLine(int x1, int y1, int x2, int y2, int px, int py) {
        double distance = Math.abs((y2 - y1) * px - (x2 - x1) * py + x2 * y1 - y2 * x1)
                        / Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2));
        return distance < 5;
    }

    private boolean allLinesClear() { //다 클리어 했을시
        for (Boolean visible : linesVisible) {
            if (visible) {
                return false;
            }
        }
        if (!gameOver) {
            gameOver = true;
            timer.stop();
        }
        return true;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 선과 점 그리기
        for (int i = 0; i < lines.size(); i++) {
            if (linesVisible.get(i)) {
                int[] line = lines.get(i);
                Color color = lineColors.get(i);

                g.setColor(color);
                g.drawLine(points[line[0]][0], points[line[0]][1],
                           points[line[1]][0], points[line[1]][1]);

                g.fillOval(points[line[0]][0] - 5, points[line[0]][1] - 5, 10, 10);
                g.fillOval(points[line[1]][0] - 5, points[line[1]][1] - 5, 10, 10);
            }
        }

        // 남은 시간 표시
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 25));
        g.drawString("Time Seconds: " + timeSecond + "s", 10, 20);

        // 결과 메시지 표시
        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 30));
            if (allLinesClear()) {
                g.drawString("Clear!", getWidth() / 2 - 50, getHeight() / 2);
                
                SwingUtilities.invokeLater(() -> {
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
                });
            }
            else {
                g.drawString("You Lose!", getWidth() / 2 - 70, getHeight() / 2);
                
                SwingUtilities.invokeLater(() -> {
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
                });
            }
        }
    }

    public static void createAndShowGUI() {
    	ImageIcon image = new ImageIcon("imgs/line.jpg");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 475);
        frame.add(new LineDots(app));
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setIconImage(image.getImage());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LineDots::createAndShowGUI);
    }
}
