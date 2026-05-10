package agents.agenturgentprijmu;

import java.util.List; 
import java.util.ArrayList;

import OSPABA.*;
import agents.agentosetrenia.ManagerOsetrenia;
import agents.agentvstupvysetrenia.ManagerVstupVysetrenia;
import simulation.*;
import entities.*;
import generators.TrojuholnikovyGenerator;

//meta! id="3"
public class ManagerUrgentPrijmu extends OSPABA.Manager
{
	private TrojuholnikovyGenerator genPresunZVchodu;
	private TrojuholnikovyGenerator genPresunMedziMiestnostami;


	public List<Lekar> volniLekari;
		public List<Sestra> volneSestry;
		public List<Ambulancia> volneAmbulancieA;
		public List<Ambulancia> volneAmbulancieB;

	public ManagerUrgentPrijmu(int id, Simulation mySim, Agent myAgent)
	{
		super(id, mySim, myAgent);
		init();
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication

		if (petriNet() != null)
		{
			petriNet().clear();
		}

		genPresunZVchodu = new TrojuholnikovyGenerator(120.0, 150.0, 300.0, ((MySimulation)mySim()).masterRandom.nextInt());
		genPresunMedziMiestnostami = new TrojuholnikovyGenerator(15.0, 20.0, 45.0, ((MySimulation)mySim()).masterRandom.nextInt());

		//** LLM
		volniLekari = new ArrayList<>();
        volneSestry = new ArrayList<>();
        volneAmbulancieA = new ArrayList<>();
        volneAmbulancieB = new ArrayList<>();

		MySimulation sim = (MySimulation) mySim();

		int pocetLekarov = sim.configPocetLekarov;
		int pocetSestier = sim.configPocetSestier;
		rezim1Aktivny = sim.configRezim1Aktivny;

		for (int i = 0; i < pocetLekarov; i++) {
            volniLekari.add(new Lekar(i, "VCHOD"));
        }

		for (int i = 0; i < pocetSestier; i++) {
            volneSestry.add(new Sestra(i, "VCHOD"));
        }

		for (int i = 1; i <= 5; i++) {
            volneAmbulancieA.add(new Ambulancia(i, "A", "A" + i)); //# CONFIGURE LOCATION
        }

		for (int i = 1; i <= 7; i++) {
            volneAmbulancieB.add(new Ambulancia(i, "B", "B" + i));
        }
	}

	//meta! sender="AgentVstupVysetrenia", id="17", type="Response"
	public void processVykonatVstupOsetrenie(MessageForm message)
	{
		MyMessage pacient = (MyMessage) message;
		MySimulation sim = (MySimulation) mySim();

		volneSestry.add(pacient.priradenaSestra);

		sim.zahrievanieSkonciloCheck(sim.currentTime());
        if (sim.currentTime() >= sim.trvanieZahrievania) {
            int busySestry = sim.configPocetSestier - volneSestry.size();
            sim.wstatVyuzitieSestra.update(sim.currentTime(), busySestry);
        }

		if (pacient.priradenaMiestnost != null) {
			volneAmbulancieB.add(pacient.priradenaMiestnost);
		}

		pacient.priradenaSestra = null;
        pacient.priradenaMiestnost = null;
		
		ManagerOsetrenia manOsetrenia = (ManagerOsetrenia) ((MySimulation)mySim()).agentOsetrenia().myManager();
        manOsetrenia.pridatDoRadu(pacient);

		pridelitZdroje();	
	}

