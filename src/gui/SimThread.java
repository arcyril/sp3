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
            });

            _sim.onSimulationDidFinish((sim) -> {
                String results = String.format("<html><b>Výsledky:</b><br>" +
                    "Priemerný čas v systéme: %.2f &plusmn; %.2f s</html>",
                    _sim.globalCasVSysteme.getGlobalAverage(),
                    _sim.globalCasVSysteme.getConfidenceIntervalHalfWidth()
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