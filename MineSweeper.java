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
	public class RoundedButton extends JButton {

	    public RoundedButton(String text) {
	        super(text);
	        setFont(new Font("Arial", Font.BOLD, 20));
	        setFocusPainted(false);
	        setContentAreaFilled(false);
	        setOpaque(false);
	        setForeground(new Color(220, 220, 220));
	    }

	    @Override
	    protected void paintComponent(Graphics g) {
	        Graphics2D g2 = (Graphics2D) g.create();
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	        g2.setColor(new Color(0, 0, 70));
	        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);

	        super.paintComponent(g);
	        g2.dispose();
	    }

	    @Override
	    protected void paintBorder(Graphics g) {
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
    RoundedButton replaybutton = new RoundedButton("restart");
    
    ImageIcon image = new ImageIcon("imgs/landmine.png");
    
    int MineCount;
    MineTile[][] board;
    ArrayList<MineTile> mineList;
    Random random = new Random();

    int tilesClicked = 0;
    boolean gameOver = false;

    private Timer timer;
    private int secondsElapsed = 0;
    private int bestTime = Integer.MAX_VALUE;

    App app;
    LightGame game;
    LineDots ld;
    BoomDefuse bdf;

    public MineSweeper(App app, String mode) {
        this.app = app;
        this.mode = mode;

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
        
        frame.setIconImage(image.getImage());
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
        textPanel.add(replaybutton, BorderLayout.EAST);

        boardPanel.setLayout(new GridLayout(numRows, numCols));
        boardPanel.setBackground(Color.DARK_GRAY);
        frame.add(boardPanel);
        
        replaybutton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        replaybutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });
        replaybutton.setPreferredSize(new Dimension(120, 45));

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
    void startTimer() {
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

    void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }
    
    void setMines() {
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
    
    void revealMines() {
        for (MineTile tile : mineList) {
            tile.setText("💣");
        }

        gameOver = true;
        textLabel.setText("Game Over");
        stopTimer();
        
        String[] responses = {"예","아니요"};
        ImageIcon icon = new ImageIcon(" ");
        int result = JOptionPane.showOptionDialog(null, "게임을 계속하시겠습니까?", "quetion", JOptionPane.YES_NO_OPTION, 
        		JOptionPane.QUESTION_MESSAGE, icon, responses, 0);
        if (result == JOptionPane.YES_OPTION) {
            StartMemu startMenu = new StartMemu(mode -> {
                MineSweeper newGame = new MineSweeper(app,mode);
            });
            startMenu.showMenu();
            frame.dispose();
        } 
        else if (result == JOptionPane.NO_OPTION) {
            frame.dispose();
        } 
        else {
            frame.dispose();
        }
        }

    void checkMine(int r, int c) {
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
            
            saveAllTime(mode, secondsElapsed);
            if (secondsElapsed < bestTime) {
                bestTime = secondsElapsed;
                saveBestTimeToFile(mode, bestTime);
                updateBestTimeLabel(mode);
            }
            int choice = (int)(Math.random()*3);
            switch (choice) {
                case 0:
                    System.out.println("LineDots를 띄웁니다.");
                    SwingUtilities.invokeLater(LineDots::createAndShowGUI);
                break;
                case 1:
                    System.out.println("LightGame을 띄웁니다.");
                    SwingUtilities.invokeLater(LightGame::createAndShowGUI);
                break;
                case 2:
                    System.out.println("BoomDefuse를 띄웁니다.");
                    SwingUtilities.invokeLater(BoomDefuse::createAndShowGUI);
                break;
                default: System.out.print("error");
            }
            frame.dispose();
        }
	}
    void saveAllTime(String mode, int time) {
        try {
            File file = new File("times.txt");
            try (FileWriter writer = new FileWriter(file, true);
                 BufferedWriter bufferedWriter = new BufferedWriter(writer)) {

                bufferedWriter.write(mode + ": " + time);
                bufferedWriter.newLine(); // 줄 바꿈 추가
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    void saveBestTimeToFile(String mode, int time) {
        try {
            File file = new File("game_time.txt");
            ArrayList<String> lines = new ArrayList<>();

            if (!file.exists()) {
                lines.add("N/A");
                lines.add("N/A");
                lines.add("N/A");
            } else {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        lines.add(line);
                    }
                }
            }

            while (lines.size() < 3) {
                lines.add("N/A");
            }
            switch (mode) {
                case "EASY":
                    lines.set(0, Integer.toString(time));
                    break;
                case "NORMAL":
                    lines.set(1, Integer.toString(time));
                    break;
                case "HARD":
                    lines.set(2, Integer.toString(time));
                    break;
            }

            try (FileWriter writer = new FileWriter(file, false)) {
                for (String line : lines) {
                    writer.write(line + "\n");
                }
            }
            System.out.println("파일 저장 완료!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    int loadBestTime(String mode) {
        try {
            File file = new File("game_time.txt");
            ArrayList<String> lines = new ArrayList<>();
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        lines.add(line);
                    }
                }
            }

            while (lines.size() < 3) {
                lines.add("N/A");
            }

            switch (mode) {
                case "EASY":
                    return !lines.get(0).equals("N/A") ? Integer.parseInt(lines.get(0)) : Integer.MAX_VALUE;
                case "NORMAL":
                    return !lines.get(1).equals("N/A") ? Integer.parseInt(lines.get(1)) : Integer.MAX_VALUE;
                case "HARD":
                    return !lines.get(2).equals("N/A") ? Integer.parseInt(lines.get(2)) : Integer.MAX_VALUE;
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
        }
        else {
            bestTimeLabel.setText("Best Time (" + mode + "): " + time + "s");
        }
    }

    int countMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
            return 0;
        }
        if (mineList.contains(board[r][c])) {
            return 1;
        }
        return 0;
    }
    void restartGame(){
        frame.dispose();
        StartMemu startMenu = new StartMemu(mode -> {
            MineSweeper newGame = new MineSweeper(app,mode);
        });
        startMenu.showMenu();
    }
    public void ShowDialog() {
        String[] responses = {"예","아니요"};
        ImageIcon icon = new ImageIcon(" ");
        int result = JOptionPane.showOptionDialog(null, "게임을 계속하시겠습니까?", "quetion", JOptionPane.YES_NO_OPTION, 
        		JOptionPane.QUESTION_MESSAGE, icon, responses, 0);
        if (result == JOptionPane.YES_OPTION) {
            StartMemu startMenu = new StartMemu(mode -> {
                MineSweeper newGame = new MineSweeper(app,mode);
            });
            startMenu.showMenu();
            frame.dispose();
        }
        else if (result == JOptionPane.NO_OPTION) {
            frame.dispose();
        }
        else {
            frame.dispose();
        }
    }
    public static void main(String[] args) {
        new MineSweeper(null, null);
    }
}
