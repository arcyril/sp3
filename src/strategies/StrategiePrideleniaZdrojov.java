package strategies;

import agents.agenturgentprijmu.ManagerUrgentPrijmu;
import agents.agentosetrenia.ManagerOsetrenia;
import agents.agentvstupvysetrenia.ManagerVstupVysetrenia;
import simulation.MySimulation;
import simulation.MyMessage;

public class StrategiePrideleniaZdrojov {
    public static boolean pridelitZdrojeRezim1(ManagerUrgentPrijmu manUrgent)
    {
        MySimulation sim = (MySimulation) manUrgent.mySim();
        ManagerOsetrenia manOsetrenia = (ManagerOsetrenia) sim.agentOsetrenia().myManager();
        ManagerVstupVysetrenia manVstup = (ManagerVstupVysetrenia) sim.agentVstupVysetrenia().myManager();
            
        MyMessage pacientNaOsetrenie = manOsetrenia.peekDalsiPacient();

        int dostupneSestryOsetrenie = manUrgent.volneSestry.size() - (sim.configRezervovatSestruAmbulanciuB ? 1 : 0);
        int dostupneAmbBOsetrenie = manUrgent.volneAmbulancieB.size() - (sim.configRezervovatSestruAmbulanciuB ? 1 : 0);

        if (pacientNaOsetrenie != null) {
            int priorita = pacientNaOsetrenie.priorita;

            if (priorita == 1 || priorita == 2) {
                if (manUrgent.volniLekari.size() > 0 && dostupneSestryOsetrenie > 0 && manUrgent.volneAmbulancieA.size() > 0) {
                    manUrgent.startOsetrenie(manOsetrenia.dalsiPacient(), "A");
                    return true;
                }
            } 
            else if (priorita == 3 || priorita == 4) {
                if (manUrgent.volniLekari.size() >= 2 && dostupneSestryOsetrenie >= 2) {
                    if (dostupneAmbBOsetrenie > 0) {
                        manUrgent.startOsetrenie(manOsetrenia.dalsiPacient(), "B");
                        return true;
                    } else if (manUrgent.volneAmbulancieA.size() > 0) {
                        manUrgent.startOsetrenie(manOsetrenia.dalsiPacient(), "A");
                        return true;
                    }
                }
            } 
            else if (priorita == 5) {
                if (manUrgent.volniLekari.size() >= 3 && dostupneSestryOsetrenie >= 3 && dostupneAmbBOsetrenie > 0) {
                    manUrgent.startOsetrenie(manOsetrenia.dalsiPacient(), "B");
                    return true;
                }
            }
        }

        if (manUrgent.volneSestry.size() > 0 && manUrgent.volneAmbulancieB.size() > 0) {
            if (!manVstup.radSantikouVstupVysetrenie.isEmpty()) {
                manUrgent.startVstupVysetrenie(manVstup.radSantikouVstupVysetrenie.dequeue());
                return true;
            } else if (dostupneSestryOsetrenie >= 2 && !manVstup.radVstupVysSamostatne.isEmpty()) {
                manUrgent.startVstupVysetrenie(manVstup.radVstupVysSamostatne.dequeue());
                return true;
            }
        }
        return false;
    }

