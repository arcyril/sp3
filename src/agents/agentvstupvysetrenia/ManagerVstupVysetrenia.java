package agents.agentvstupvysetrenia;

import OSPABA.*;
import OSPDataStruct.SimQueue;
import simulation.*;

//meta! id="4"
public class ManagerVstupVysetrenia extends OSPABA.Manager
{
	public SimQueue<MyMessage> radSantikouVstupVysetrenie;
    public SimQueue<MyMessage> radVstupVysSamostatne;

	public ManagerVstupVysetrenia(int id, Simulation mySim, Agent myAgent)
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

		radSantikouVstupVysetrenie = new SimQueue<>();
        radVstupVysSamostatne = new SimQueue<>();
	}

	//meta! sender="AgentUrgentPrijmu", id="17", type="Request"
	public void processVykonatVstupOsetrenie(MessageForm message)
	{
		System.out.println("6 processVykonatVstupOsetrenie");
		message.setAddressee(Id.procesVstupVysetrenia);
		startContinualAssistant(message);
	}

	//meta! sender="ProcesVstupVysetrenia", id="35", type="Finish"
	public void processFinish(MessageForm message)
	{
		System.out.println("9 processFinish of ProcesVstupVysetrenia");

		message.setCode(Mc.vykonatVstupOsetrenie);
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
		case Mc.vykonatVstupOsetrenie:
			processVykonatVstupOsetrenie(message);
		break;

		case Mc.finish:
			processFinish(message);
		break;

		default:
			processDefault(message);
		break;
		}
	}
	//meta! tag="end"

	@Override
	public AgentVstupVysetrenia myAgent()
	{
		return (AgentVstupVysetrenia)super.myAgent();
	}

}
