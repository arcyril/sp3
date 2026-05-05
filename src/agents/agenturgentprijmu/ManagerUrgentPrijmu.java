package agents.agenturgentprijmu;

import java.util.List; 
import java.util.ArrayList;

import OSPABA.*;
import agents.agentosetrenia.ManagerOsetrenia;
import agents.agentvstupvysetrenia.ManagerVstupVysetrenia;
import simulation.*;
import entities.*;

//meta! id="3"
public class ManagerUrgentPrijmu extends OSPABA.Manager
{
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

		//** LLM
		volniLekari = new ArrayList<>();
        volneSestry = new ArrayList<>();
        volneAmbulancieA = new ArrayList<>();
        volneAmbulancieB = new ArrayList<>();

		//# CONFIGURE FROM GUI
		int pocetLekarov = 5;
		int pocetSestier = 10;

		for (int i = 0; i < pocetLekarov; i++) {
            volniLekari.add(new Lekar(i, "VCHOD"));
        }

		for (int i = 0; i < pocetSestier; i++) {
            volneSestry.add(new Sestra(i, "VCHOD"));
        }

		for (int i = 1; i <= 5; i++) {
            volneAmbulancieA.add(new Ambulancia(i, "A", "A1")); //# CONFIGURE LOCATION
        }

		for (int i = 1; i <= 7; i++) {
            volneAmbulancieB.add(new Ambulancia(i, "B", "B1"));
        }
	}

	//meta! sender="AgentVstupVysetrenia", id="17", type="Response"
	public void processVykonatVstupOsetrenie(MessageForm message)
	{
		// MyMessage pacient = (MyMessage) message;
		// volneSestry.add(pacient.priradenaSestra);
		// volneAmbulancieB.add(pacient.priradenaMiestnost);
		MyMessage pacient = (MyMessage) message;

		volneSestry.add(pacient.priradenaSestra);
        volneAmbulancieB.add(pacient.priradenaMiestnost);

		pacient.priradenaSestra = null;
        pacient.priradenaMiestnost = null;
		
		ManagerOsetrenia manOsetrenia = (ManagerOsetrenia) ((MySimulation)mySim()).agentOsetrenia().myManager();
        manOsetrenia.pridatDoRadu(pacient);

		pridelPracu();
		
	}

	//meta! sender="AgentOsetrenia", id="18", type="Response"
	public void processVykonatOsetrenie(MessageForm message)
	{
		pridelPracu();
	}

	//meta! sender="ProcesPresunu", id="30", type="Finish"
	public void processFinish(MessageForm message)
	{
		MyMessage pacient = (MyMessage) message;

		ManagerVstupVysetrenia manVstup = (ManagerVstupVysetrenia) ((MySimulation)mySim()).agentVstupVysetrenia().myManager();
		if (pacient.typPacienta.equals("SAMOSTATNE")) {
			manVstup.radSamostatne.enqueue(pacient);
		} else {
			manVstup.radSanitkou.enqueue(pacient);
		}
		pridelPracu();

		//# GUI
		if (((MySimulation)mySim()).aktualniPacienti.containsKey(pacient.idPacienta)) {
			((MySimulation)mySim()).aktualniPacienti.get(pacient.idPacienta)[2] = "čaka v rade na vyšetrenie";
		}
		((MySimulation)mySim()).refreshUI();

		//!! FINISH
		message.setAddressee(Id.agentVstupVysetrenia);
		message.setCode(Mc.vykonatVstupOsetrenie);
		request(message);
	}

	//meta! sender="AgentModelu", id="16", type="Request"
	public void processSpracovaniePacienta(MessageForm message)
	{
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

	public void pridelPracu() {
		// if (volneSestry.isEmpty()) {
		// 	return;
		// }

		// //??
		// boolean cakajuciPacienti = ((ManagerOsetrenia)((MySimulation)mySim()).agentOsetrenia().myManager()).cakajuciPacienti();
		// if (cakajuciPacienti && !volniLekari.isEmpty() && !volneSestry.isEmpty() /* //# has right room */) {

		// }

		ManagerVstupVysetrenia manVstup = (ManagerVstupVysetrenia) ((MySimulation)mySim()).agentVstupVysetrenia().myManager();

		if (!volneSestry.isEmpty() && !manVstup.radSamostatne.isEmpty()) {
        
        MyMessage pacient = manVstup.radSamostatne.dequeue();
        pacient.priradenaSestra = volneSestry.remove(0);

        pacient.setAddressee(Id.agentVstupVysetrenia);
        pacient.setCode(Mc.vykonatVstupOsetrenie);
        request(pacient); 
    }
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