    public static boolean pridelitZdrojeRezim2(ManagerUrgentPrijmu manUrgent)
    {
        MySimulation sim = (MySimulation) manUrgent.mySim();
        ManagerOsetrenia manOsetrenia = (ManagerOsetrenia) sim.agentOsetrenia().myManager();
        ManagerVstupVysetrenia manVstup = (ManagerVstupVysetrenia) sim.agentVstupVysetrenia().myManager();

        MyMessage pacientNaOsetrenie = manOsetrenia.peekDalsiPacient();

        int dostupneSestryOsetrenie = manUrgent.volneSestry.size() - (sim.configRezervovatSestruAmbulanciuB ? 1 : 0);
        int dostupneAmbBOsetrenie = manUrgent.volneAmbulancieB.size() - (sim.configRezervovatSestruAmbulanciuB ? 1 : 0);

        if (pacientNaOsetrenie != null) {
            int priorita = pacientNaOsetrenie.priorita;

            if (priorita == 1 || priorita == 2) {
                if (manUrgent.volniLekari.size() > 0 && dostupneSestryOsetrenie > 0 && manUrgent.volneAmbulancieA.size() > 0) {
                    manUrgent.startOsetrenie(manOsetrenia.dalsiPacient(), "A");
                    return true;
                }
            } 
            else if (priorita == 3 || priorita == 4) {
                if (manUrgent.volniLekari.size() > 0 && dostupneSestryOsetrenie > 0) {
                    if (dostupneAmbBOsetrenie > 0) {
                        manUrgent.startOsetrenie(manOsetrenia.dalsiPacient(), "B");
                        return true;
                    } else if (manUrgent.volneAmbulancieA.size() > 0) {
                        manUrgent.startOsetrenie(manOsetrenia.dalsiPacient(), "A");
                        return true;
                    }
                }
            } 
            else if (priorita == 5) {
                if (manUrgent.volniLekari.size() > 0 && dostupneSestryOsetrenie > 0 && dostupneAmbBOsetrenie > 0) {
                    manUrgent.startOsetrenie(manOsetrenia.dalsiPacient(), "B");
                    return true;
                }
            }
            
            return false; 
        }

        if (manUrgent.volneSestry.size() > 0 && manUrgent.volneAmbulancieB.size() > 0) {
            if (!manVstup.radSantikouVstupVysetrenie.isEmpty()) {
                manUrgent.startVstupVysetrenie(manVstup.radSantikouVstupVysetrenie.dequeue());
                return true;
            } else if (!manVstup.radVstupVysSamostatne.isEmpty()) {
                manUrgent.startVstupVysetrenie(manVstup.radVstupVysSamostatne.dequeue());
                return true;
            }
        }

        return false;
    }

    public static boolean pridelitZdrojeRezim3(ManagerUrgentPrijmu manUrgent)
    {
        MySimulation sim = (MySimulation) manUrgent.mySim();
        ManagerOsetrenia manOsetrenia = (ManagerOsetrenia) sim.agentOsetrenia().myManager();
        ManagerVstupVysetrenia manVstup = (ManagerVstupVysetrenia) sim.agentVstupVysetrenia().myManager();

        int dostupneSestryOsetrenie = manUrgent.volneSestry.size() - (sim.configRezervovatSestruAmbulanciuB ? 1 : 0);
        int dostupneAmbBOsetrenie = manUrgent.volneAmbulancieB.size() - (sim.configRezervovatSestruAmbulanciuB ? 1 : 0);

        MyMessage pacientNaOsetrenie = manOsetrenia.peekDalsiPacient();

        if (pacientNaOsetrenie != null) {
            int priorita = pacientNaOsetrenie.priorita;

            if (priorita == 1 || priorita == 2) {
                if (manUrgent.volniLekari.size() > 0 && dostupneSestryOsetrenie > 0 && manUrgent.volneAmbulancieA.size() > 0) {
                    manUrgent.startOsetrenie(manOsetrenia.dalsiPacient(), "A");
                    return true;
                }
            } else if (priorita == 3) {
                if (manUrgent.volniLekari.size() > 0 && dostupneSestryOsetrenie > 0) {
                    if (dostupneAmbBOsetrenie > 0) {
                        manUrgent.startOsetrenie(manOsetrenia.dalsiPacient(), "B");
                        return true;
                    } else if (manUrgent.volneAmbulancieA.size() > 0) {
                        manUrgent.startOsetrenie(manOsetrenia.dalsiPacient(), "A");
                        return true;
                    }
                }
            }
            
            if (manUrgent.volneSestry.size() > 0 && manUrgent.volneAmbulancieB.size() > 0 && !manVstup.radSantikouVstupVysetrenie.isEmpty()) {
                manUrgent.startVstupVysetrenie(manVstup.radSantikouVstupVysetrenie.dequeue());
                return true;
            }

            if (priorita == 4) {
                if (manUrgent.volniLekari.size() > 0 && dostupneSestryOsetrenie > 0) {
                    if (dostupneAmbBOsetrenie > 0) {
                        manUrgent.startOsetrenie(manOsetrenia.dalsiPacient(), "B");
                        return true;
                    } else if (manUrgent.volneAmbulancieA.size() > 0) {
                        manUrgent.startOsetrenie(manOsetrenia.dalsiPacient(), "A");
                        return true;
                    }
                }
            } else if (priorita == 5) {
                if (manUrgent.volniLekari.size() > 0 && dostupneSestryOsetrenie > 0 && dostupneAmbBOsetrenie > 0) {
                    manUrgent.startOsetrenie(manOsetrenia.dalsiPacient(), "B");
                    return true;
                }
            }
        }

        if (manUrgent.volneSestry.size() > 0 && manUrgent.volneAmbulancieB.size() > 0) {
            if (!manVstup.radSantikouVstupVysetrenie.isEmpty()) {
                manUrgent.startVstupVysetrenie(manVstup.radSantikouVstupVysetrenie.dequeue());
                return true;
            } 
            else if (manUrgent.volneSestry.size() >= 2 && !manVstup.radVstupVysSamostatne.isEmpty()) {
                manUrgent.startVstupVysetrenie(manVstup.radVstupVysSamostatne.dequeue());
                return true;
            }
        }

        return false;
    }

