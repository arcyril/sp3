package simulation;

import OSPABA.*;
import agents.agentosetrenia.*;
import agents.agentokolia.*;
import agents.agentvstupvysetrenia.*;
import agents.agenturgentprijmu.*;
import agents.agentmodelu.*;

public class MySimulation extends OSPABA.Simulation
{
	public MySimulation()
	{
		init();
	}

	@Override
	public void prepareSimulation()
	{
		super.prepareSimulation();
		// Create global statistcis
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Reset entities, queues, local statistics, etc...
	}

	@Override
	public void replicationFinished()
	{
		// Collect local statistics into global, update UI, etc...
		super.replicationFinished();
	}

	@Override
	public void simulationFinished()
	{
		// Display simulation results
		super.simulationFinished();
	}

	//meta! userInfo="Generated code: do not modify", tag="begin"
	private void init()
	{
		setAgentModelu(new AgentModelu(Id.agentModelu, this, null));
		setAgentUrgentPrijmu(new AgentUrgentPrijmu(Id.agentUrgentPrijmu, this, agentModelu()));
		setAgentVstupVysetrenia(new AgentVstupVysetrenia(Id.agentVstupVysetrenia, this, agentUrgentPrijmu()));
		setAgentOsetrenia(new AgentOsetrenia(Id.agentOsetrenia, this, agentUrgentPrijmu()));
		setAgentOkolia(new AgentOkolia(Id.agentOkolia, this, agentModelu()));
	}

	private AgentModelu _agentModelu;

public AgentModelu agentModelu()
	{ return _agentModelu; }

	public void setAgentModelu(AgentModelu agentModelu)
	{_agentModelu = agentModelu; }

	private AgentUrgentPrijmu _agentUrgentPrijmu;

public AgentUrgentPrijmu agentUrgentPrijmu()
	{ return _agentUrgentPrijmu; }

	public void setAgentUrgentPrijmu(AgentUrgentPrijmu agentUrgentPrijmu)
	{_agentUrgentPrijmu = agentUrgentPrijmu; }

	private AgentVstupVysetrenia _agentVstupVysetrenia;

public AgentVstupVysetrenia agentVstupVysetrenia()
	{ return _agentVstupVysetrenia; }

	public void setAgentVstupVysetrenia(AgentVstupVysetrenia agentVstupVysetrenia)
	{_agentVstupVysetrenia = agentVstupVysetrenia; }

	private AgentOsetrenia _agentOsetrenia;

public AgentOsetrenia agentOsetrenia()
	{ return _agentOsetrenia; }

	public void setAgentOsetrenia(AgentOsetrenia agentOsetrenia)
	{_agentOsetrenia = agentOsetrenia; }

	private AgentOkolia _agentOkolia;

public AgentOkolia agentOkolia()
	{ return _agentOkolia; }

	public void setAgentOkolia(AgentOkolia agentOkolia)
	{_agentOkolia = agentOkolia; }
	//meta! tag="end"
}
