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

                _sim.configPocetLekarov = (int) guiSettings[9];
                _sim.configPocetSestier = (int) guiSettings[10];
                _sim.configRezim1Aktivny = (boolean) guiSettings[11];
                _sim.configZahrievanie = (double) guiSettings[12];
                _sim.configTurboRezim = (boolean) guiSettings[13];
                _sim.configSledovatZahrievanie = (boolean) guiSettings[14];

                if (_sim.configSledovatZahrievanie && _sim.currentReplication() == 0) {
                    try (java.io.FileWriter writer = new java.io.FileWriter("warmup_data.csv", false)) {
                        writer.write("Cas_Sekundy,Cas_Hodiny,PriemernyCasVSysteme,PriemerneCakanieOsetrenie,RadSanitka,RadSamostatne\n");
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

            _sim.onSimulationDidFinish((sim) -> {
                String results = String.format("<html><b>Výsledky (%d replikácií):</b><br>" +
                    "Vybavení pacienti (priemerne): %.0f<br>" +
                    "Čas v systéme: %.2f &plusmn; %.2f s<br>" +
                    "Čakanie na ošetrenie: %.2f &plusmn; %.2f s<br>" +
                    "Čakanie na vstup. vyš.: %.2f &plusmn; %.2f s<br><br>" +
                    "Dĺžka radu Sanitka: %.2f ľudí<br>" +
                    "Dĺžka radu Samostatne: %.2f ľudí<br><br>" +
                    "Využitie Lekárov: %.2f %%<br>" +
                    "Využitie Sestier: %.2f %%</html>",
                    (int) settings[1],
                    _sim.globalVybaveniPacienti.getGlobalAverage(),
                    _sim.globalCasVSysteme.getGlobalAverage(), _sim.globalCasVSysteme.getConfidenceIntervalHalfWidth(),
                    _sim.globalCasCakaniaOsetrenie.getGlobalAverage(), _sim.globalCasCakaniaOsetrenie.getConfidenceIntervalHalfWidth(),
                    _sim.globalCasCakaniaVstup.getGlobalAverage(), _sim.globalCasCakaniaVstup.getConfidenceIntervalHalfWidth(),
                    _sim.globalRadSanitka.getGlobalAverage(),
                    _sim.globalRadSamostatne.getGlobalAverage(),
                    _sim.globalVyuzitieLekar.getGlobalAverage(),
                    _sim.globalVyuzitieSestra.getGlobalAverage()
                );
                _guiLogic.showResults(results);
            });

            _sim.simulate((int) settings[1], (double) settings[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
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