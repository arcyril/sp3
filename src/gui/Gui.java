package gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JProgressBar;

@SuppressWarnings("serial")
public class Gui extends JFrame {

    JButton btnStart;
    JButton btnPause;
    JButton btnStop;

    JTextField txtTrvanie;
    JTextField txtReplikacii;
    JTextField txtZahrievanie;
    JCheckBox chckBoxTurboRezim;
    JCheckBox chckBoxSledovatZahrievanie;
    JTextField txtPocetLekarov;
    JTextField txtPocetSestier;
    JCheckBox chckBoxRezim1Aktivny;

    JRadioButton rdioBttonImgType1;
    JRadioButton rdioBttonImgType2;

    JLabel lblResult;
    JLabel lblSimTime;

    JSlider sliderSimDur;
    JSlider sliderSimInt;
    JButton btnSimMaxSpeed;

    JSlider sliderAnimDur;
    JSlider sliderAnimInt;
    JButton btnAnimMaxSpeed;

    JCheckBox chckBoxCreateAnimAfterStart;
    JButton btnCreateAnim;
    JButton btnRemoveAnim;

    DefaultTableModel tableModel;
    JTable tablePacienti;
    JProgressBar progressBar;

    public Gui() {
        setTitle("Simulácia Urgentného Príjmu");
        setSize(1600, 800);
        setMinimumSize(new Dimension(1100, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        buidGui();
        setValues();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
    }

    private void setValues() {
        txtTrvanie.setText("2419200");
        txtReplikacii.setText("20");
        txtPocetLekarov.setText("5");
        txtPocetSestier.setText("10");
        chckBoxRezim1Aktivny.setSelected(true);
        txtZahrievanie.setText("0"); 
        chckBoxTurboRezim.setSelected(false);
        sliderSimDur.setValue(1);
        sliderSimInt.setValue(500);
    }

    private void buidGui() {
        Container container = getContentPane();
        setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));

        container.add(buidTopPanel());
        container.add(buidBottomPanel());
    }

