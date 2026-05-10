package gui;

import simulation.MySimulation;

public class SimThread extends Thread {

    private GuiLogic _guiLogic;
    private MySimulation _sim;

    public SimThread(GuiLogic guiLogic, MySimulation sim) {
        _guiLogic = guiLogic;
        _sim = sim;
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

                if (_sim.configSledovatZahrievanie && _sim.currentReplication() == 0) {
                    try (java.io.FileWriter writer = new java.io.FileWriter("warmup_data.csv", false)) {
                        writer.write("Cas_Sekundy,Cas_Hodiny,PriemernyCasVSysteme,PriemerneCakanieOsetrenie,radVstupVysetrenieSanitkou,RadVstupVysSamostatne\n");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

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

                //** LLM for progress bar update */
                double totalTime = (int) settings[1] * (double) settings[0];
                double currentOverallTime = ((_sim.currentReplication() + 1) * (double) settings[0]);
                int percent = (int) ((currentOverallTime / totalTime) * 100);
                
                _guiLogic.updateProgressBar(percent);
            });

            boolean isOptimizationMode = (boolean) settings[15];

            _sim.onSimulationDidFinish((sim) -> {
                if (!isOptimizationMode) {
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
                }
            });

            _sim.configTurboRezim = (boolean) settings[13];
            _sim.configSledovatZahrievanie = (boolean) settings[14];
            _sim.trvanieZahrievania = (double) settings[12];
            _sim.configRezim1Aktivny = (boolean) settings[11];

            if (!isOptimizationMode) {

                _sim.configPocetLekarov = (int) settings[9];
                _sim.configPocetSestier = (int) settings[10];
                
                _sim.simulate((int) settings[1], (double) settings[0]);

            } else {
                int currentDocs = 1;
                int currentNurses = 1; 
                int step = 5;

                StringBuilder log = new StringBuilder();
                log.append("<html><div style='font-size: 11px;'>");
                log.append("<b>Priebeh optimalizácie (Limit: San 15m, Sam 30m):</b><br>");

                System.out.println("--- PHASE 1: JUMP BY 5 ---");
                while (true) {
                    _guiLogic.showResults(log.toString() + "<i>▶ Testujem: " + currentDocs + " Lek, " + currentNurses + " Ses...</i></div></html>");
                    
                    _sim.configPocetLekarov = currentDocs;
                    _sim.configPocetSestier = currentNurses;
                    _sim.simulate((int) settings[1], (double) settings[0]);

                    double waitSan = _sim.globalCasCakaniaOsetreniaSanitkou.getGlobalAverage() / 60.0;
                    double waitSam = _sim.globalCasCakaniaOsetreniaSamostatne.getGlobalAverage() / 60.0;

                    if (checkConditionsPassed(_sim)) {
                        log.append(String.format("<span style='color:green;'>✓ PASS: %d Lek, %d Ses (San: %.1fm, Sam: %.1fm)</span><br>", currentDocs, currentNurses, waitSan, waitSam));
                        break; 
                    } else {
                        log.append(String.format("<span style='color:red;'>✗ FAIL: %d Lek, %d Ses (San: %.1fm, Sam: %.1fm)</span><br>", currentDocs, currentNurses, waitSan, waitSam));
                        currentDocs += step;
                        currentNurses += step;
                    }
                }

                System.out.println("--- PHASE 2: TRIM DOCTORS ---");
                while (true) {
                    currentDocs -= 1;
                    if (currentDocs < 1) {
                        currentDocs = 1; 
                        break;
                    }
                    _guiLogic.showResults(log.toString() + "<i>▶ Testujem: " + currentDocs + " Lek, " + currentNurses + " Ses...</i></div></html>");
                    
                    _sim.configPocetLekarov = currentDocs;
                    _sim.simulate((int) settings[1], (double) settings[0]);
                    
                    double waitSan = _sim.globalCasCakaniaOsetreniaSanitkou.getGlobalAverage() / 60.0;
                    double waitSam = _sim.globalCasCakaniaOsetreniaSamostatne.getGlobalAverage() / 60.0;

                    if (!checkConditionsPassed(_sim)) {
                        log.append(String.format("<span style='color:red;'>✗ FAIL: %d Lek, %d Ses (San: %.1fm, Sam: %.1fm)</span><br>", currentDocs, currentNurses, waitSan, waitSam));
                        currentDocs += 1;
                        break;
                    } else {
                        log.append(String.format("<span style='color:green;'>✓ PASS: %d Lek, %d Ses (San: %.1fm, Sam: %.1fm)</span><br>", currentDocs, currentNurses, waitSan, waitSam));
                    }
                }

                System.out.println("--- PHASE 3: TRIM NURSES ---");
                while (true) {
                    currentNurses -= 1;
                    if (currentNurses < 1) {
                        currentNurses = 1; 
                        break;
                    }
                    _guiLogic.showResults(log.toString() + "<i>▶ Testujem: " + currentDocs + " Lek, " + currentNurses + " Ses...</i></div></html>");
                    
                    _sim.configPocetSestier = currentNurses;
                    _sim.simulate((int) settings[1], (double) settings[0]);
                    
                    double waitSan = _sim.globalCasCakaniaOsetreniaSanitkou.getGlobalAverage() / 60.0;
                    double waitSam = _sim.globalCasCakaniaOsetreniaSamostatne.getGlobalAverage() / 60.0;

                    if (!checkConditionsPassed(_sim)) {
                        log.append(String.format("<span style='color:red;'>✗ FAIL: %d Lek, %d Ses (San: %.1fm, Sam: %.1fm)</span><br>", currentDocs, currentNurses, waitSan, waitSam));
                        currentNurses += 1;
                        break;
                    } else {
                        log.append(String.format("<span style='color:green;'>✓ PASS: %d Lek, %d Ses (San: %.1fm, Sam: %.1fm)</span><br>", currentDocs, currentNurses, waitSan, waitSam));
                    }
                }

                log.append(String.format("<br><div style='background-color:#e6ffe6; padding:8px; border:1px solid green;'><b>VÍŤAZ: %d Lekárov, %d Sestier</b></div></div></html>", currentDocs, currentNurses));
                
                _guiLogic.showResults(log.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkConditionsPassed(MySimulation sim) {
        boolean sanitkaPass = sim.globalCasCakaniaOsetreniaSanitkou.getGlobalAverage() <= 900.0;
        boolean samostatnePass = sim.globalCasCakaniaOsetreniaSamostatne.getGlobalAverage() <= 1800.0;
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