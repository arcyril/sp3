package agents.agentosetrenia;

import OSPABA.*;
import OSPDataStruct.SimQueue;
import simulation.*;

//meta! id="5"
public class ManagerOsetrenia extends OSPABA.Manager
{
	private SimQueue<MyMessage>[] radyPodlaPriority;
	
	public ManagerOsetrenia(int id, Simulation mySim, Agent myAgent)
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

		radyPodlaPriority = new SimQueue[5];
		//** LLM */
		for (int i = 0; i < 5; i++) {
            radyPodlaPriority[i] = new SimQueue<>();
        }
	}

	public void pridatDoRadu(MyMessage pacient) {
		int index = pacient.priorita - 1;
		radyPodlaPriority[index].enqueue(pacient);
	}

	public boolean cakajuciPacienti() {
		for (int i = 0; i < radyPodlaPriority.length; i++) {
			if (!radyPodlaPriority[i].isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public MyMessage dalsiPacient() {
		for (int i = 0; i < radyPodlaPriority.length; i++) {
			if (!radyPodlaPriority[i].isEmpty()) {
				return radyPodlaPriority[i].dequeue();
			}
		}
		return null;
	}

	public MyMessage peekDalsiPacient() {
		for (int i = 0; i < radyPodlaPriority.length; i++) {
			if (!radyPodlaPriority[i].isEmpty()) {
				return radyPodlaPriority[i].peek();
			}
		}
		return null;
	}

	public int pocetCakajucichPriorita(int priorita) {
        int index = priorita - 1;
        return radyPodlaPriority[index].size();
    }

	//meta! sender="AgentUrgentPrijmu", id="18", type="Request"
	public void processVykonatOsetrenie(MessageForm message)
	{
		System.out.println("10 processVykonatOsetrenie");
		message.setAddressee(Id.procesOsetrenia);
		startContinualAssistant(message);
	}

	//meta! sender="ProcesOsetrenia", id="37", type="Finish"
	public void processFinish(MessageForm message)
	{
		System.out.println("12 ProcesOsetrenia IS FINISHED");

		message.setCode(Mc.vykonatOsetrenie);
		response(message);
	}

	//meta! userInfo="Process messages defined in code", id="0"
	public void processDefault(MessageForm message)
	{
		switch (message.code())
		{
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
		case Mc.finish:
			processFinish(message);
		break;

		case Mc.vykonatOsetrenie:
			processVykonatOsetrenie(message);
		break;

		default:
			processDefault(message);
		break;
		}
	}
	//meta! tag="end"

	@Override
	public AgentOsetrenia myAgent()
	{
		return (AgentOsetrenia)super.myAgent();
	}

}
