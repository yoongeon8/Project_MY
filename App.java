package MWProject;

import javax.swing.*;

public class App {
    private MineSweeper minesweeper;

    public App() {
        // StartMenu 실행
        showStartMenu();
    }

    // StartMenu를 보여주는 메서드
    private void showStartMenu() {
        SwingUtilities.invokeLater(() -> {
            StartMemu startMenu = new StartMemu(selectedMode -> {
                // StartMenu에서 선택된 모드를 전달받아 Minesweeper 게임 시작
                System.out.println("Selected Mode: " + selectedMode);
                startMinesweeper(selectedMode);
            });
            startMenu.showMenu();
        });
    }

    // Minesweeper 게임 시작 메서드
    public void startMinesweeper(String mode) {
        new MineSweeper(this, mode);
    }

    // Minesweeper에서 승리 시 호출되는 메서드
    public void onMinesweeperWin() {
        int choice = (int) (Math.random() * 3); // 0부터 2까지 랜덤 값 선택

        switch (choice) {
            case 0 -> {
                System.out.println("LineDots를 띄웁니다.");
                SwingUtilities.invokeLater(LineDots::createAndShowGUI);
            }
            case 1 -> {
                System.out.println("LightGame을 띄웁니다.");
                SwingUtilities.invokeLater(LightGame::createAndShowGUI);
            }
            case 2 -> {
                System.out.println("BoomDefuse를 띄웁니다.");
                SwingUtilities.invokeLater(BoomDefuse::createAndShowGUI);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::new);
    }
}