    private JPanel createPanel(float xAligment, float yAligment, int layOut) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, layOut));
        panel.setAlignmentX(xAligment);
        panel.setAlignmentY(yAligment);
        return panel;
    }

    private JPanel buidTopPanel() {
        JPanel panel = createPanel(0f, 0f, BoxLayout.LINE_AXIS);

        btnStart = new JButton("Start");
        panel.add(btnStart);
        panel.add(Box.createRigidArea(new DimensionUIResource(10, 0)));

        btnPause = new JButton("Pauza");
        panel.add(btnPause);
        panel.add(Box.createRigidArea(new DimensionUIResource(10, 0)));

        btnStop = new JButton("Stop");
        panel.add(btnStop);
        panel.add(Box.createRigidArea(new DimensionUIResource(10, 0)));

        JPanel vstupyPanel = createPanel(0f, 0.5f, BoxLayout.PAGE_AXIS);
        vstupyPanel.add(createTextInputPanel());
        vstupyPanel.add(Box.createRigidArea(new DimensionUIResource(0, 5)));
        vstupyPanel.add(createAnimObjectType());

        panel.add(vstupyPanel);
        panel.add(Box.createRigidArea(new DimensionUIResource(5, 0)));

        JPanel vystupPanel = createPanel(0f, 0.5f, BoxLayout.PAGE_AXIS);

        JPanel simTimePanel = createPanel(0f, 0f, BoxLayout.LINE_AXIS);
        simTimePanel.add(Box.createRigidArea(new DimensionUIResource(12, 0)));
        lblSimTime = new JLabel("Simulacny cas:");
        lblSimTime.setFont(new Font("SansSerif", Font.BOLD, 22));
        simTimePanel.add(lblSimTime);
        simTimePanel.add(Box.createRigidArea(new DimensionUIResource(20, 0)));
        vystupPanel.add(simTimePanel);
        vystupPanel.add(Box.createRigidArea(new DimensionUIResource(0, 5)));
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(300, 25));
        progressBar.setMaximumSize(new Dimension(300, 25));
        vystupPanel.add(progressBar);

        lblResult = new JLabel("<html><br>Čaká sa na simuláciu...</html>");
        lblResult.setFont(new Font("SansSerif", Font.PLAIN, 14));
        vystupPanel.add(lblResult);
        panel.add(Box.createRigidArea(new DimensionUIResource(0, 15)));

        panel.add(vystupPanel);
        return panel;
    }

    private JPanel createTextInputPanel() {
        JPanel panel = createPanel(0.5f, 0f, BoxLayout.LINE_AXIS);

        panel.add(new JLabel("Trvanie simulacie:"));
        panel.add(Box.createRigidArea(new DimensionUIResource(5, 0)));

        txtTrvanie = new JTextField();
        txtTrvanie.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        txtTrvanie.setPreferredSize(new Dimension(100, 20));
        txtTrvanie.setMaximumSize(new Dimension(100, 20));
        panel.add(txtTrvanie);
        panel.add(Box.createRigidArea(new DimensionUIResource(5, 0)));

        panel.add(new JLabel("Pocet replikacii:"));
        panel.add(Box.createRigidArea(new DimensionUIResource(5, 0)));

        txtReplikacii = new JTextField();
        txtReplikacii.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        txtReplikacii.setPreferredSize(new Dimension(70, 20));
        txtReplikacii.setMaximumSize(new Dimension(70, 20));
        panel.add(txtReplikacii);

        panel.add(Box.createRigidArea(new DimensionUIResource(10, 0)));

        panel.add(new JLabel("Pocet lekarov:"));
        panel.add(Box.createRigidArea(new DimensionUIResource(5, 0)));

        txtPocetLekarov = new JTextField();
        txtPocetLekarov.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        txtPocetLekarov.setPreferredSize(new Dimension(70, 20));
        txtPocetLekarov.setMaximumSize(new Dimension(70, 20));
        panel.add(txtPocetLekarov);

        panel.add(Box.createRigidArea(new DimensionUIResource(10, 0)));

        panel.add(new JLabel("Pocet sestier:"));
        panel.add(Box.createRigidArea(new DimensionUIResource(5, 0)));

        txtPocetSestier = new JTextField();
        txtPocetSestier.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        txtPocetSestier.setPreferredSize(new Dimension(70, 20));
        txtPocetSestier.setMaximumSize(new Dimension(70, 20));
        panel.add(txtPocetSestier);

        panel.add(Box.createRigidArea(new DimensionUIResource(10, 0)));

        panel.add(new JLabel("Zahrievanie (s):"));
        panel.add(Box.createRigidArea(new DimensionUIResource(5, 0)));

        txtZahrievanie = new JTextField();
        txtZahrievanie.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        txtZahrievanie.setPreferredSize(new Dimension(70, 20));
        txtZahrievanie.setMaximumSize(new Dimension(70, 20));
        panel.add(txtZahrievanie);

        return panel;
    }

    private JPanel createAnimObjectType() {
        JPanel panel = createPanel(0.5f, 0f, BoxLayout.LINE_AXIS);

        rdioBttonImgType1 = new JRadioButton("Obrázok Typ 1 (Samostatne)");
        panel.add(rdioBttonImgType1);
        rdioBttonImgType1.setSelected(true);

        panel.add(Box.createRigidArea(new DimensionUIResource(5, 0)));

        rdioBttonImgType2 = new JRadioButton("Obrázok Typ 2 (Sanitka)");
        panel.add(rdioBttonImgType2);

        ButtonGroup group = new ButtonGroup();
        group.add(rdioBttonImgType1);
        group.add(rdioBttonImgType2);

        return panel;
    }

    private JPanel buidBottomPanel() {
        JPanel panel = createPanel(0f, 0f, BoxLayout.LINE_AXIS);
        JPanel leftPanel = createPanel(0f, 0f, BoxLayout.PAGE_AXIS);

        chckBoxRezim1Aktivny = new JCheckBox("Rezim 1 aktivny");
        chckBoxRezim1Aktivny.setAlignmentX(0.5f);
        leftPanel.add(chckBoxRezim1Aktivny);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 3)));

        chckBoxTurboRezim = new JCheckBox("Turbo Režim");
        chckBoxTurboRezim.setAlignmentX(0.5f);
        leftPanel.add(chckBoxTurboRezim);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        chckBoxSledovatZahrievanie = new JCheckBox("Analýza Zahrievania (CSV)");
        panel.add(chckBoxSledovatZahrievanie);
        panel.add(Box.createRigidArea(new DimensionUIResource(10, 0)));

        JLabel lblSimDur = new JLabel("Sim. trvanie pauzy");
        lblSimDur.setAlignmentX(0.5f);
        leftPanel.add(lblSimDur);

        sliderSimDur = new JSlider(JSlider.HORIZONTAL, 1, 10, 10);
        sliderSimDur.setMajorTickSpacing(2);
        sliderSimDur.setPaintTicks(true);
        sliderSimDur.setPaintLabels(true);
        sliderSimDur.setMaximumSize(new Dimension(150, 50));
        leftPanel.add(sliderSimDur);

        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JLabel lblSimInt = new JLabel("Sim. interval mdzi pauzami");
        lblSimInt.setAlignmentX(0.5f);
        leftPanel.add(lblSimInt);

        sliderSimInt = new JSlider(JSlider.HORIZONTAL, 1, 1000, 100);
        sliderSimInt.setMajorTickSpacing(200);
        sliderSimInt.setPaintTicks(true);
        sliderSimInt.setPaintLabels(true);
        sliderSimInt.setMaximumSize(new Dimension(150, 50));
        leftPanel.add(sliderSimInt);

        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        btnSimMaxSpeed = new JButton("Sim. max. speed");
        btnSimMaxSpeed.setAlignmentX(0.5f);
        leftPanel.add(btnSimMaxSpeed);

        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel lblAnimDur = new JLabel("Anim. trvanie interevalu");
        lblAnimDur.setAlignmentX(0.5f);
        leftPanel.add(lblAnimDur);

        sliderAnimDur = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);
        sliderAnimDur.setMajorTickSpacing(20);
        sliderAnimDur.setPaintTicks(true);
        sliderAnimDur.setPaintLabels(true);
        sliderAnimDur.setMaximumSize(new Dimension(150, 50));
        leftPanel.add(sliderAnimDur);

        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        chckBoxCreateAnimAfterStart = new JCheckBox("Vytvor Anim. po starte");
        chckBoxCreateAnimAfterStart.setAlignmentX(0.5f);
        chckBoxCreateAnimAfterStart.setVisible(false);
        leftPanel.add(chckBoxCreateAnimAfterStart);

        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        btnCreateAnim = new JButton("Vytvor Animator");
        btnCreateAnim.setAlignmentX(0.5f);
        leftPanel.add(btnCreateAnim);

        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        btnRemoveAnim = new JButton("Vymaz Animator");
        btnRemoveAnim.setAlignmentX(0.5f);
        leftPanel.add(btnRemoveAnim);

        panel.add(leftPanel);

        //** LLM usage, to specify
        String[] stlpce = {"ID Pacienta", "Typ", "Aktuálny Stav", "Čas Príchodu"};
        tableModel = new DefaultTableModel(stlpce, 0);
        tablePacienti = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tablePacienti);
        scrollPane.setAlignmentY(0f);
        panel.add(scrollPane);

        return panel;
    }
}