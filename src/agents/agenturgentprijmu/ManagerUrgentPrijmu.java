package agents.agenturgentprijmu;

import java.util.List; 
import java.util.ArrayList;

import OSPABA.*;
import agents.agentosetrenia.ManagerOsetrenia;
import agents.agentvstupvysetrenia.ManagerVstupVysetrenia;
import simulation.*;
import entities.*;
import generators.TrojuholnikovyGenerator;
import generators.UniformGenerator;

//meta! id="3"
public class ManagerUrgentPrijmu extends OSPABA.Manager
{
	private TrojuholnikovyGenerator genPresunZVchoduSamostatne;
    private UniformGenerator genPresunZVchoduSanitkou;
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

		genPresunZVchoduSamostatne = new TrojuholnikovyGenerator(120.0, 150.0, 300.0, ((MySimulation)mySim()).masterRandom.nextInt());
		genPresunZVchoduSanitkou = new UniformGenerator(90.0, 200.0, ((MySimulation)mySim()).masterRandom.nextInt());
		genPresunMedziMiestnostami = new TrojuholnikovyGenerator(15.0, 20.0, 45.0, ((MySimulation)mySim()).masterRandom.nextInt());

		//** LLM
		volniLekari = new ArrayList<>();
        volneSestry = new ArrayList<>();
        volneAmbulancieA = new ArrayList<>();
        volneAmbulancieB = new ArrayList<>();

		MySimulation sim = (MySimulation) mySim();

		int pocetLekarov = sim.configPocetLekarov;
		int pocetSestier = sim.configPocetSestier;

		// for (int i = 0; i < pocetLekarov; i++) {
        //     volniLekari.add(new Lekar(i, simulation.Constants.LOKACIA_VCHOD_SANITKA));
        // }

		// for (int i = 0; i < pocetSestier; i++) {
        //     volneSestry.add(new Sestra(i, simulation.Constants.LOKACIA_VCHOD_SANITKA));
        // }

		for (int i = 0; i < pocetLekarov; i++) {
            Lekar lekar = new Lekar(i, simulation.Constants.LOKACIA_VCHOD_SANITKA);
            if (mySim().animatorExists()) {
                lekar.animaciaPracovnika = new OSPAnimator.AnimImageItem("./assets/lekar.png");
                lekar.animaciaPracovnika.setPosition(((MySimulation)mySim()).bodVchodSanitka);
                mySim().animator().register(lekar.animaciaPracovnika);
            }
            volniLekari.add(lekar);
        }

        for (int i = 0; i < pocetSestier; i++) {
            Sestra sestra = new Sestra(i, simulation.Constants.LOKACIA_VCHOD_SANITKA);
            if (mySim().animatorExists()) {
                sestra.animaciaPracovnika = new OSPAnimator.AnimImageItem("./assets/sestra.png");
                sestra.animaciaPracovnika.setPosition(((MySimulation)mySim()).bodVchodSanitka);
                mySim().animator().register(sestra.animaciaPracovnika);
            }
            volneSestry.add(sestra);
        }

		for (int i = 1; i <= 5; i++) {
            volneAmbulancieA.add(new Ambulancia(i, simulation.Constants.AMBULANCIA_TYP_A, "A" + i)); //# CONFIG LOCACIU
        }

		for (int i = 1; i <= 7; i++) {
            volneAmbulancieB.add(new Ambulancia(i, simulation.Constants.AMBULANCIA_TYP_B, "B" + i));
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
            sim.stavAmbulancii.put(pacient.priradenaMiestnost.typ + pacient.priradenaMiestnost.id, "Voľná");
        }

        if (sim.aktualniPacienti.containsKey(pacient.idPacienta)) {
            sim.aktualniPacienti.get(pacient.idPacienta)[2] = "Čaká v rade na ošetrenie";
        }

		pacient.priradenaSestra = null;
        pacient.priradenaMiestnost = null;
		
		ManagerOsetrenia manOsetrenia = (ManagerOsetrenia) ((MySimulation)mySim()).agentOsetrenia().myManager();
		
		manOsetrenia.pridatDoRadu(pacient);

		int pocetVrade = manOsetrenia.pocetCakajucichPriorita(1) + 
                         manOsetrenia.pocetCakajucichPriorita(2) + 
                         manOsetrenia.pocetCakajucichPriorita(3) + 
                         manOsetrenia.pocetCakajucichPriorita(4) + 
                         manOsetrenia.pocetCakajucichPriorita(5) - 1;
                         
