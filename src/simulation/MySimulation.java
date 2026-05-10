package simulation;

import OSPABA.*;
import OSPStat.Stat;
import agents.agentosetrenia.*;
import agents.agentokolia.*;
import agents.agentvstupvysetrenia.*;
import statistics.GlobalStatistic;
import statistics.Statistic;
import statistics.StatisticWeighted;
import agents.agenturgentprijmu.*;
import agents.agentmodelu.*;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class MySimulation extends OSPABA.Simulation
{
	//#
	public int configPocetLekarov = 5;
	public int configPocetSestier = 10;
	public boolean configRezim1Aktivny = true;

	public double configZahrievanie = 0.0;
    public boolean configTurboRezim = false;
    private boolean zahriate = false;

	//# CSV warmup
	public boolean configSledovatZahrievanie = false;
    private int poslednyZaznamenanyInterval = -1;

	public Random masterRandom;
	
    public Statistic statCasVSysteme;
    public GlobalStatistic globalCasVSysteme;

	public Statistic statCasCakaniaVstup;
    public GlobalStatistic globalCasCakaniaVstup;

    public StatisticWeighted wstatRadSanitka;
    public GlobalStatistic globalRadSanitka;

	public StatisticWeighted wstatRadSamostatne;
    public GlobalStatistic globalRadSamostatne;

	public Statistic statCasCakaniaOsetrenie;
    public GlobalStatistic globalCasCakaniaOsetrenie;

    public StatisticWeighted wstatVyuzitieLekar;
    public GlobalStatistic globalVyuzitieLekar;

    public StatisticWeighted wstatVyuzitieSestra;
    public GlobalStatistic globalVyuzitieSestra;

	public Statistic statVybaveniPacienti;
    public GlobalStatistic globalVybaveniPacienti;

	//** LLM usage, to specify
	public ConcurrentHashMap<Integer, String[]> aktualniPacienti = new ConcurrentHashMap<>();

	public MySimulation()
	{
		masterRandom = new Random(12345); //#

        statCasVSysteme = new Statistic();
        globalCasVSysteme = new GlobalStatistic();
		statCasCakaniaVstup = new Statistic();
        globalCasCakaniaVstup = new GlobalStatistic();
        wstatRadSanitka = new StatisticWeighted();
        globalRadSanitka = new GlobalStatistic();
		wstatRadSamostatne = new StatisticWeighted();
        globalRadSamostatne = new GlobalStatistic();
		statCasCakaniaOsetrenie = new Statistic();
        globalCasCakaniaOsetrenie = new GlobalStatistic();
        wstatVyuzitieLekar = new StatisticWeighted();
        globalVyuzitieLekar = new GlobalStatistic();
		wstatVyuzitieSestra = new StatisticWeighted();
        globalVyuzitieSestra = new GlobalStatistic();
        statVybaveniPacienti = new Statistic();
        globalVybaveniPacienti = new GlobalStatistic();
		
		init();
	}

	@Override
	public void prepareSimulation()
	{
		super.prepareSimulation();
		// Create global statistcis
        globalCasVSysteme.clear();
		globalCasCakaniaVstup.clear();
        globalRadSanitka.clear();
		globalRadSamostatne.clear();
		globalCasCakaniaOsetrenie.clear();
        globalVyuzitieLekar.clear();
        globalVyuzitieSestra.clear();
        globalVybaveniPacienti.clear();

		// if (configSledovatZahrievanie) {
        //     try (java.io.FileWriter writer = new java.io.FileWriter("warmup_data.csv", false)) {
        //         writer.write("Cas_Sekundy,Cas_Hodiny,PriemernyCasVSysteme,PriemerneCakanieOsetrenie,RadSanitka,RadSamostatne\n");
        //     } catch (Exception e) {
        //         e.printStackTrace();
        //     }
        // }
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Reset entities, queues, local statistics, etc...
        statCasVSysteme.clear();
		statCasCakaniaVstup.clear();
        wstatRadSanitka.clear();
		wstatRadSamostatne.clear();
		statCasCakaniaOsetrenie.clear();
        wstatVyuzitieLekar.clear();
        wstatVyuzitieSestra.clear();
        statVybaveniPacienti.clear();

		zahriate = false;

		poslednyZaznamenanyInterval = -1;
	}

	@Override
	public void replicationFinished()
	{
		// Collect local statistics into global, update UI, etc...
		super.replicationFinished();

		globalCasCakaniaOsetrenie.addReplicationData(statCasCakaniaOsetrenie.getAverage(), statCasCakaniaOsetrenie.getMax());
        
        globalCasVSysteme.addReplicationData(statCasVSysteme.getAverage(), statCasVSysteme.getMax());
		
		globalCasCakaniaVstup.addReplicationData(statCasCakaniaVstup.getAverage(), statCasCakaniaVstup.getMax());
        
        globalRadSanitka.addReplicationData(wstatRadSanitka.getAverage(currentTime()), 0);

		globalRadSamostatne.addReplicationData(wstatRadSamostatne.getAverage(currentTime()), 0);
        //#
        double vyuzitieLekarPercento = (wstatVyuzitieLekar.getAverage(currentTime()) / configPocetLekarov) * 100.0;
        globalVyuzitieLekar.addReplicationData(vyuzitieLekarPercento, 0);
		
		double vyuzitieSestraPercento = (wstatVyuzitieSestra.getAverage(currentTime()) / configPocetSestier) * 100.0;
        globalVyuzitieSestra.addReplicationData(vyuzitieSestraPercento, 0);

        globalVybaveniPacienti.addReplicationData(statVybaveniPacienti.getAverage() * statVybaveniPacienti.getCount(), 0);

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

	//* LLM */
	public void skontrolujZahrievanie(double currentTime) {
        if (configZahrievanie > 0 && !zahriate && currentTime >= configZahrievanie) {
			statCasVSysteme.clear();
			statCasCakaniaVstup.clear();
            statCasCakaniaOsetrenie.clear();
            statVybaveniPacienti.clear();

			ManagerVstupVysetrenia manVstup = (ManagerVstupVysetrenia) agentVstupVysetrenia().myManager();
            ManagerUrgentPrijmu manUrgent = (ManagerUrgentPrijmu) agentUrgentPrijmu().myManager();
            
            wstatRadSanitka.warmUp(currentTime, manVstup.radSanitkou.size());
			wstatRadSamostatne.warmUp(currentTime, manVstup.radSamostatne.size());
            wstatVyuzitieLekar.warmUp(currentTime, configPocetLekarov - manUrgent.volniLekari.size());
			wstatVyuzitieSestra.warmUp(currentTime, configPocetSestier - manUrgent.volneSestry.size());

            zahriate = true;
        }
    }

    public void logWarmupData(double currentTime) {
        if (!configSledovatZahrievanie) return;

        int aktualnyInterval = (int)(currentTime / 1800); 

        if (aktualnyInterval > poslednyZaznamenanyInterval) {
            poslednyZaznamenanyInterval = aktualnyInterval;
            
            int qSanitka = 0;
            int qSamostatne = 0;
            if (agentVstupVysetrenia() != null && agentVstupVysetrenia().myManager() != null) {
                ManagerVstupVysetrenia manVstup = (ManagerVstupVysetrenia) agentVstupVysetrenia().myManager();
                if (manVstup.radSanitkou != null) qSanitka = manVstup.radSanitkou.size();
                if (manVstup.radSamostatne != null) qSamostatne = manVstup.radSamostatne.size();
            }

            try (java.io.FileWriter writer = new java.io.FileWriter("warmup_data.csv", true)) {
                writer.write(currentTime + "," 
                     + (currentTime / 3600.0) + "," 
                     + statCasVSysteme.getAverage() + "," 
                     + statCasCakaniaOsetrenie.getAverage() + "," 
                     + qSanitka + "," 
                     + qSamostatne + "\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
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
