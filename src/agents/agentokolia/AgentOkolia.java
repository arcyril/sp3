package agents.agentokolia;

import OSPABA.*;
import simulation.*;
import agents.agentokolia.continualassistants.*;

//meta! id="1"
public class AgentOkolia extends OSPABA.Agent
{
	public AgentOkolia(int id, Simulation mySim, Agent parent)
	{
		super(id, mySim, parent);
		init();
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication
	}

	//meta! userInfo="Generated code: do not modify", tag="begin"
	private void init()
	{
		new ManagerOkolia(Id.managerOkolia, mySim(), this);
		new PlanovacPrichodovSanitkou(Id.planovacPrichodovSanitkou, mySim(), this);
		new PlanovacPrichodovSamostatne(Id.planovacPrichodovSamostatne, mySim(), this);
		addOwnMessage(Mc.pacientOdisiel);
		addOwnMessage(Mc.prisielSamostatne);
		addOwnMessage(Mc.prisielSanitkou);
	}
	//meta! tag="end"
}
