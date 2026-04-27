package agents.agentokolia;

import OSPABA.*;
import simulation.*;

//meta! id="2"
public class ManagerOkolia extends OSPABA.Manager
{
	public ManagerOkolia(int id, Simulation mySim, Agent myAgent)
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

	//meta! sender="AgentModelu", id="26", type="Notice"
	public void processPacientOdisiel(MessageForm message)
	{
	}

	//meta! sender="PlanovacPrichodovSamostatne", id="10", type="Notice"
	public void processNoticePlanovacPrichodovSamostatne(MessageForm message)
	{
	}

	//meta! sender="PlanovacPrichodovSanitkou", id="12", type="Notice"
	public void processNoticePlanovacPrichodovSanitkou(MessageForm message)
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
		case Mc.notice:
			switch (message.sender().id())
			{
			case Id.planovacPrichodovSamostatne:
				processNoticePlanovacPrichodovSamostatne(message);
			break;

			case Id.planovacPrichodovSanitkou:
				processNoticePlanovacPrichodovSanitkou(message);
			break;
			}
		break;

		case Mc.pacientOdisiel:
			processPacientOdisiel(message);
		break;

		default:
			processDefault(message);
		break;
		}
	}
	//meta! tag="end"

	@Override
	public AgentOkolia myAgent()
	{
		return (AgentOkolia)super.myAgent();
	}

}
