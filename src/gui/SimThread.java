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

            _sim.onRefreshUI((sim) -> {
                if (!_sim.configTurboRezim) {
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

                if (_sim.configTurboRezim) {
                    setSimSpeed(duration, interval, true);
                } else {
                    setSimSpeed(duration, interval, maxSpeed);
                }
            });
            
            _sim.onReplicationDidFinish((sim) -> {
                if (!_sim.configTurboRezim) {
                    _guiLogic.onRefreshUI(sim);
                }

                double totalTime = (int) settings[1] * (double) settings[0];
                double currentOverallTime = ((_sim.currentReplication() + 1) * (double) settings[0]);
                int percent = (int) ((currentOverallTime / totalTime) * 100);
                
                _guiLogic.updateProgressBar(percent);

                if (!(boolean) settings[15]) {
                    dataSanitka.add(_sim.globalCasCakaniaOsetreniaSanitkou.getGlobalAverage());
                    ciSanitka.add(_sim.globalCasCakaniaOsetreniaSanitkou.getConfidenceIntervalHalfWidth());
                    
                    dataSamostatne.add(_sim.globalCasCakaniaOsetreniaSamostatne.getGlobalAverage());
                    ciSamostatne.add(_sim.globalCasCakaniaOsetreniaSamostatne.getConfidenceIntervalHalfWidth());
                }
            });

            boolean jeOptimalizacnyRezim = (boolean) settings[15];

            _sim.onSimulationDidFinish((sim) -> {
                if (!jeOptimalizacnyRezim) {
                    String results = String.format("<html><b>Výsledky (%d replikácií):</b><br>" +
                        "Vybavení pacienti (priemerne): %.0f<br>" +
                        "Čas v systéme: %.2f &plusmn; %.2f s<br>" +
                        "Čakanie na ošetrenie sanitka: %.2f &plusmn; %.2f s<br>" +
                        "Čakanie na ošetrenie samostatne: %.2f &plusmn; %.2f s<br>" +
                        "Čakanie na vstup. vyš.: %.2f &plusmn; %.2f s<br>" +
                        "Dĺžka radu Sanitka: %.2f ľudí<br>" +
                        "Dĺžka radu Samostatne: %.2f ľudí<br>" +
                        "Využitie Lekárov: %.2f %%<br>" +
                        "Využitie Sestier: %.2f %% <br></html>",
                        (int) settings[1],
                        _sim.globalVybaveniPacienti.getGlobalAverage(),
                        _sim.globalCasVSysteme.getGlobalAverage(), _sim.globalCasVSysteme.getConfidenceIntervalHalfWidth(),
                        _sim.globalCasCakaniaOsetreniaSanitkou.getGlobalAverage(), _sim.globalCasCakaniaOsetreniaSanitkou.getConfidenceIntervalHalfWidth(),
                        _sim.globalCasCakaniaOsetreniaSamostatne.getGlobalAverage(), _sim.globalCasCakaniaOsetreniaSamostatne.getConfidenceIntervalHalfWidth(),
                        _sim.globalCasCakaniaVstupVysVsetci.getGlobalAverage(), _sim.globalCasCakaniaVstupVysVsetci.getConfidenceIntervalHalfWidth(),
                        _sim.globalRadVstupVysetrenieSanitkou.getGlobalAverage(),
                        _sim.globalRadVstupVysSamostatne.getGlobalAverage(),
                        _sim.globalVyuzitieLekar.getGlobalAverage(),
                        _sim.globalVyuzitieSestra.getGlobalAverage()
                    );
            
                    _guiLogic.showResults(results);

                    _guiLogic.invokeInEventDispatchThread(() -> {
                        OknoGrafu oknoGrafu = new OknoGrafu();
                        oknoGrafu.panelGrafu.nastavData(dataSanitka, ciSanitka, dataSamostatne, ciSamostatne);
                        oknoGrafu.setVisible(true);
                    });
                }
            });

            _sim.configRezervovatSestruAmbulanciuB = (boolean) settings[16];
            _sim.configTurboRezim = (boolean) settings[13];
            _sim.configSledovatZahrievanie = (boolean) settings[14];
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

                StringBuilder log = new StringBuilder();
                log.append("<html><div style='font-size: 11px;'>");
                log.append("<b>Priebeh optimalizácie (Limit: San 15m, Sam 30m):</b><br>");

                while (true) {
                    _guiLogic.showResults(log.toString() + "<i>▶ Hľadám min. lekárov: " + pocetLekarov + " Lek, " + pocetSestier + " Ses...</i></div></html>");
                    
                    _sim.configPocetLekarov = pocetLekarov;
                    _sim.configPocetSestier = pocetSestier;
                    _sim.simulate((int) settings[1], (double) settings[0]);

                    if (podmienkySplnene(_sim)) {
                        log.append(String.format("<span style='color:blue;'>Nájdené min. lekárov: %d (pri dočasných %d sestrách)</span><br>", pocetLekarov, pocetSestier));
                        break;
                    }
                    pocetLekarov++;
                }

                pocetSestier = 1;
                while (true) {
                    _guiLogic.showResults(log.toString() + "<i>▶ Hľadám min. sestry: " + pocetLekarov + " Lek, " + pocetSestier + " Ses...</i></div></html>");
                    
                    _sim.configPocetLekarov = pocetLekarov;
                    _sim.configPocetSestier = pocetSestier;
                    _sim.simulate((int) settings[1], (double) settings[0]);

                    double cakanieSanitka = _sim.globalCasCakaniaOsetreniaSanitkou.getGlobalAverage() / 60.0;
                    double cakanieSamostatne = _sim.globalCasCakaniaOsetreniaSamostatne.getGlobalAverage() / 60.0;

                    if (podmienkySplnene(_sim)) {
                        log.append(String.format("<span style='color:green;'>✓ PASS: %d Lek, %d Ses (San: %.1fm, Sam: %.1fm)</span><br>", pocetLekarov, pocetSestier, cakanieSanitka, cakanieSamostatne));
                        break;
                    } else {
                        log.append(String.format("<span style='color:red;'>✗ FAIL: %d Lek, %d Ses (San: %.1fm, Sam: %.1fm)</span><br>", pocetLekarov, pocetSestier, cakanieSanitka, cakanieSamostatne));
                    }
                    pocetSestier++;
                }

                log.append(String.format("<br><div style='background-color:#e6ffe6; padding:8px; border:1px solid green;'><b>VÍŤAZ: %d Lekárov, %d Sestier</b></div></div></html>", pocetLekarov, pocetSestier));
                _guiLogic.showResults(log.toString());
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