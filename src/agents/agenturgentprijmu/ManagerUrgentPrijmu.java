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

	//meta! sender="AgentVstupVysetrenia", id="34", type="Response"
	public void processVykonatVstupOsetrenie(MessageForm message)
	{
	}

	//meta! sender="AgentOsetrenia", id="36", type="Response"
	public void processVykonatOsetrenie(MessageForm message)
	{
	}

	//meta! sender="AgentModelu", id="28", type="Request"
	public void processSpracovatPacienta(MessageForm message)
	{
	}

	//meta! sender="ProcesPresunu", id="55", type="Finish"
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
		case Mc.vykonatOsetrenie:
			processVykonatOsetrenie(message);
		break;

		case Mc.finish:
			processFinish(message);
		break;

		case Mc.vykonatVstupOsetrenie:
			processVykonatVstupOsetrenie(message);
		break;

		case Mc.spracovatPacienta:
			processSpracovatPacienta(message);
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