        simulation.AnimationHelper.pridatDoRaduOsetrenia(sim, pacient, pocetVrade);

        pridelitZdroje();	
	}

	//meta! sender="AgentOsetrenia", id="18", type="Response"
	public void processVykonatOsetrenie(MessageForm message)
	{
		// System.out.println("13 response is sent from AgentOsetrenia");

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

		sim.stavAmbulancii.put(pacient.priradenaMiestnost.typ + pacient.priradenaMiestnost.id, "Voľná");

		pacient.priradenyLekar = null;
        pacient.priradenaSestra = null;
		pacient.priradenaMiestnost = null;
		
		pridelitZdroje();

		message.setCode(Mc.spracovaniePacienta);
		response(message);
	}

	//meta! sender="ProcesPresunu", id="30", type="Finish"
	public void processFinish(MessageForm message)
	{
		// System.out.println("5 processFinish runs after procesPresunu");

		MyMessage pacient = (MyMessage) message;
		ManagerVstupVysetrenia manVstup = (ManagerVstupVysetrenia) ((MySimulation)mySim()).agentVstupVysetrenia().myManager();
		MySimulation sim = (MySimulation) mySim();

		if (pacient.typPacienta.equals(simulation.Constants.PACIENT_SAMOSTATNE)) {
			manVstup.radVstupVysSamostatne.enqueue(pacient);
			sim.zahrievanieSkonciloCheck(sim.currentTime());
			
			if (sim.currentTime() >= sim.trvanieZahrievania) {
                sim.wstatRadVstupVysSamostatne.update(sim.currentTime(), manVstup.radVstupVysSamostatne.size());
            }

			if (sim.animatorExists() && pacient.animaciaPacienta != null) {
				if (pacient.typPacienta.equals(simulation.Constants.PACIENT_SAMOSTATNE)) {
					sim.animRadSamostatne.insert(pacient.animaciaPacienta);
				} else {
					sim.animRadSanitka.insert(pacient.animaciaPacienta);
				}
			}

		} else {
			manVstup.radSantikouVstupVysetrenie.enqueue(pacient);

			sim.zahrievanieSkonciloCheck(sim.currentTime());
            if (sim.currentTime() >= sim.trvanieZahrievania) {
                sim.wstatradVstupVysetrenieSanitkou.update(sim.currentTime(), manVstup.radSantikouVstupVysetrenie.size());
            }

			if (sim.animatorExists() && pacient.animaciaPacienta != null) {
				if (pacient.typPacienta.equals(simulation.Constants.PACIENT_SAMOSTATNE)) {
					sim.animRadSamostatne.insert(pacient.animaciaPacienta);
				} else {
					sim.animRadSanitka.insert(pacient.animaciaPacienta);
				}
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
		// System.out.println("3 processSpracovaniePacienta");
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
        //??
		if (simulation.Constants.LOKACIA_VCHOD_SAMOSTATNE.equals(od) || simulation.Constants.LOKACIA_VCHOD_SAMOSTATNE.equals(kam)) {
			return genPresunZVchoduSamostatne.sample();
		} else if (simulation.Constants.LOKACIA_VCHOD_SANITKA.equals(od) || simulation.Constants.LOKACIA_VCHOD_SANITKA.equals(kam)) {
            return genPresunZVchoduSanitkou.sample();
        }

		return genPresunMedziMiestnostami.sample();
	}

    private Lekar najdiLekaraPreLokaciu(String cielovaLokacia) {
        for (int i = 0; i < volniLekari.size(); i++) {
            if (volniLekari.get(i).lokacia.equals(cielovaLokacia)) {
                return volniLekari.remove(i);
            }
        }
        return volniLekari.remove(0);
    }

    private Sestra najdiSestruPreLokaciu(String cielovaLokacia) {
        for (int i = 0; i < volneSestry.size(); i++) {
            if (volneSestry.get(i).lokacia.equals(cielovaLokacia)) {
                return volneSestry.remove(i);
            }
        }
        return volneSestry.remove(0);
    }


	public void pridelitZdroje() 
	{
		boolean pracoval = true;  //#
        MySimulation sim = (MySimulation) mySim();

        while (pracoval) {
            pracoval = false;
            
            switch (sim.configZvolenyRezim) {
                case 1:
                    pracoval = strategies.StrategiePrideleniaZdrojov.pridelitZdrojeRezim1(this);
                    break;
                case 2:
                    pracoval = strategies.StrategiePrideleniaZdrojov.pridelitZdrojeRezim2(this);
                    break;
                case 3:
                    pracoval = strategies.StrategiePrideleniaZdrojov.pridelitZdrojeRezim3(this);
                    break;
                case 5:
                    pracoval = strategies.StrategiePrideleniaZdrojov.pridelitZdrojeRezim5(this);
                    break;
                default:
                    pracoval = strategies.StrategiePrideleniaZdrojov.pridelitZdrojeRezim1(this);
                    break;
            }
        }
	}

	//#
    public void startOsetrenie(MyMessage pacient, String typAmbulancie) {
        Ambulancia miestnost = typAmbulancie.equals("A") ? volneAmbulancieA.remove(0) : volneAmbulancieB.remove(0);
        Lekar lekar = najdiLekaraPreLokaciu(miestnost.lokacia);
        Sestra sestra = najdiSestruPreLokaciu(miestnost.lokacia);

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
            
			if (pacient.typPacienta.equals(simulation.Constants.PACIENT_SAMOSTATNE)) {
                sim.statCakanieSamostatne.addValue(casCakania);
            } else {
                sim.statCakanieSanitka.addValue(casCakania);
            }

			int busyDoctors = sim.configPocetLekarov - volniLekari.size();
            sim.wstatVyuzitieLekar.update(sim.currentTime(), busyDoctors);
			int busySestry = sim.configPocetSestier - volneSestry.size();
			sim.wstatVyuzitieSestra.update(sim.currentTime(), busySestry);
        }

		sim.stavAmbulancii.put(miestnost.typ + miestnost.id, "Obsadená (Pacient ID: " + pacient.idPacienta + ")");
        if (sim.aktualniPacienti.containsKey(pacient.idPacienta)) {
            sim.aktualniPacienti.get(pacient.idPacienta)[2] = "Ošetrenie (" + miestnost.typ + miestnost.id + ")";
        }

        pacient.setAddressee(Id.agentOsetrenia);
        pacient.setCode(Mc.vykonatOsetrenie);
        request(pacient);
    }

    public void startVstupVysetrenie(MyMessage pacient) {
		//!!
		MySimulation sim = (MySimulation) mySim();
        if (sim.animatorExists() && pacient.animaciaPacienta != null) {
            if (pacient.typPacienta.equals(simulation.Constants.PACIENT_SAMOSTATNE)) {
                sim.animRadSamostatne.remove(pacient.animaciaPacienta);
            } else {
                sim.animRadSanitka.remove(pacient.animaciaPacienta);
            }
        }

        Ambulancia miestnost = volneAmbulancieB.remove(0);
        Sestra sestra = najdiSestruPreLokaciu(miestnost.lokacia);

        double casPresunuSestry = casPresunu(sestra.lokacia, miestnost.lokacia);
        pacient.casPresunu = casPresunuSestry;
        sestra.lokacia = miestnost.lokacia;
        pacient.priradenaSestra = sestra;
        pacient.priradenaMiestnost = miestnost;

		// MySimulation sim = (MySimulation) mySim();
        ManagerVstupVysetrenia manVstup = (ManagerVstupVysetrenia) ((MySimulation)mySim()).agentVstupVysetrenia().myManager();
        
        sim.zahrievanieSkonciloCheck(sim.currentTime());

        if (sim.currentTime() >= sim.trvanieZahrievania) {
            double casCakaniaTriage = sim.currentTime() - pacient.casPrichodu;
            sim.statCasCakaniaVstup.addValue(casCakaniaTriage);
            
            if (pacient.typPacienta.equals(simulation.Constants.PACIENT_SAMOSTATNE)) {
                sim.wstatRadVstupVysSamostatne.update(sim.currentTime(), manVstup.radVstupVysSamostatne.size());
            } else {
                sim.wstatradVstupVysetrenieSanitkou.update(sim.currentTime(), manVstup.radSantikouVstupVysetrenie.size());
            }
			
			int busySestry = sim.configPocetSestier - volneSestry.size();
			sim.wstatVyuzitieSestra.update(sim.currentTime(), busySestry);
        }

		sim.stavAmbulancii.put(miestnost.typ + miestnost.id, "Obsadená (Pacient ID: " + pacient.idPacienta + ")");

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
