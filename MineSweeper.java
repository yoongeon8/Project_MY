package MWProject;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class MineSweeper {
	private String mode;
    private class MineTile extends JButton {
        int r;
        int c;

        public MineTile(int r, int c) {
            this.r = r;
            this.c = c;
            this.setBackground(Color.decode("#CCFF90"));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (this.getText().equals("🚩")) {
                this.setForeground(Color.decode("#FF5252"));
            }
        }
        
    }

    int tileSize = 60;
    int numRows;
    int numCols;
    int boardWidth = 900;
    int boardHeight = 900;

    JFrame frame = new JFrame("지뢰 찾기");
    JLabel textLabel = new JLabel();
    JLabel timerLabel = new JLabel("Time: 0s");
    JLabel bestTimeLabel = new JLabel("Best Time: 0s");
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JButton replaybutton = new JButton("restart");
    
    int MineCount;
    MineTile[][] board;
    ArrayList<MineTile> mineList;
    Random random = new Random();

    int tilesClicked = 0;
    boolean gameOver = false;

    private Timer timer;
    private int secondsElapsed = 0;
    private int bestTime = Integer.MAX_VALUE;

    private App app;

    public MineSweeper(App app, String mode) {
        this.app = app;

        // 모드에 따라 행, 열, 지뢰 개수 설정
        switch (mode) {
            case "EASY":
                numRows = 10;
                numCols = 10;
                MineCount = 10;
                break;
            case "HARD":
                numRows = 18;
                numCols = 18;
                MineCount = numRows + numCols - 2;
                break;
            default: // NORMAL
                numRows = 15;
                numCols = 15;
                MineCount = numRows + numCols - 2;
                break;
        }
        board = new MineTile[numRows][numCols];
        
        this.bestTime = loadBestTime(mode);
        updateBestTimeLabel(mode);
        
        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textLabel.setFont(new Font("Arial", Font.BOLD, 25));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Minesweeper: " + MineCount);
        textLabel.setOpaque(true);

        timerLabel.setFont(new Font("Arial", Font.BOLD, 25));
        timerLabel.setHorizontalAlignment(JLabel.CENTER);

        bestTimeLabel.setFont(new Font("Arial", Font.BOLD, 25));
        bestTimeLabel.setHorizontalAlignment(JLabel.RIGHT);
        
        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel, BorderLayout.NORTH);
        textPanel.add(timerLabel, BorderLayout.CENTER);
        textPanel.add(bestTimeLabel, BorderLayout.SOUTH);
        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(numRows, numCols));
        boardPanel.setBackground(Color.DARK_GRAY);
        frame.add(boardPanel);

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;

                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 40));

                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) {
                            return;
                        }

                        MineTile tile = (MineTile) e.getSource();

                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (tile.getText().equals("")) {
                                if (mineList.contains(tile)) {
                                    revealMines();
                                } else {
                                    checkMine(tile.r, tile.c);
                                }
                            }
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            if (tile.getText().equals("") && tile.isEnabled()) {
                                tile.setText("🚩");
                            } else if (tile.getText().equals("🚩")) {
                                tile.setText("");
                            }
                        }
                    }
                });
                boardPanel.add(tile);
            }
        }

        frame.setVisible(true);
        setMines();
        startTimer();
    }
    void startTimer() { //타이머 시작하는 메서드
        if (timer == null) {
            timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!gameOver) {
                        secondsElapsed++;
                        timerLabel.setText("Time: " + secondsElapsed + "s");
                    }
                }
            });
            timer.start();
        }
    }

    void stopTimer() { //타이머 멈추개하는 메서드
        if (timer != null) {
            timer.stop();
        }
    }
    
    void setMines() { //지뢰의 위치를 정해두는 메서드
        mineList = new ArrayList<>();
        int mineLeft = MineCount;
        while (mineLeft > 0) {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numCols);

            MineTile tile = board[r][c];
            if (!mineList.contains(tile)) {
                mineList.add(tile);
                mineLeft--;
            }
        }
    }

    void revealMines() { //지뢰를 찾았을 경우
        for (MineTile tile : mineList) {
            tile.setText("💣");
        }

        gameOver = true;
        textLabel.setText("Game Over");
        stopTimer();
        }

    void checkMine(int r, int c) { //지뢰의 위치를 체크하는 메서드
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
            return;
        }

        MineTile tile = board[r][c];
        if (!tile.isEnabled()) {
            return;
        }
        tile.setBackground(Color.decode("#ECEFF1"));
        tile.setEnabled(false);
        tilesClicked++;

        int minesFound = 0;

        //top 3
        minesFound += countMine(r - 1, c - 1);
        minesFound += countMine(r - 1, c);
        minesFound += countMine(r - 1, c + 1);
        // left and right
        minesFound += countMine(r, c - 1);
        minesFound += countMine(r, c + 1);
        //bottom 3
        minesFound += countMine(r + 1, c - 1);
        minesFound += countMine(r + 1, c);
        minesFound += countMine(r + 1, c + 1);

        if (minesFound > 0) {
            tile.setText(Integer.toString(minesFound));
        } else {
            tile.setText("");
            checkMine(r - 1, c - 1);
            checkMine(r - 1, c);
            checkMine(r - 1, c + 1);
            checkMine(r, c - 1);
            checkMine(r, c + 1);
            checkMine(r + 1, c - 1);
            checkMine(r + 1, c);
            checkMine(r + 1, c + 1);
        }

        if (tilesClicked == numRows * numCols - mineList.size()) {
            gameOver = true;
            textLabel.setText("Mines Cleared!");
            stopTimer();
			
            if (secondsElapsed < bestTime) {
                bestTime = secondsElapsed;
                saveBestTimeToFile(mode, bestTime);
                updateBestTimeLabel(mode);
            }
            
            app.onMinesweeperWin(); //app클래스에 게임을 승리했다는 내용 넣음.
        }
	}

    void saveBestTimeToFile(String mode, int time) {
        try {
            // 기존 데이터를 읽어와 수정
            File file = new File("game_time.txt");
            ArrayList<String> lines = new ArrayList<>();

            // 파일이 존재하지 않을 경우 초기화
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        lines.add(line);
                    }
                }
            }

            // 각 모드별 시간 갱신
            while (lines.size() < 3) {
                lines.add("N/A"); // 모드별 초기 값 추가
            }

            switch (mode) {
                case "NORMAL":
                    lines.set(0, Integer.toString(time));
                    break;
                case "EASY":
                    lines.set(1, Integer.toString(time));
                    break;
                case "HARD":
                    lines.set(2, Integer.toString(time));
                    break;
            }

            // 파일에 다시 쓰기
            try (FileWriter writer = new FileWriter(file)) {
                for (String line : lines) {
                    writer.write(line + "\n");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int loadBestTime(String mode) {
        try (BufferedReader reader = new BufferedReader(new FileReader("game_time.txt"))) {
            ArrayList<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            switch (mode) {
                case "NORMAL":
                    return lines.size() > 0 && !lines.get(0).equals("N/A") ? Integer.parseInt(lines.get(0)) : Integer.MAX_VALUE;
                case "EASY":
                    return lines.size() > 1 && !lines.get(1).equals("N/A") ? Integer.parseInt(lines.get(1)) : Integer.MAX_VALUE;
                case "HARD":
                    return lines.size() > 2 && !lines.get(2).equals("N/A") ? Integer.parseInt(lines.get(2)) : Integer.MAX_VALUE;
            }

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return Integer.MAX_VALUE;
    }

    void updateBestTimeLabel(String mode) {
        int time = loadBestTime(mode);
        if (time == Integer.MAX_VALUE) {
            bestTimeLabel.setText("Best Time (" + mode + "): N/A");
        } else {
            bestTimeLabel.setText("Best Time (" + mode + "): " + time + "s");
        }
    }

    int countMine(int r, int c) { //지뢰를 개수를 계산하는 메서드
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
            return 0;
        }
        if (mineList.contains(board[r][c])) {
            return 1;
        }
        return 0;
    }

    public static void main(String[] args) {
        new MineSweeper(null, null);
    }
}