	//meta! sender="AgentOsetrenia", id="18", type="Response"
	public void processVykonatOsetrenie(MessageForm message)
	{
		System.out.println("13 response is sent from AgentOsetrenia");

		MyMessage pacient = (MyMessage) message;
		MySimulation sim = (MySimulation) mySim();

		volniLekari.add(pacient.priradenyLekar);
        volneSestry.add(pacient.priradenaSestra);

		sim.zahrievanieSkonciloCheck(sim.currentTime());
        if (sim.currentTime() >= sim.trvanieZahrievania) {
            int busyDoctors = sim.configPocetLekarov - volniLekari.size();
            sim.wstatVyuzitieLekar.update(sim.currentTime(), busyDoctors);
			int busySestry = sim.configPocetSestier - volneSestry.size();
            sim.wstatVyuzitieSestra.update(sim.currentTime(), busySestry);
        }

		if (pacient.priradenaMiestnost != null) {
			if ("A".equals(pacient.priradenaMiestnost.typ)) {
				volneAmbulancieA.add(pacient.priradenaMiestnost);
			} else if ("B".equals(pacient.priradenaMiestnost.typ)) {
				volneAmbulancieB.add(pacient.priradenaMiestnost);
			}
		}

		pacient.priradenyLekar = null;
        pacient.priradenaSestra = null;
		pacient.priradenaMiestnost = null;

		message.setCode(Mc.spracovaniePacienta);
		response(message);

		pridelitZdroje();
	}

	//meta! sender="ProcesPresunu", id="30", type="Finish"
	public void processFinish(MessageForm message)
	{
		System.out.println("5 processFinish runs after procesPresunu");

		MyMessage pacient = (MyMessage) message;
		ManagerVstupVysetrenia manVstup = (ManagerVstupVysetrenia) ((MySimulation)mySim()).agentVstupVysetrenia().myManager();
		MySimulation sim = (MySimulation) mySim();

		if (pacient.typPacienta.equals("SAMOSTATNE")) {
			manVstup.radVstupVysSamostatne.enqueue(pacient);
			sim.zahrievanieSkonciloCheck(sim.currentTime());
			
			if (sim.currentTime() >= sim.trvanieZahrievania) {
                sim.wstatRadVstupVysSamostatne.update(sim.currentTime(), manVstup.radVstupVysSamostatne.size());
            }

		} else {
			manVstup.radSantikouVstupVysetrenie.enqueue(pacient);

			sim.zahrievanieSkonciloCheck(sim.currentTime());
            if (sim.currentTime() >= sim.trvanieZahrievania) {
                sim.wstatradVstupVysetrenieSanitkou.update(sim.currentTime(), manVstup.radSantikouVstupVysetrenie.size());
            }
		}
		
		//# GUI
		if (((MySimulation)mySim()).aktualniPacienti.containsKey(pacient.idPacienta)) {
			((MySimulation)mySim()).aktualniPacienti.get(pacient.idPacienta)[2] = "čaka v rade na vyšetrenie";
		}
		((MySimulation)mySim()).refreshUI();
		
		pridelitZdroje();
	}

	//meta! sender="AgentModelu", id="16", type="Request"
	public void processSpracovaniePacienta(MessageForm message)
	{
		System.out.println("3 processSpracovaniePacienta");
		//!! PROCESS PRESUNU
		message.setAddressee(Id.procesPresunu);
		startContinualAssistant(message);
	}

	//meta! userInfo="Process messages defined in code", id="0"
	public void processDefault(MessageForm message)
	{
		switch (message.code())
		{
		}
	}

	private double casPresunu(String od, String kam)
	{
		if (od == null || kam == null) {
			return 0.0;
		}

		if (od.equals(kam)) {
			return 0.0;
		}

		if ("VCHOD".equals(od) || "VCHOD".equals(kam)) {
			return genPresunZVchodu.sample();
		}

		return genPresunMedziMiestnostami.sample();
	}

	public boolean rezim1Aktivny = true;

	public void pridelitZdroje() 
	{
		boolean pracoval = true; //#

		while (pracoval) {
            pracoval = false;
            
            if (rezim1Aktivny) {
                pracoval = pridelitZdrojeRezim1();
            } else {
                pracoval = pridelitZdrojeRezim2();
            }
        }
	}

