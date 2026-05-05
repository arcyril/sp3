package simulation;

import OSPABA.*;
import OSPStat.Stat;
import agents.agentosetrenia.*;
import agents.agentokolia.*;
import agents.agentvstupvysetrenia.*;
import statistics.GlobalStatistic;
import statistics.Statistic;
import agents.agenturgentprijmu.*;
import agents.agentmodelu.*;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class MySimulation extends OSPABA.Simulation
{
	public Random masterRandom;
	
	public Statistic statCasCakaniaOsetrenie;
    public GlobalStatistic globalCasCakaniaOsetrenie;
    public Statistic statCasVSysteme;
    public GlobalStatistic globalCasVSysteme;

	//** LLM usage, to specify
	public ConcurrentHashMap<Integer, String[]> aktualniPacienti = new ConcurrentHashMap<>();

	public MySimulation()
	{

		masterRandom = new Random(12345);
		statCasCakaniaOsetrenie = new Statistic();
        globalCasCakaniaOsetrenie = new GlobalStatistic();
        statCasVSysteme = new Statistic();
        globalCasVSysteme = new GlobalStatistic();
		
		init();
	}

	@Override
	public void prepareSimulation()
	{
		super.prepareSimulation();
		// Create global statistcis
		globalCasCakaniaOsetrenie.clear();
        globalCasVSysteme.clear();
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Reset entities, queues, local statistics, etc...
		statCasCakaniaOsetrenie.clear();
        statCasVSysteme.clear();
	}

	@Override
	public void replicationFinished()
	{
		// Collect local statistics into global, update UI, etc...
		super.replicationFinished();

		globalCasCakaniaOsetrenie.addReplicationData(
            statCasCakaniaOsetrenie.getAverage(), 
            statCasCakaniaOsetrenie.getMax()
        );
        
        globalCasVSysteme.addReplicationData(
            statCasVSysteme.getAverage(), 
            statCasVSysteme.getMax()
        );

		//??
		System.out.println("End of Replication: " + currentReplication());
        System.out.println("Avg Wait for Osetrenie: " + statCasCakaniaOsetrenie.getAverage() + " seconds");
        System.out.println("Avg Time in System: " + statCasVSysteme.getAverage() + " seconds");
	}

	@Override
	public void simulationFinished()
	{
		// Display simulation results
		super.simulationFinished();

		//??
		System.out.println("\n====== SIMULATION FINISHED ======");
        System.out.println("GLOBAL Avg Wait for Treatment: " + globalCasCakaniaOsetrenie.getGlobalAverage());
        System.out.println("95% CI Wait for Treatment: +/- " + globalCasCakaniaOsetrenie.getConfidenceIntervalHalfWidth());
        
        System.out.println("GLOBAL Avg Time in System: " + globalCasVSysteme.getGlobalAverage());
        System.out.println("95% CI Time in System: +/- " + globalCasVSysteme.getConfidenceIntervalHalfWidth());
        System.out.println("=================================");
	}

	public void refreshUI()
	{
		if (_onRefreshUI != null)
		{
			_onRefreshUI.accept(this);
		}
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
