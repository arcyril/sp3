package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Method;
import java.util.EventObject;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import simulation.MySimulation;
import OSPABA.Simulation;

// LLM bol použitý na odstraňovanie chýb a implementáciu niektorých funkcií v GUI
public class GuiLogic implements ActionListener, ChangeListener, ItemListener {

    public Gui _gui;
    private SimThread _simThread;
    private MySimulation _mySim;
    private javax.swing.JFrame logFrame;
    private javax.swing.JTextArea logArea;
    private boolean _simMaxSpeedPressed = false;
    private boolean _animMaxSpeedPressed = false;


    private class Prepravka {
        public Object[] data = null;
    }

    public GuiLogic(Gui gui) {
        _gui = gui;
        _mySim = new MySimulation();

        gui.btnStart.addActionListener(this);
        gui.btnPause.addActionListener(this);
        gui.btnStop.addActionListener(this);
        _gui.btnHelpRezim1.addActionListener(e -> showHelp("Režim 1", "Tento režim zaručuje, že pacienti s vysokou prioritou budú ošetrení okamžite. Aj keby v systéme čakali na vyšetrenie ďalší pacienti s rovnakou prioritou, systém má prednostne ošetriť tých, ktorí sú v systéme najdlhšie. Ošetrenie pacientov s nižšou prioritou si naopak vyžaduje väčšiu dostupnosť voľných zdrojov. Tento režim vychádza z predpokladu, že pacienti s vysokou prioritou sú tí, ktorých je potrebné ošetriť ihneď, ak má liečba mať vôbec šancu na úspech.\n" + //
                        "Ošetrenie:\n" + //
                        "-Priorita 1 a 2: Vyžaduje 1 lekára, 1 sestru a miestnosť A\n" + //
                        "-Priorita 3 a 4: Vyžaduje 2 lekárov a 2 sestry. Uprednostňuje sa miestnosť B. Ak je obsadená, prejde sa do miestnosti A.\n" + //
                        "-Priorita 5: Vyžaduje 3 lekárov, 3 sestry a miestnosť B.\n" + //
                        "Vstupné vyšetrenie:\n" + //
                        "-Pacienti radSantikou majú prednosť (ak nie sú žiadni pacienti s prioritou 1 a 2), pokiaľ je k dispozícii 1 zdravotná sestra a izba B\n" + //
                        "-Pacienti radSamostatne môžu byť ošetrení len v prípade, ak sú k dispozícii aspoň 2 zdravotné sestry."));
        _gui.btnHelpRezim2.addActionListener(e -> showHelp("Režim 2", "Tento režim je vhodnejší na minimalizáciu celkovej dĺžky čakacích radov a čakacieho času. Vďaka tomu, že systém prideľuje každého dostupného zamestnanca k každému pacientovi, dokáže ošetriť viac pacientov súčasne.\n" + //
                        "Ošetrenie:\n" + //
                        "-Priority 1 až 5: Ošetrenie sa spustí ihneď, ako je dostupný 1 voľný lekár a 1 voľná sestra.\n" + //
                        "-Systém sa vždy najskôr pokúsi alokovať zdroje pre rad čakajúcich na ošetrenie\n."  + //
                        "Ak je k dispozícii pacient a základný personál, ošetrenie sa spustí bez ohľadu na to, čo sa deje v  čakárni pred vstupnym vyšetrenim.\n" + //
                        "Vstupné vyšetrenie:\n" + //
                        "-Prichádza na rad až vtedy, keď sa nedá spustiť ošetrenie žiadneho pacienta.\n" + //
                        "-Pacienti radSamostatne sú vybavovaní ako poslední, pričom požiadavky na zdroje sú rovnako minimalizované\n"  + //
                        "(vyžaduje sa len 1 voľná sestra, na rozdiel od iných režimov, ktoré vyžadujú 2)."));
        _gui.btnHelpRezim3.addActionListener(e -> showHelp("Režim 3", "Ošetrenie:\n" + //
                        "-Priorita 1, 2 a 3: Majú absolútnu prednosť pred akýmkoľvek vstupným vyšetrením.\n" + //
                        "-Priorita 4 a 5: Sú odsunuté na vedľajšiu koľaj v prípade, že je prítomná sanitka..\n" + //
                        "Vstupné vyšetrenie:\n" + //
                        "-Pacienti radSantikou majú nadradené postavenie. Akonáhle systém odbaví priority 1 až 3,\n"  + //
                        "okamžite prejde na vstupné vyšetrenie sanitiek, predbiehajú pacientov s prioritou 4 a 5, ktorí čakajú na ošetrenie.\n" + //
                        "-Pacienti radSamostatne nemajú túto výhodu. Sú spracovaní až na samom konci rozhodovacieho cyklu\n"  + //
                        "(keď sa nedá spustiť žiadne ošetrenie ani príjem sanitky) a vyžadujú prítomnosť aspoň 2 voľných sestier."));
        _gui.btnHelpRezim5.addActionListener(e -> showHelp("Režim 5", "Ošetrenie:\n" + //
                        "-Priorita 1 a 2: Má absolútnu prioritu.\n" + //
                        "-Priorita 3 a 4: Prispôsobuje sa záťaži. V štandardnom režime vyžaduje plný aby boli voľných 2 lekárov a 2 sestry. Ak je detegovaný pretlak, potrebuju voľných 1 lekár a 1 sestra.\n" + //
                        "-Priorita 5: Vždy vyžaduje 3 voľných lekárov a 3 voľni sestry.\n" + //
                        "Vstupné vyšetrenie:\n" + //
                        "-Realizuje sa len vtedy, ak je rad na ošetrenie prázdny, alebo chýbajú zdroje na ošetrenie aktuálneho pacienta.\n" + //
                        "-Pacienti radSantikou majú klasickú prednosť pred samostatne prichádzajúcimi\n" + //
                        "-Pacienti radSamostatne sú vybavovaní poslední a ich príjem je blokovaný, pokiaľ systém nedisponuje aspoň 2 voľnými sestrami."));

        gui.btnSimMaxSpeed.addActionListener(this);
    }