	private boolean pridelitZdrojeRezim1() 
    {
        ManagerOsetrenia manOsetrenia = (ManagerOsetrenia) ((MySimulation)mySim()).agentOsetrenia().myManager();
        ManagerVstupVysetrenia manVstup = (ManagerVstupVysetrenia) ((MySimulation)mySim()).agentVstupVysetrenia().myManager();
            
        MyMessage pacientNaOsetrenie = manOsetrenia.peekDalsiPacient();

        if (pacientNaOsetrenie != null) {
            int priorita = pacientNaOsetrenie.priorita;

            if (priorita == 1 || priorita == 2) {
                if (volniLekari.size() > 0 && volneSestry.size() > 0 && volneAmbulancieA.size() > 0) {
                    startOsetrenie(manOsetrenia.dalsiPacient(), "A");
                    return true;
                }
            } 
            else if (priorita == 3 || priorita == 4) {
                if (volniLekari.size() >= 2 && volneSestry.size() >= 2) {
                    if (volneAmbulancieB.size() > 0) {
                        startOsetrenie(manOsetrenia.dalsiPacient(), "B");
                        return true;
                    } else if (volneAmbulancieA.size() > 0) {
                        startOsetrenie(manOsetrenia.dalsiPacient(), "A");
                        return true;
                    }
                }
            } 
            else if (priorita == 5) {
                if (volniLekari.size() >= 3 && volneSestry.size() >= 3 && volneAmbulancieB.size() > 0) {
                    startOsetrenie(manOsetrenia.dalsiPacient(), "B");
                    return true;
                }
            }
        }

        if (volneSestry.size() > 0 && volneAmbulancieB.size() > 0) {
            if (!manVstup.radSantikouVstupVysetrenie.isEmpty()) {
                startVstupVysetrenie(manVstup.radSantikouVstupVysetrenie.dequeue());
                return true;
            } else if (!manVstup.radVstupVysSamostatne.isEmpty()) {
                startVstupVysetrenie(manVstup.radVstupVysSamostatne.dequeue());
                return true;
            }
        }

        return false;
    }

    private boolean pridelitZdrojeRezim2() 
    {
        ManagerOsetrenia manOsetrenia = (ManagerOsetrenia) ((MySimulation)mySim()).agentOsetrenia().myManager();
        ManagerVstupVysetrenia manVstup = (ManagerVstupVysetrenia) ((MySimulation)mySim()).agentVstupVysetrenia().myManager();

        MyMessage pacientNaOsetrenie = manOsetrenia.peekDalsiPacient();

        if (pacientNaOsetrenie != null) {
            int priorita = pacientNaOsetrenie.priorita;

            if (priorita == 1 || priorita == 2) {
                if (volniLekari.size() > 0 && volneSestry.size() > 0 && volneAmbulancieA.size() > 0) {
                    startOsetrenie(manOsetrenia.dalsiPacient(), "A");
                    return true;
                }
            } 
            else if (priorita == 3 || priorita == 4) {
                if (volniLekari.size() > 0 && volneSestry.size() > 0) {
                    if (volneAmbulancieB.size() > 0) {
                        startOsetrenie(manOsetrenia.dalsiPacient(), "B");
                        return true;
                    } else if (volneAmbulancieA.size() > 0) {
                        startOsetrenie(manOsetrenia.dalsiPacient(), "A");
                        return true;
                    }
                }
            } 
            else if (priorita == 5) {
                if (volniLekari.size() > 0 && volneSestry.size() > 0 && volneAmbulancieB.size() > 0) {
                    startOsetrenie(manOsetrenia.dalsiPacient(), "B");
                    return true;
                }
            }
            
            return false; 
        }

        if (volneSestry.size() > 0 && volneAmbulancieB.size() > 0) {
            if (!manVstup.radSantikouVstupVysetrenie.isEmpty()) {
                startVstupVysetrenie(manVstup.radSantikouVstupVysetrenie.dequeue());
                return true;
            } else if (!manVstup.radVstupVysSamostatne.isEmpty()) {
                startVstupVysetrenie(manVstup.radVstupVysSamostatne.dequeue());
                return true;
            }
        }

        return false;
    }

