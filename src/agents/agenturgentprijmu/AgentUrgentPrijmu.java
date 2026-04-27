package agents.agenturgentprijmu;

import OSPABA.*;
import simulation.*;
import agents.agenturgentprijmu.continualassistants.*;

//meta! id="3"
public class AgentUrgentPrijmu extends OSPABA.Agent
{
	public AgentUrgentPrijmu(int id, Simulation mySim, Agent parent)
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
		new ManagerUrgentPrijmu(Id.managerUrgentPrijmu, mySim(), this);
		new ProcesPresunu(Id.procesPresunu, mySim(), this);
		addOwnMessage(Mc.vykonatVstupOsetrenie);
		addOwnMessage(Mc.vykonatOsetrenie);
		addOwnMessage(Mc.spracovaniePacienta);
	}
	//meta! tag="end"
}
