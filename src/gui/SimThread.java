package gui;

import simulation.MySimulation;
import java.util.ArrayList;
import java.util.List;

public class SimThread extends Thread {

    private GuiLogic _guiLogic;
    private MySimulation _sim;
    private List<Double> dataSanitka;
    private List<Double> ciSanitka;
    private List<Double> dataSamostatne;
    private List<Double> ciSamostatne;

    public SimThread(GuiLogic guiLogic, MySimulation sim) {
        _guiLogic = guiLogic;
        _sim = sim;
        dataSanitka = new ArrayList<>();
        ciSanitka = new ArrayList<>();
        dataSamostatne = new ArrayList<>();
        ciSamostatne = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            Object[] settings = _guiLogic.getSettings();

            boolean isTurbo = (boolean) settings[13];
            boolean isZahrievanie = (boolean) settings[14];
            boolean isMinPocet = (boolean) settings[15];
            boolean isZobrazitPriebeh = (boolean) settings[17];
            // if (isTurbo || isMinPocet || isZahrievanie) {
            //     _guiLogic.invokeInEventDispatchThread(() -> {
            //         // Force the UI to switch to the Tabuľka stavov (Index 1)
            //         _guiLogic._gui.tabbedPane.setSelectedIndex(1);
            //     });
            // }

            boolean showUI = !isTurbo && !isMinPocet && !isZahrievanie && isZobrazitPriebeh;

            _sim.onRefreshUI((sim) -> {
                if (showUI) {
                    _guiLogic.onRefreshUI(sim);
                }

                double totalTime = (int) settings[1] * (double) settings[0];
                double currentOverallTime = (_sim.currentReplication() * (double) settings[0]) + sim.currentTime();
                int percent = (int) ((currentOverallTime / totalTime) * 100);
                
                _guiLogic.updateProgressBar(percent);
            });

            _sim.onReplicationWillStart((sim) -> {
                Object[] guiSettings = _guiLogic.getSettings();
                int duration = (int) guiSettings[2];
                int interval = (int) guiSettings[3];
                boolean maxSpeed = (boolean) guiSettings[6];

                if (!showUI) {
                    setSimSpeed(duration, interval, true);
                } else {
                    setSimSpeed(duration, interval, maxSpeed);
                }
            });
            
            _sim.onReplicationDidFinish((sim) -> {
                if (showUI) {
                    _guiLogic.onRefreshUI(sim);
                }

                double totalTime = (int) settings[1] * (double) settings[0];
                double currentOverallTime = ((_sim.currentReplication() + 1) * (double) settings[0]);
                int percent = (int) ((currentOverallTime / totalTime) * 100);
                
                _guiLogic.updateProgressBar(percent);

                if (!isMinPocet) {
                    dataSanitka.add(_sim.globalCasCakaniaOsetreniaSanitkou.getGlobalAverage());
                    ciSanitka.add(_sim.globalCasCakaniaOsetreniaSanitkou.getConfidenceIntervalHalfWidth());
                    
                    dataSamostatne.add(_sim.globalCasCakaniaOsetreniaSamostatne.getGlobalAverage());
                    ciSamostatne.add(_sim.globalCasCakaniaOsetreniaSamostatne.getConfidenceIntervalHalfWidth());
                }
            });

            boolean jeOptimalizacnyRezim = isMinPocet;

            _sim.onSimulationDidFinish((sim) -> {
                if (!jeOptimalizacnyRezim) {
                    String results = String.format("___ Výsledky (%d replikácií) ___\n\n" +
                        "Priem. Vybavení pacienti:      %.0f\n" +
                        "Priem. Čas v systéme:          %.2f ± %.2f min\n" +
                        "Priem. Čakanie (Sanitka):      %.2f ± %.2f min\n" +
                        "Priem. Čakanie (Samostatne):   %.2f ± %.2f min\n" +
                        "Priem. Čakanie (Triage):       %.2f ± %.2f min\n" +
                        "Priem. Dĺžka radu Sanitka:     %.2f ľudí\n" +
                        "Priem. Dĺžka radu Samostatne:  %.2f ľudí\n" +
                        "Priem. Využitie Lekárov:       %.2f %%\n" +
                        "Priem. Využitie Sestier:       %.2f %%\n",
                        (int) settings[1],
                        _sim.globalVybaveniPacienti.getGlobalAverage(),
                        _sim.globalCasVSysteme.getGlobalAverage() / 60.0, _sim.globalCasVSysteme.getConfidenceIntervalHalfWidth() / 60.0,
                        _sim.globalCasCakaniaOsetreniaSanitkou.getGlobalAverage() / 60.0, _sim.globalCasCakaniaOsetreniaSanitkou.getConfidenceIntervalHalfWidth() / 60.0,
                        _sim.globalCasCakaniaOsetreniaSamostatne.getGlobalAverage() / 60.0, _sim.globalCasCakaniaOsetreniaSamostatne.getConfidenceIntervalHalfWidth() / 60.0,
                        _sim.globalCasCakaniaVstupVysVsetci.getGlobalAverage() / 60.0, _sim.globalCasCakaniaVstupVysVsetci.getConfidenceIntervalHalfWidth() / 60.0,
                        _sim.globalRadVstupVysetrenieSanitkou.getGlobalAverage(),
                        _sim.globalRadVstupVysSamostatne.getGlobalAverage(),
                        _sim.globalVyuzitieLekar.getGlobalAverage(),
                        _sim.globalVyuzitieSestra.getGlobalAverage()
                    );
            
                    _guiLogic.updateLiveLog("Výsledky Simulácie", results, true);
                    _guiLogic.showResults("<html>Simulácia ukončená. Výsledky sú v novom okne.</html>");

                    _guiLogic.invokeInEventDispatchThread(() -> {
                        OknoGrafu oknoGrafu = new OknoGrafu();
                        oknoGrafu.panelGrafu.nastavData(dataSanitka, ciSanitka, dataSamostatne, ciSamostatne);
                        oknoGrafu.setVisible(true);
                    });
                }
            });

            _sim.configRezervovatSestruAmbulanciuB = (boolean) settings[16];
            _sim.configTurboRezim = isTurbo;
            _sim.configSledovatZahrievanie = isZahrievanie;
            _sim.trvanieZahrievania = (double) settings[12];
            _sim.configZvolenyRezim = (int) settings[11];

            if (!jeOptimalizacnyRezim) {
                _sim.configPocetLekarov = (int) settings[9];
                _sim.configPocetSestier = (int) settings[10];
                dataSanitka.clear();
                ciSanitka.clear();
                dataSamostatne.clear();
                ciSamostatne.clear();
                _sim.simulate((int) settings[1], (double) settings[0]);

            } else {
                int pocetLekarov = 1;
                int pocetSestier = 25;

                _guiLogic.updateLiveLog("Priebeh Optimalizácie", "Analýza bola spustená (Limit: Sanitkou 15m, Samostatne 30m)\n", true);

                while (true) {
                    _guiLogic.updateLiveLog("Priebeh Optimalizácie", "Hľadanie min. hodnoty lekárov: " + pocetLekarov + " Lek, " + pocetSestier + " Ses...", false);
                    
                    _sim.configPocetLekarov = pocetLekarov;
                    _sim.configPocetSestier = pocetSestier;
                    _sim.simulate((int) settings[1], (double) settings[0]);

                    if (podmienkySplnene(_sim)) {
                        _guiLogic.updateLiveLog("Priebeh Optimalizácie", "  Zistená minimálna hodnota lekárov: " + pocetLekarov + " (pri dočasných " + pocetSestier + " sestrách)\n", false);
                        break;
                    }
                    pocetLekarov++;
                }

                pocetSestier = 1;
                while (true) {
                    _guiLogic.updateLiveLog("Priebeh Optimalizácie", "Hľadanie min. hodnoty sestier: " + pocetLekarov + " Lek, " + pocetSestier + " Ses...", false);
                    
                    _sim.configPocetLekarov = pocetLekarov;
                    _sim.configPocetSestier = pocetSestier;
                    _sim.simulate((int) settings[1], (double) settings[0]);

                    double cakanieSanitka = _sim.globalCasCakaniaOsetreniaSanitkou.getGlobalAverage() / 60.0;
                    double cakanieSamostatne = _sim.globalCasCakaniaOsetreniaSamostatne.getGlobalAverage() / 60.0;

                    if (podmienkySplnene(_sim)) {
                        _guiLogic.updateLiveLog("Priebeh Optimalizácie", String.format("  ✓ PASS: %d Lek, %d Ses (San: %.1fm, Sam: %.1fm)", pocetLekarov, pocetSestier, cakanieSanitka, cakanieSamostatne), false);
                        break;
                    } else {
                        _guiLogic.updateLiveLog("Priebeh Optimalizácie", String.format("  ✗ FAIL: %d Lek, %d Ses (San: %.1fm, Sam: %.1fm)", pocetLekarov, pocetSestier, cakanieSanitka, cakanieSamostatne), false);
                    }
                    pocetSestier++;
                }

                _guiLogic.updateLiveLog("Priebeh Optimalizácie", "\n___ VÝSLEDOK: " + pocetLekarov + " Lekárov, " + pocetSestier + " Sestier ___\n", false);
                _guiLogic.showResults("<html>Optimalizácia ukončená. Výsledky sú v novom okne.</html>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    private boolean podmienkySplnene(MySimulation sim) {
        boolean sanitkaPass = sim.globalCasCakaniaOsetreniaSanitkou.getGlobalAverage() < 900.0;
        boolean samostatnePass = sim.globalCasCakaniaOsetreniaSamostatne.getGlobalAverage() < 1800.0;
        return sanitkaPass && samostatnePass;
    }

    public MySimulation getSim() {
        return _sim;
    }

    public void pauseSim() {
        if (_sim != null) _sim.pauseSimulation();
    }

    public void resumeSim() {
        if (_sim != null) _sim.resumeSimulation();
    }

    public void stopSim() {
        if (_sim != null) _sim.stopSimulation();
    }

    public void setSimSpeed(int duration, int interval, boolean maxSpeed) {
        if (_sim == null) return;

        if (maxSpeed) {
            _sim.setMaxSimSpeed();
        } else {
            _sim.setSimSpeed(interval, duration);
        }
    }
}