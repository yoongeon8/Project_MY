package MWProject;

import javax.swing.*;
import java.math.*;

public class App {
    private MineSweeper minesweeper;

    public App() {
        showStartMenu();
    }

    private void showStartMenu() {
        SwingUtilities.invokeLater(() -> {
            StartMemu startMenu = new StartMemu(selectedMode -> {
                System.out.println("Selected Mode: " + selectedMode);
                startMinesweeper(selectedMode);
            });
            startMenu.showMenu();
        });
    }

    public void startMinesweeper(String mode) {
        new MineSweeper(this, mode);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::new);
    }
}
