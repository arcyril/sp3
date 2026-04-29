import gui.Gui;
import gui.GuiLogic;

public class App {
    public static void main(String[] args) {
        Gui mainWindow = new Gui();
        new GuiLogic(mainWindow);
        mainWindow.setVisible(true);
    }
}