    private boolean callActionMethod(EventObject ae, JComponent component, String methodName) {
        if (ae.getSource() == component) {
            try {
                Method method = getClass().getDeclaredMethod(methodName, new Class[]{});
                method.invoke(this, new Object[]{});
                return true;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (callActionMethod(ae, _gui.btnStart, "btnStart")) return;
        if (callActionMethod(ae, _gui.btnPause, "btnPause")) return;
        if (callActionMethod(ae, _gui.btnStop, "btnStop")) return;
        if (callActionMethod(ae, _gui.btnSimMaxSpeed, "btnSimMaxSpeed")) return;
    }

    @Override
    public void stateChanged(ChangeEvent che) {
        if (callActionMethod(che, _gui.sliderSimInt, "sliderSimInt")) return;
    }

    @Override
    public void itemStateChanged(ItemEvent arg0) {}

    public Object[] getSettings() {
        Prepravka prepravka = new Prepravka();

        invokeInEventDispatchThread(() -> {
            try {
                prepravka.data = new Object[18];
                prepravka.data[0] = Double.parseDouble(_gui.txtTrvanie.getText()) * 3600.0;
                prepravka.data[1] = Integer.parseInt(_gui.txtReplikacii.getText());                
                prepravka.data[2] = 1;
                prepravka.data[3] = _gui.sliderSimInt.getValue();
                prepravka.data[6] = _simMaxSpeedPressed;
                prepravka.data[7] = _animMaxSpeedPressed;
                // prepravka.data[8] = _gui.chckBoxCreateAnimAfterStart.isSelected();
                prepravka.data[9] = Integer.parseInt(_gui.txtPocetLekarov.getText());
                prepravka.data[10] = Integer.parseInt(_gui.txtPocetSestier.getText());
                int rezim = 1;
                if (_gui.rbRezim2.isSelected()) rezim = 2;
                else if (_gui.rbRezim3.isSelected()) rezim = 3;
                else if (_gui.rbRezim5.isSelected()) rezim = 5;
                prepravka.data[11] = rezim;
                prepravka.data[12] = Double.parseDouble(_gui.txtZahrievanie.getText()) * 3600.0;
                prepravka.data[13] = _gui.chckBoxTurboRezim.isSelected();
                prepravka.data[14] = _gui.chckBoxSledovatZahrievanie.isSelected();
                prepravka.data[15] = _gui.chckBoxMinPocet.isSelected();
                prepravka.data[16] = _gui.chckBoxRezervSestruAmbulanciuB.isSelected();
                prepravka.data[17] = _gui.chckBoxZobrazitPriebeh.isSelected();

            } catch (NumberFormatException e) {
            }
        });

        return prepravka.data;
    }

    public void invokeInEventDispatchThread(Runnable code) {
        if (SwingUtilities.isEventDispatchThread()) {
            code.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(() -> { code.run(); });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showResults(String results) {
        invokeInEventDispatchThread(() -> {
            _gui.lblResult.setText(results);
        });
    }

    @SuppressWarnings("unused")
    private void btnStart() {
        if (_simThread != null) {
            _simThread.stopSim();
            setGuiDefaultVisage();
        }
        
        _mySim = new MySimulation();
        
        Object[] settings = getSettings();
        boolean isTurbo = (boolean) settings[13];
        boolean isZahrievanie = (boolean) settings[14];
        boolean isMinPocet = (boolean) settings[15];
        boolean isZobrazitPriebeh = (boolean) settings[17];
        
        boolean allowUI = isZobrazitPriebeh && !isTurbo && !isZahrievanie && !isMinPocet;

        invokeInEventDispatchThread(() -> {
            _gui.animatorPanel.removeAll();
            
            if (allowUI) {
                _mySim.createAnimator();
                java.awt.Component canvas = _mySim.animator().canvas();
                _gui.animatorPanel.add(canvas, java.awt.BorderLayout.CENTER);
                _mySim.initStaticAnimation();
            }
            
            _gui.animatorPanel.revalidate();
            _gui.animatorPanel.repaint();
        });

        _simThread = new SimThread(this, _mySim);
        _simThread.start();
    }

    @SuppressWarnings("unused")
    private void btnPause() {
        if (_simThread != null) {
            if (_simThread.getSim().isPaused()) {
                _simThread.resumeSim();
                _gui.btnPause.setText("Pause");
            } else {
                _simThread.pauseSim();
                _gui.btnPause.setText("Resume");
            }
        }
    }

    @SuppressWarnings("unused")
    private void btnStop() {
        if (_simThread != null) _simThread.stopSim();
    }

    private void setSimSpeed() {
        if (_simThread != null) {
            _simThread.setSimSpeed(
                    1,
                    _gui.sliderSimInt.getValue(),
                    _simMaxSpeedPressed);
        }
    }

    @SuppressWarnings("unused")
    private void btnSimMaxSpeed() {
        _simMaxSpeedPressed = true;
        setButtonTextCollor(_gui.btnSimMaxSpeed, Color.RED);
        setSimSpeed();
    }

    @SuppressWarnings("unused")
    private void sliderSimInt() {
        _simMaxSpeedPressed = false;
        setButtonTextCollor(_gui.btnSimMaxSpeed, Color.BLACK);
        setSimSpeed();
    }

    private void setButtonTextCollor(JButton button, Color color) {
        button.setForeground(color);
    }

    private void setGuiDefaultVisage() {
        _gui.lblSimTime.setText("Simulacny cas:");
        _gui.btnPause.setText("Pause");
    }

    public void onRefreshUI(Simulation sim) {
        //** LLM usage, to specify
        invokeInEventDispatchThread(() -> {
            MySimulation simulacia = (MySimulation) sim;
            _gui.lblSimTime.setText("Čas: " + formatSimTime(sim.currentTime()));

            if (_gui.tabbedPane != null && _gui.tabbedPane.getSelectedIndex() == 1) {
                
                if (_gui.tableModel != null) {
                    _gui.tableModel.setRowCount(0);
                    for (String[] riadok : simulacia.aktualniPacienti.values()) {
                        _gui.tableModel.addRow(riadok);
                    }
                }
                
                if (_gui.tableAmbulancieModel != null) {
                    _gui.tableAmbulancieModel.setRowCount(0);
                    for (int i = 1; i <= 5; i++) {
                        String key = "A" + i;
                        _gui.tableAmbulancieModel.addRow(new Object[]{key, simulacia.stavAmbulancii.getOrDefault(key, "Voľná")});
                    }
                    for (int i = 1; i <= 7; i++) {
                        String key = "B" + i;
                        _gui.tableAmbulancieModel.addRow(new Object[]{key, simulacia.stavAmbulancii.getOrDefault(key, "Voľná")});
                    }
                }
            }

        });
    }

    private String formatSimTime(double timeInSeconds) {
        long totalSeconds = (long) timeInSeconds;
        
        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if (days > 0) {
            return String.format("%d dni, %02d:%02d:%02d", days, hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
    }

    public void updateProgressBar(int percent) {
        invokeInEventDispatchThread(() -> {
            _gui.progressBar.setValue(percent);
        });
    }

    //** LLM usage, to specify */
    public void updateLiveLog(String title, String message, boolean clearFirst) {
        invokeInEventDispatchThread(() -> {
            if (logFrame == null || !logFrame.isVisible()) {
                logFrame = new javax.swing.JFrame(title);
                logFrame.setSize(500, 400);
                logArea = new javax.swing.JTextArea();
                logArea.setEditable(false);
                logArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 13));
                logFrame.add(new javax.swing.JScrollPane(logArea));
                logFrame.setLocationRelativeTo(_gui);
                logFrame.setVisible(true);
            }
            
            if (clearFirst) {
                logArea.setText(message + "\n");
            } else {
                logArea.append(message + "\n");
                logArea.setCaretPosition(logArea.getDocument().getLength());
            }
        });
    }

    private void showHelp(String title, String text) {
        String formattedText = "<html><body style='width: 500px;'>" + text + "</body></html>";
        javax.swing.JOptionPane.showMessageDialog(_gui, formattedText, "Info o režime: " + title, javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
}