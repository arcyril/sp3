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
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JProgressBar;
import java.awt.BorderLayout;

@SuppressWarnings("serial")
public class Gui extends JFrame {

    JButton btnStart;
    JButton btnPause;
    JButton btnStop;

    JTextField txtTrvanie;
    JTextField txtReplikacii;
    JTextField txtZahrievanie;
    JCheckBox chckBoxTurboRezim;
    JCheckBox chckBoxRezervSestruAmbulanciuB;
    JCheckBox chckBoxSledovatZahrievanie;
    JCheckBox chckBoxMinPocet;
    JTextField txtPocetLekarov;
    JTextField txtPocetSestier;
    JRadioButton rbRezim1;
    JRadioButton rbRezim2;
    JRadioButton rbRezim3;
    JRadioButton rbRezim5;
    ButtonGroup bgRezim;
    JCheckBox chckBoxRezim1Aktivny;

    // JRadioButton rdioBttonImgType1;
    // JRadioButton rdioBttonImgType2;

    JLabel lblResult;
    JLabel lblSimTime;

    JSlider sliderSimDur;
    JSlider sliderSimInt;
    JButton btnSimMaxSpeed;

    JSlider sliderAnimDur;
    JSlider sliderAnimInt;
    JButton btnAnimMaxSpeed;

    JCheckBox chckBoxZobrazitPriebeh;
    // JCheckBox chckBoxCreateAnimAfterStart;
    // JButton btnCreateAnim;
    // JButton btnRemoveAnim;


    DefaultTableModel tableModel;
    DefaultTableModel tableAmbulancieModel;
    JTable tableAmbulancie;
    JTable tablePacienti;
    JProgressBar progressBar;

