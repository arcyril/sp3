package agents.agentvstupvysetrenia;

import OSPABA.*;
import simulation.*;

//meta! id="4"
public class ManagerVstupVysetrenia extends OSPABA.Manager
{
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
	}

	//meta! sender="AgentUrgentPrijmu", id="17", type="Request"
	public void processVykonatVstupOsetrenie(MessageForm message)
	{
	}

	//meta! sender="ProcesVstupVysetrenia", id="35", type="Finish"
	public void processFinish(MessageForm message)
	{
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
