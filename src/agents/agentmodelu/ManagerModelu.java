package agents.agentmodelu;

import OSPABA.*;
import simulation.*;

//meta! id="2"
public class ManagerModelu extends OSPABA.Manager
{
	public ManagerModelu(int id, Simulation mySim, Agent myAgent)
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

	//meta! sender="AgentOkolia", id="13", type="Notice"
	public void processPacientPrisiel(MessageForm message)
	{
		// System.out.println("2 processPacientPrisiel");
		message.setAddressee(Id.agentUrgentPrijmu); //((MySimulation)mySim()).agentUrgentPrijmu()
		message.setCode(Mc.spracovaniePacienta);
		request(message);
	}

	//meta! sender="AgentUrgentPrijmu", id="16", type="Response"
	public void processSpracovaniePacienta(MessageForm message)
	{
		// System.out.println("14 throwing patient out through AgentModelu" + message);
		message.setAddressee(Id.agentOkolia); //((MySimulation)mySim()).agentOkolia()
		message.setCode(Mc.pacientOdisiel);
		notice(message);
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

		case Mc.pacientPrisiel:
			processPacientPrisiel(message);
		break;

		default:
			processDefault(message);
		break;
		}
	}
	//meta! tag="end"

	@Override
	public AgentModelu myAgent()
	{
		return (AgentModelu)super.myAgent();
	}

}