    public static boolean pridelitZdrojeRezim5(ManagerUrgentPrijmu manUrgent)
    {
        MySimulation sim = (MySimulation) manUrgent.mySim();
        ManagerOsetrenia manOsetrenia = (ManagerOsetrenia) sim.agentOsetrenia().myManager();
        ManagerVstupVysetrenia manVstup = (ManagerVstupVysetrenia) sim.agentVstupVysetrenia().myManager();
        
        int dostupneSestryOsetrenie = manUrgent.volneSestry.size() - (sim.configRezervovatSestruAmbulanciuB ? 1 : 0);
        int dostupneAmbBOsetrenie = manUrgent.volneAmbulancieB.size() - (sim.configRezervovatSestruAmbulanciuB ? 1 : 0);

        boolean jePretlak = (manOsetrenia.pocetCakajucichPriorita(3) + manOsetrenia.pocetCakajucichPriorita(4)) > 5;

        MyMessage pacientNaOsetrenie = manOsetrenia.peekDalsiPacient();

        if (pacientNaOsetrenie != null) {
            int priorita = pacientNaOsetrenie.priorita;

            if (priorita == 1 || priorita == 2) {
                if (manUrgent.volniLekari.size() > 0 && dostupneSestryOsetrenie > 0 && manUrgent.volneAmbulancieA.size() > 0) {
                    manUrgent.startOsetrenie(manOsetrenia.dalsiPacient(), "A");
                    return true;
                }
            } 
            else if (priorita == 3 || priorita == 4) {
                int minimumLekarov = jePretlak ? 1 : 2;
                int minimumSestier = jePretlak ? 1 : 2;

                if (manUrgent.volniLekari.size() >= minimumLekarov && dostupneSestryOsetrenie >= minimumSestier) {
                    if (dostupneAmbBOsetrenie > 0) {
                        manUrgent.startOsetrenie(manOsetrenia.dalsiPacient(), "B");
                        return true;
                    } else if (manUrgent.volneAmbulancieA.size() > 0) {
                        manUrgent.startOsetrenie(manOsetrenia.dalsiPacient(), "A");
                        return true;
                    }
                }
            } 
            else if (priorita == 5) {
                if (manUrgent.volniLekari.size() >= 3 && dostupneSestryOsetrenie >= 3 && dostupneAmbBOsetrenie > 0) {
                    manUrgent.startOsetrenie(manOsetrenia.dalsiPacient(), "B");
                    return true;
                }
            }
        }

        if (manUrgent.volneSestry.size() > 0 && manUrgent.volneAmbulancieB.size() > 0) {
            if (!manVstup.radSantikouVstupVysetrenie.isEmpty()) {
                manUrgent.startVstupVysetrenie(manVstup.radSantikouVstupVysetrenie.dequeue());
                return true;
            } 
            else if (manUrgent.volneSestry.size() >= 2 && !manVstup.radVstupVysSamostatne.isEmpty()) {
                manUrgent.startVstupVysetrenie(manVstup.radVstupVysSamostatne.dequeue());
                return true;
            }
        }

        return false;
    }

}