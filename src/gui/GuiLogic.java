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

public class GuiLogic implements ActionListener, ChangeListener, ItemListener {

    public Gui _gui; //??
    private SimThread _simThread;
    private MySimulation _mySim;
    private javax.swing.JFrame logFrame;
    private javax.swing.JTextArea logArea;
    private boolean _simMaxSpeedPressed = false;
    private boolean _animMaxSpeedPressed = false;

    // public static int SELECTED_IMAGE_TYPE = 1; 

    private class Prepravka {
        public Object[] data = null;
    }

    public GuiLogic(Gui gui) {
        _gui = gui;
        _mySim = new MySimulation();

        // if (_mySim.animator() != null && _gui.animatorPanel != null) {
        //     java.awt.Component canvas = _mySim.animator().canvas();
        //     _gui.animatorPanel.add(canvas, java.awt.BorderLayout.CENTER);
        //     _mySim.initStaticAnimation();            
        // }

        gui.btnStart.addActionListener(this);
        gui.btnPause.addActionListener(this);
        gui.btnStop.addActionListener(this);

        // gui.rdioBttonImgType1.addActionListener(e -> SELECTED_IMAGE_TYPE = 1);
        // gui.rdioBttonImgType2.addActionListener(e -> SELECTED_IMAGE_TYPE = 2);

        gui.btnSimMaxSpeed.addActionListener(this);
        // gui.btnCreateAnim.addActionListener(this);
        // gui.btnRemoveAnim.addActionListener(this);

        gui.sliderSimDur.addChangeListener(this);
        gui.sliderSimInt.addChangeListener(this);
        // gui.chckBoxCreateAnimAfterStart.addItemListener(this);
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
        // if (callActionMethod(ae, _gui.btnCreateAnim, "btnCreateAnim")) return;
        // if (callActionMethod(ae, _gui.btnRemoveAnim, "btnRemoveAnim")) return;
    }

    @Override
    public void stateChanged(ChangeEvent che) {
        if (callActionMethod(che, _gui.sliderSimDur, "sliderSimDur")) return;
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
                prepravka.data[1] = Integer.parseInt(_gui.txtReplikacii.getText());                prepravka.data[2] = _gui.sliderSimDur.getValue();
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
                    _gui.sliderSimDur.getValue(),
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

    // @SuppressWarnings("unused")
    // public void btnCreateAnim() {
    //     Object[] settings = getSettings();
    //     boolean isTurbo = (boolean) settings[13];
    //     boolean isMinPocet = (boolean) settings[15];
    //     boolean isZahrievanie = (boolean) settings[14];

    //     if (isTurbo || isMinPocet || isZahrievanie) {
    //         javax.swing.JOptionPane.showMessageDialog(_gui, "Animáciu nie je možné spustiť v režimoch Turbo, Min. Počet alebo Zahrievanie.");
    //         return;
    //     }

    //     _mySim.createAnimator();
        
    //     invokeInEventDispatchThread(() -> {
    //         _gui.animatorPanel.removeAll();
    //         java.awt.Component canvas = _mySim.animator().canvas();
    //         _gui.animatorPanel.add(canvas, java.awt.BorderLayout.CENTER);
            
    //         _gui.animatorPanel.revalidate();
    //         _gui.animatorPanel.repaint();

    //         // Wait 500ms for the library to finish building the toolbar, then hide buttons
    //         javax.swing.Timer timer = new javax.swing.Timer(500, e -> {
    //             hideUnwantedAnimatorControls(canvas);
    //         });
    //         timer.setRepeats(false);
    //         timer.start();
    //     });

    //     _mySim.initStaticAnimation();
    // }
    // public void btnCreateAnim() {
    //     Object[] settings = getSettings();
    //     boolean isTurbo = (boolean) settings[13];
    //     boolean isMinPocet = (boolean) settings[15];
    //     boolean isZahrievanie = (boolean) settings[14];

    //     if (isTurbo || isMinPocet || isZahrievanie) {
    //         javax.swing.JOptionPane.showMessageDialog(_gui, "Animáciu nie je možné spustiť v režimoch Turbo, Min. Počet alebo Zahrievanie.");
    //         return;
    //     }

    //     _mySim.createAnimator();
        
    //     invokeInEventDispatchThread(() -> {
    //         _gui.animatorPanel.removeAll();
    //         java.awt.Component canvas = _mySim.animator().canvas();
    //         _gui.animatorPanel.add(canvas, java.awt.BorderLayout.CENTER);
            
    //         _gui.animatorPanel.revalidate();
    //         _gui.animatorPanel.repaint();
    //     });

    //     _mySim.initStaticAnimation();
    // }

    // @SuppressWarnings("unused")
    // private void btnRemoveAnim() {
    // }

    @SuppressWarnings("unused")
    private void sliderSimDur() {
        _simMaxSpeedPressed = false;
        setButtonTextCollor(_gui.btnSimMaxSpeed, Color.BLACK);
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

    // //** LLM usage, to specify */
    // private void hideUnwantedAnimatorControls(java.awt.Component c) {
    //     if (c instanceof javax.swing.JToolBar) {
    //         javax.swing.JToolBar toolbar = (javax.swing.JToolBar) c;
    //         for (java.awt.Component btnComponent : toolbar.getComponents()) {
    //             if (btnComponent instanceof javax.swing.AbstractButton) {
    //                 javax.swing.AbstractButton btn = (javax.swing.AbstractButton) btnComponent;
                    
    //                 String tooltip = btn.getToolTipText() != null ? btn.getToolTipText().toLowerCase() : "";
    //                 String text = btn.getText() != null ? btn.getText().toLowerCase() : "";
    //                 String action = btn.getActionCommand() != null ? btn.getActionCommand().toLowerCase() : "";
                    
    //                 // Checks for zoom, +, -, lupa (Slovak for magnifying glass)
    //                 boolean isZoom = tooltip.contains("zoom") || tooltip.contains("+") || tooltip.contains("-")
    //                               || text.contains("zoom") || text.contains("+") || text.contains("-")
    //                               || action.contains("zoom") || action.contains("+") || action.contains("-")
    //                               || tooltip.contains("lupa") || tooltip.contains("zväčšiť") || tooltip.contains("zmenšiť");
                    
    //                 if (!isZoom) {
    //                     btn.setVisible(false);
    //                 }
    //             }
    //         }
    //     } else if (c instanceof java.awt.Container) {
    //         for (java.awt.Component child : ((java.awt.Container) c).getComponents()) {
    //             hideUnwantedAnimatorControls(child);
    //         }
    //     }
    // }
}