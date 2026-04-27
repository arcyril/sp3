package agents.agenturgentprijmu;

import OSPABA.*;
import simulation.*;

//meta! id="3"
public class ManagerUrgentPrijmu extends OSPABA.Manager
{
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
	}

	//meta! sender="AgentVstupVysetrenia", id="17", type="Response"
	public void processVykonatVstupOsetrenie(MessageForm message)
	{
	}

	//meta! sender="AgentOsetrenia", id="18", type="Response"
	public void processVykonatOsetrenie(MessageForm message)
	{
	}

	//meta! sender="ProcesPresunu", id="30", type="Finish"
	public void processFinish(MessageForm message)
	{
	}

	//meta! sender="AgentModelu", id="16", type="Request"
	public void processSpracovaniePacienta(MessageForm message)
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
