package gui;

import simulation.MySimulation;
import java.util.ArrayList;
import java.util.List;

// LLM bol použitý na odstraňovanie chýb a implementáciu niektorých funkcií v GUI
public class SimThread extends Thread {
    private GuiLogic _guiLogic;
    private MySimulation _sim;
    private List<Double> dataSanitka;
    private List<Double> ciSanitka;
    private List<Double> dataSamostatne;
    private List<Double> ciSamostatne;

    //optimalizácia. využitie pamäte RAM
    private int lastPercent = -1;

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
            boolean showUI = !isTurbo && !isMinPocet && !isZahrievanie && isZobrazitPriebeh;

            _sim.onRefreshUI((sim) -> {
                if (showUI) {
                    _guiLogic.onRefreshUI(sim);
                }

                double totalTime = (int) settings[1] * (double) settings[0];
                double currentOverallTime = (_sim.currentReplication() * (double) settings[0]) + sim.currentTime();
                int percent = (int) ((currentOverallTime / totalTime) * 100);
                
                if (percent != lastPercent) {
                    lastPercent = percent;
                    _guiLogic.updateProgressBar(percent);
                }
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
                
                if (percent != lastPercent) {
                    lastPercent = percent;
                    _guiLogic.updateProgressBar(percent);
                }
                
                // _guiLogic.updateProgressBar(percent);

                if (isTurbo && !isMinPocet) {
                    dataSanitka.add(_sim.globalCasCakaniaOsetreniaSanitkou.getGlobalAverage());
                    ciSanitka.add(_sim.globalCasCakaniaOsetreniaSanitkou.getConfidenceIntervalHalfWidth());
                    
                    dataSamostatne.add(_sim.globalCasCakaniaOsetreniaSamostatne.getGlobalAverage());
                    ciSamostatne.add(_sim.globalCasCakaniaOsetreniaSamostatne.getConfidenceIntervalHalfWidth());
                }
            });

            boolean jeOptimalizacnyRezim = isMinPocet;

            _sim.onSimulationDidFinish((sim) -> {
                if (isTurbo && !jeOptimalizacnyRezim) {
                    String results = String.format("___Výsledky (%d replikácií)___\n\n" +
                        "Priem. celkový počet vybavených:  %.0f ± %.0f osôb\n" +
                        "Priem. čas v systéme:             %s ± %s\n" +
                        "Priem. čas čakania na ošetrenie (Sanitka):     %s ± %s\n" +
                        "Priem. čas čakania na ošetrenie (Samostatne):  %s ± %s\n" +
                        "Priem. čas čakania na vstupné vyšetrenie (všetci):     %s ± %s\n" +
                        "Priem. dĺžka radu, na vstupné vyšetrenie (Sanitka):      %.2f ± %.2f ľudí\n" +
                        "Priem. dĺžka radum, na vstupné vyšetrenie(Samostatne):   %.2f ± %.2f ľudí\n" +
                        "Priem. využitie Lekárov:          %.2f ± %.2f %%\n" +
                        "Priem. využitie Sestier:          %.2f ± %.2f %%\n",
                        (int) settings[1],
                        
                        _sim.globalVybaveniPacienti.getGlobalAverage(), 
                        _sim.globalVybaveniPacienti.getConfidenceIntervalHalfWidth(),
                        
                        formatCas(_sim.globalCasVSysteme.getGlobalAverage()), 
                        formatCas(_sim.globalCasVSysteme.getConfidenceIntervalHalfWidth()),
                        
                        formatCas(_sim.globalCasCakaniaOsetreniaSanitkou.getGlobalAverage()), 
                        formatCas(_sim.globalCasCakaniaOsetreniaSanitkou.getConfidenceIntervalHalfWidth()),
                        
                        formatCas(_sim.globalCasCakaniaOsetreniaSamostatne.getGlobalAverage()), 
                        formatCas(_sim.globalCasCakaniaOsetreniaSamostatne.getConfidenceIntervalHalfWidth()),
                        
                        formatCas(_sim.globalCasCakaniaVstupVysVsetci.getGlobalAverage()), 
                        formatCas(_sim.globalCasCakaniaVstupVysVsetci.getConfidenceIntervalHalfWidth()),
                        
                        _sim.globalRadVstupVysetrenieSanitkou.getGlobalAverage(), 
                        _sim.globalRadVstupVysetrenieSanitkou.getConfidenceIntervalHalfWidth(),
                        
                        _sim.globalRadVstupVysSamostatne.getGlobalAverage(), 
                        _sim.globalRadVstupVysSamostatne.getConfidenceIntervalHalfWidth(),
                        
                        _sim.globalVyuzitieLekar.getGlobalAverage(), 
                        _sim.globalVyuzitieLekar.getConfidenceIntervalHalfWidth(),
                        
                        _sim.globalVyuzitieSestra.getGlobalAverage(), 
                        _sim.globalVyuzitieSestra.getConfidenceIntervalHalfWidth()
                    );

                    _guiLogic.updateLiveLog("Výsledky Simulácie", results, true);
                    _guiLogic.showResults("<html>Simulácia ukončená. Výsledky sú v novom okne.</html>");

                    _guiLogic.invokeInEventDispatchThread(() -> {
                        OknoGrafu oknoGrafu = new OknoGrafu();
                        oknoGrafu.panelGrafu.nastavData(dataSanitka, ciSanitka, dataSamostatne, ciSamostatne);
                        oknoGrafu.setVisible(true);
                    });
                } else if (!jeOptimalizacnyRezim) {
                    _guiLogic.showResults("<html>Simulácia ukončená.</html>");
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
                int pocetLekarov = 5;
                int pocetSestier = 25;

                _guiLogic.updateLiveLog("Priebeh Optimalizácie", "Analýza bola spustená (Limit: Sanitkou 15m, Samostatne 30m)\n", true);

                while (true) {
                    _guiLogic.updateLiveLog("Priebeh Optimalizácie", "Hľadanie min. hodnoty lekárov: " + pocetLekarov + " Lek, " + pocetSestier + " Ses...", false);
                    
                    _sim.configPocetLekarov = pocetLekarov;
                    _sim.configPocetSestier = pocetSestier;
                    _sim.simulate((int) settings[1], (double) settings[0]);

                    if (podmienkySplnene(_sim)) {
                        _guiLogic.updateLiveLog("Priebeh Optimalizácie", "Zistená minimálna hodnota lekárov: " + pocetLekarov + " (pri dočasných " + pocetSestier + " sestrách)\n", false);
                        break;
                    }
                    pocetLekarov++;
                }

                pocetSestier = 5;
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

    private String formatCas(double totalSeconds) {
        long m = (long) (totalSeconds / 60);
        long s = Math.round(totalSeconds % 60);
        return String.format("%dm %ds", m, s);
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