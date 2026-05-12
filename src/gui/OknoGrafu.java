package gui;

import javax.swing.JFrame;

//** Na Vytvorenie Kódu Bol Použitý LLM */
public class OknoGrafu extends JFrame {
    public PanelGrafu panelGrafu;

    public OknoGrafu() {
        setTitle("Graf Ustaľovania s Intervalom Spoľahlivosti");
        setSize(1000, 700);
        panelGrafu = new PanelGrafu();
        add(panelGrafu);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}