	//#
    private void startOsetrenie(MyMessage pacient, String typAmbulancie) {
        Lekar lekar = volniLekari.remove(0);
        Sestra sestra = volneSestry.remove(0);
        Ambulancia miestnost = typAmbulancie.equals("A") ? volneAmbulancieA.remove(0) : volneAmbulancieB.remove(0);

        double casPresunuLekar = casPresunu(lekar.lokacia, miestnost.lokacia);
        double casPresunuSestra = casPresunu(sestra.lokacia, miestnost.lokacia);

        pacient.casPresunu = Math.max(casPresunuLekar, casPresunuSestra);

        lekar.lokacia = miestnost.lokacia;
        sestra.lokacia = miestnost.lokacia;

        pacient.priradenyLekar = lekar;
        pacient.priradenaSestra = sestra;
        pacient.priradenaMiestnost = miestnost;

		MySimulation sim = (MySimulation) mySim();
		sim.zahrievanieSkonciloCheck(sim.currentTime());

        if (sim.currentTime() >= sim.trvanieZahrievania) {
            double casCakania = sim.currentTime() - pacient.casPrichodu;
            
			if (pacient.typPacienta.equals("SAMOSTATNE")) {
                sim.statCakanieSamostatne.addValue(casCakania);
            } else {
                sim.statCakanieSanitka.addValue(casCakania);
            }

			int busyDoctors = sim.configPocetLekarov - volniLekari.size();
            sim.wstatVyuzitieLekar.update(sim.currentTime(), busyDoctors);
			int busySestry = sim.configPocetSestier - volneSestry.size();
			sim.wstatVyuzitieSestra.update(sim.currentTime(), busySestry);
        }

        pacient.setAddressee(Id.agentOsetrenia);
        pacient.setCode(Mc.vykonatOsetrenie);
        request(pacient);
    }

    private void startVstupVysetrenie(MyMessage pacient) {
        Sestra sestra = volneSestry.remove(0);
        Ambulancia miestnost = volneAmbulancieB.remove(0);

        double casPresunuSestry = casPresunu(sestra.lokacia, miestnost.lokacia);
        pacient.casPresunu = casPresunuSestry;
        sestra.lokacia = miestnost.lokacia;
        pacient.priradenaSestra = sestra;
        pacient.priradenaMiestnost = miestnost;

		MySimulation sim = (MySimulation) mySim();
        ManagerVstupVysetrenia manVstup = (ManagerVstupVysetrenia) ((MySimulation)mySim()).agentVstupVysetrenia().myManager();
        
        sim.zahrievanieSkonciloCheck(sim.currentTime());

        if (sim.currentTime() >= sim.trvanieZahrievania) {
            double casCakaniaTriage = sim.currentTime() - pacient.casPrichodu;
            sim.statCasCakaniaVstup.addValue(casCakaniaTriage);
            
            if (pacient.typPacienta.equals("SAMOSTATNE")) {
                sim.wstatRadVstupVysSamostatne.update(sim.currentTime(), manVstup.radVstupVysSamostatne.size());
            } else {
                sim.wstatradVstupVysetrenieSanitkou.update(sim.currentTime(), manVstup.radSantikouVstupVysetrenie.size());
            }
			
			int busySestry = sim.configPocetSestier - volneSestry.size();
			sim.wstatVyuzitieSestra.update(sim.currentTime(), busySestry);
        }


        pacient.setAddressee(Id.agentVstupVysetrenia);
        pacient.setCode(Mc.vykonatVstupOsetrenie);
        request(pacient);
    }

	//meta! userInfo="Generated code: do not modify", tag="begin"
	public void init()
	{
	}

	@Override
	public void processMessage(MessageForm message)
	{
		switch (message.code())
		{
		case Mc.spracovaniePacienta:
			processSpracovaniePacienta(message);
		break;

		case Mc.vykonatOsetrenie:
			processVykonatOsetrenie(message);
		break;

		case Mc.finish:
			processFinish(message);
		break;

		case Mc.vykonatVstupOsetrenie:
			processVykonatVstupOsetrenie(message);
		break;

		default:
			processDefault(message);
		break;
		}
	}
	//meta! tag="end"

	@Override
	public AgentUrgentPrijmu myAgent()
	{
		return (AgentUrgentPrijmu)super.myAgent();
	}

}