    JPanel animatorPanel;
    javax.swing.JTabbedPane tabbedPane;

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
        txtTrvanie.setText("672");
        txtReplikacii.setText("20");
        txtPocetLekarov.setText("7");
        txtPocetSestier.setText("9");
        txtZahrievanie.setText("320"); 
        chckBoxTurboRezim.setSelected(false);
        chckBoxRezervSestruAmbulanciuB.setSelected(false);
        sliderSimDur.setValue(1);
        sliderSimInt.setValue(500);
    }

    private void buidGui() {
        Container container = getContentPane();
        // setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
        // container.add(buidTopPanel());
        // container.add(buidBottomPanel());
        container.setLayout(new java.awt.BorderLayout());
        container.add(buidBottomPanel(), java.awt.BorderLayout.CENTER);
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
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 10));

        JPanel btnPanel = createPanel(0f, 0f, BoxLayout.LINE_AXIS);
        btnStart = new JButton("Start");
        btnPanel.add(btnStart);
        btnPanel.add(Box.createRigidArea(new DimensionUIResource(10, 0)));
        btnPause = new JButton("Pauza");
        btnPanel.add(btnPause);
        btnPanel.add(Box.createRigidArea(new DimensionUIResource(10, 0)));
        btnStop = new JButton("Stop");
        btnPanel.add(btnStop);
        
        panel.add(btnPanel);
        panel.add(Box.createRigidArea(new DimensionUIResource(30, 0)));

        JPanel vstupyPanel = createPanel(0f, 0f, BoxLayout.PAGE_AXIS);
        vstupyPanel.add(createTextInputPanel());
        
        panel.add(vstupyPanel);
        return panel;
    }

    private JPanel createTextInputPanel() {
        JPanel panel = createPanel(0.5f, 0f, BoxLayout.LINE_AXIS);

        panel.add(new JLabel("Trvanie simulacie, hod.:"));
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

        panel.add(new JLabel("Zahrievanie, hod.:"));
        panel.add(Box.createRigidArea(new DimensionUIResource(5, 0)));

        txtZahrievanie = new JTextField();
        txtZahrievanie.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        txtZahrievanie.setPreferredSize(new Dimension(70, 20));
        txtZahrievanie.setMaximumSize(new Dimension(70, 20));
        panel.add(txtZahrievanie);

        return panel;
    }

    // private JPanel createAnimObjectType() {
    //     JPanel panel = createPanel(0.5f, 0f, BoxLayout.LINE_AXIS);

    //     rdioBttonImgType1 = new JRadioButton("Obrázok Typ 1 (Samostatne)");
    //     panel.add(rdioBttonImgType1);
    //     rdioBttonImgType1.setSelected(true);

    //     panel.add(Box.createRigidArea(new DimensionUIResource(5, 0)));

    //     rdioBttonImgType2 = new JRadioButton("Obrázok Typ 2 (Sanitka)");
    //     panel.add(rdioBttonImgType2);

    //     ButtonGroup group = new ButtonGroup();
    //     group.add(rdioBttonImgType1);
    //     group.add(rdioBttonImgType2);

    //     return panel;
    // }

    private JPanel buidBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));
        leftPanel.setPreferredSize(new Dimension(280, 800));

        lblSimTime = new JLabel("Sim. čas: 00:00:00");
        lblSimTime.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblSimTime.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        leftPanel.add(lblSimTime);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setMaximumSize(new Dimension(250, 20));
        progressBar.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        leftPanel.add(progressBar);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel modeGroup = new JPanel();
        modeGroup.setLayout(new BoxLayout(modeGroup, BoxLayout.PAGE_AXIS));
        modeGroup.setBorder(BorderFactory.createTitledBorder("Nastavenia a Režimy"));
        modeGroup.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        modeGroup.setMaximumSize(new Dimension(250, 180));

        // JPanel rezimPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        // rezimPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        // rezimPanel.add(new JLabel("Zvolený Režim: "));
        // txtZvolenyRezim = new JTextField();
        // txtZvolenyRezim.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        // txtZvolenyRezim.setPreferredSize(new Dimension(50, 20));
        // rezimPanel.add(txtZvolenyRezim);
        // modeGroup.add(rezimPanel);
        
        modeGroup.add(Box.createRigidArea(new Dimension(0, 4)));
        chckBoxRezervSestruAmbulanciuB = new JCheckBox("Rezerv. sestru a Ambulanciu B");
        chckBoxRezervSestruAmbulanciuB.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        modeGroup.add(chckBoxRezervSestruAmbulanciuB);
        modeGroup.add(Box.createRigidArea(new Dimension(0, 4)));

        JPanel rezimPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));
        rezimPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        rezimPanel.add(new JLabel("Režim: "));
        
        rbRezim1 = new JRadioButton("1");
        rbRezim2 = new JRadioButton("2");
        rbRezim3 = new JRadioButton("3");
        rbRezim5 = new JRadioButton("5");
        
        bgRezim = new ButtonGroup();
        bgRezim.add(rbRezim1);
        bgRezim.add(rbRezim2);
        bgRezim.add(rbRezim3);
        bgRezim.add(rbRezim5);
        rbRezim1.setSelected(true);
        
        rezimPanel.add(rbRezim1);
        rezimPanel.add(rbRezim2);
        rezimPanel.add(rbRezim3);
        rezimPanel.add(rbRezim5);
        modeGroup.add(rezimPanel);

        chckBoxTurboRezim = new JCheckBox("Turbo Režim");
        chckBoxTurboRezim.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        modeGroup.add(chckBoxTurboRezim);

        chckBoxMinPocet = new JCheckBox("Min. počet analýza");
        chckBoxMinPocet.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        modeGroup.add(chckBoxMinPocet);

        chckBoxSledovatZahrievanie = new JCheckBox("Analýza Zahrievania (CSV)");
        chckBoxSledovatZahrievanie.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        modeGroup.add(chckBoxSledovatZahrievanie);

        leftPanel.add(modeGroup);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel lblSimDur = new JLabel("Sim. trvanie pauzy");
        lblSimDur.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        leftPanel.add(lblSimDur);

        sliderSimDur = new JSlider(JSlider.HORIZONTAL, 1, 10, 10);
        sliderSimDur.setMajorTickSpacing(2);
        sliderSimDur.setPaintTicks(true);
        sliderSimDur.setPaintLabels(true);
        sliderSimDur.setMaximumSize(new Dimension(250, 50));
        sliderSimDur.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        leftPanel.add(sliderSimDur);

        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JLabel lblSimInt = new JLabel("Sim. interval mdzi pauzami");
        lblSimInt.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        leftPanel.add(lblSimInt);

        sliderSimInt = new JSlider(JSlider.HORIZONTAL, 1, 1000, 100);
        sliderSimInt.setMajorTickSpacing(200);
        sliderSimInt.setPaintTicks(true);
        sliderSimInt.setPaintLabels(true);
        sliderSimInt.setMaximumSize(new Dimension(250, 50));
        sliderSimInt.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        leftPanel.add(sliderSimInt);

        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        btnSimMaxSpeed = new JButton("Sim. max. speed");
        btnSimMaxSpeed.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        leftPanel.add(btnSimMaxSpeed);

        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel lblAnimDur = new JLabel("Anim. trvanie interevalu");
        lblAnimDur.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        leftPanel.add(lblAnimDur);

        sliderAnimDur = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);
        sliderAnimDur.setMajorTickSpacing(20);
        sliderAnimDur.setPaintTicks(true);
        sliderAnimDur.setPaintLabels(true);
        sliderAnimDur.setMaximumSize(new Dimension(250, 50));
        sliderAnimDur.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        leftPanel.add(sliderAnimDur);

        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // chckBoxCreateAnimAfterStart = new JCheckBox("Vytvor Anim. po starte");
        // chckBoxCreateAnimAfterStart.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        // chckBoxCreateAnimAfterStart.setVisible(false);
        // leftPanel.add(chckBoxCreateAnimAfterStart);

        // btnCreateAnim = new JButton("Vytvor Animator");
        // btnCreateAnim.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        // leftPanel.add(btnCreateAnim);

        // leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // btnRemoveAnim = new JButton("Vymaz Animator");
        // btnRemoveAnim.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        // leftPanel.add(btnRemoveAnim);

        chckBoxZobrazitPriebeh = new JCheckBox("Zobraziť animáciu / tabuľku");
        chckBoxZobrazitPriebeh.setFont(new Font("SansSerif", Font.BOLD, 13));
        chckBoxZobrazitPriebeh.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        chckBoxZobrazitPriebeh.setSelected(false);
        leftPanel.add(chckBoxZobrazitPriebeh);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        panel.add(leftPanel, BorderLayout.WEST);

        animatorPanel = new JPanel(new BorderLayout());
        JPanel tabulkaPanel = new JPanel(new BorderLayout());
        JPanel gridTabuliek = new JPanel(new java.awt.GridLayout(2, 1));
        
        String[] stlpcePacienti = {"ID Pacienta", "Typ", "Aktuálny Stav", "Čas Príchodu"};
        tableModel = new DefaultTableModel(stlpcePacienti, 0);
        tablePacienti = new JTable(tableModel);
        gridTabuliek.add(new javax.swing.JScrollPane(tablePacienti));

        String[] stlpceAmbulancie = {"Ambulancia", "Stav"};
        tableAmbulancieModel = new DefaultTableModel(stlpceAmbulancie, 0);
        tableAmbulancie = new JTable(tableAmbulancieModel);
        gridTabuliek.add(new javax.swing.JScrollPane(tableAmbulancie));

        tabulkaPanel.add(gridTabuliek, BorderLayout.CENTER);

        lblResult = new JLabel(" ");
        lblResult.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabulkaPanel.add(lblResult, BorderLayout.SOUTH);

        tabbedPane = new javax.swing.JTabbedPane();
        tabbedPane.addTab("Animácia", animatorPanel);
        tabbedPane.addTab("Tabuľka stavov", tabulkaPanel);

        JPanel rightSideWrapper = new JPanel(new BorderLayout());
        rightSideWrapper.add(buidTopPanel(), BorderLayout.NORTH);
        rightSideWrapper.add(tabbedPane, BorderLayout.CENTER);
        panel.add(rightSideWrapper, BorderLayout.CENTER);

        return panel;
    }
}