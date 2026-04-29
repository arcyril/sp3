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
                _guiLogic.onRefreshUI(sim);
            });
            _sim.onReplicationWillStart((sim) -> {
                Object[] guiSettings = _guiLogic.getSettings();

                int duration = (int) guiSettings[2];
                int interval = (int) guiSettings[3];
                boolean maxSpeed = (boolean) guiSettings[6];

                setSimSpeed(duration, interval, maxSpeed);
                // if ((boolean) guiSettings[8]) {
                //     _guiLogic.btnCreateAnim();
                // } else {
                    // _sim.setSpeed(guiSettings);
                // }
            });
            _sim.onReplicationDidFinish((sim) -> {
                _guiLogic.onRefreshUI(sim);
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