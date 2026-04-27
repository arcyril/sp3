package agents.agentvstupvysetrenia;

import OSPABA.*;
import agents.agentvstupvysetrenia.continualassistants.*;
import simulation.*;

//meta! id="4"
public class AgentVstupVysetrenia extends OSPABA.Agent
{
	public AgentVstupVysetrenia(int id, Simulation mySim, Agent parent)
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
		new ManagerVstupVysetrenia(Id.managerVstupVysetrenia, mySim(), this);
		new ProcesVstupVysetrenia(Id.procesVstupVysetrenia, mySim(), this);
		addOwnMessage(Mc.vykonatVstupOsetrenie);
	}
	//meta! tag="end"
}
