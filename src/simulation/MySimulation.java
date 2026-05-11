package simulation;

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

	public int configZvolenyRezim = 1;
    public boolean configTurboRezim = false;
	public boolean configRezervovatSestruAmbulanciuB = false;

	public double trvanieZahrievania = 0.0;
    private boolean zahriate = false;

	//# CSV warmup
	public boolean configSledovatZahrievanie = false;
    private int poslednyZaznamenanyInterval = -1;


	public Random masterRandom;
	
    public Statistic statCasVSysteme;
    public GlobalStatistic globalCasVSysteme;

	public Statistic statCasCakaniaVstup;
    public GlobalStatistic globalCasCakaniaVstupVysVsetci;

    public StatisticWeighted wstatradVstupVysetrenieSanitkou;
    public GlobalStatistic globalRadVstupVysetrenieSanitkou;

	public StatisticWeighted wstatRadVstupVysSamostatne;
    public GlobalStatistic globalRadVstupVysSamostatne;

	public Statistic statCakanieSanitka;
    public GlobalStatistic globalCasCakaniaOsetreniaSanitkou;
    public Statistic statCakanieSamostatne;
    public GlobalStatistic globalCasCakaniaOsetreniaSamostatne;

    public StatisticWeighted wstatVyuzitieLekar;
    public GlobalStatistic globalVyuzitieLekar;

    public StatisticWeighted wstatVyuzitieSestra;
    public GlobalStatistic globalVyuzitieSestra;

	public Statistic statVybaveniPacienti;
    public GlobalStatistic globalVybaveniPacienti;

	//** LLM usage, to specify
	public ConcurrentHashMap<Integer, String[]> aktualniPacienti = new ConcurrentHashMap<>();

	//!! ANIMACIA
	public OSPAnimator.AnimQueue[] animRadyOsetrenie = new OSPAnimator.AnimQueue[5];
	public java.awt.geom.Point2D.Double bodVchodSamostatne = new java.awt.geom.Point2D.Double(0, 500);
    public java.awt.geom.Point2D.Double bodVchodSanitka = new java.awt.geom.Point2D.Double(0, 200);
    public java.awt.geom.Point2D.Double bodTriageSamostatne = new java.awt.geom.Point2D.Double(400, 300);
    public java.awt.geom.Point2D.Double bodTriageSanitka = new java.awt.geom.Point2D.Double(400, 450);
	public OSPAnimator.AnimQueue animRadSanitka;
	public OSPAnimator.AnimQueue animRadSamostatne;

	public MySimulation()
	{
		OSPAnimator.Flags.IgnoreQueueExceptions = true;
		
		masterRandom = new Random(12345); //#

        statCasVSysteme = new Statistic();
        globalCasVSysteme = new GlobalStatistic();
		statCasCakaniaVstup = new Statistic();
        globalCasCakaniaVstupVysVsetci = new GlobalStatistic();
        wstatradVstupVysetrenieSanitkou = new StatisticWeighted();
        globalRadVstupVysetrenieSanitkou = new GlobalStatistic();
		wstatRadVstupVysSamostatne = new StatisticWeighted();
        globalRadVstupVysSamostatne = new GlobalStatistic();
		statCakanieSanitka = new Statistic();
        globalCasCakaniaOsetreniaSanitkou = new GlobalStatistic();
        statCakanieSamostatne = new Statistic();
        globalCasCakaniaOsetreniaSamostatne = new GlobalStatistic();
		wstatVyuzitieLekar = new StatisticWeighted();
        globalVyuzitieLekar = new GlobalStatistic();
		wstatVyuzitieSestra = new StatisticWeighted();
        globalVyuzitieSestra = new GlobalStatistic();
        statVybaveniPacienti = new Statistic();
        globalVybaveniPacienti = new GlobalStatistic();
		
		init();

		createAnimator();
	}

	@Override
	public void prepareSimulation()
	{
		super.prepareSimulation();
		// Create global statistcis
        globalCasVSysteme.clear();
		globalCasCakaniaVstupVysVsetci.clear();
        globalRadVstupVysetrenieSanitkou.clear();
		globalRadVstupVysSamostatne.clear();
		globalCasCakaniaOsetreniaSanitkou.clear();
        globalCasCakaniaOsetreniaSamostatne.clear();
		globalVyuzitieLekar.clear();
        globalVyuzitieSestra.clear();
        globalVybaveniPacienti.clear();
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Reset entities, queues, local statistics, etc...
        statCasVSysteme.clear();
		statCasCakaniaVstup.clear();
        wstatradVstupVysetrenieSanitkou.clear();
		wstatRadVstupVysSamostatne.clear();
		statCakanieSanitka.clear();
        statCakanieSamostatne.clear();
        wstatVyuzitieLekar.clear();
        wstatVyuzitieSestra.clear();
        statVybaveniPacienti.clear();

		zahriate = false;

		poslednyZaznamenanyInterval = -1;

		if (animatorExists()) {
			initStaticAnimation();
		}
	}

	@Override
	public void replicationFinished()
	{
		// Collect local statistics into global, update UI, etc...
		super.replicationFinished();

		globalCasCakaniaOsetreniaSanitkou.addReplicationData(statCakanieSanitka.getAverage(), statCakanieSanitka.getMax());
        globalCasCakaniaOsetreniaSamostatne.addReplicationData(statCakanieSamostatne.getAverage(), statCakanieSamostatne.getMax());
		
        globalCasVSysteme.addReplicationData(statCasVSysteme.getAverage(), statCasVSysteme.getMax());
		
		globalCasCakaniaVstupVysVsetci.addReplicationData(statCasCakaniaVstup.getAverage(), statCasCakaniaVstup.getMax());
        
        globalRadVstupVysetrenieSanitkou.addReplicationData(wstatradVstupVysetrenieSanitkou.getAverage(currentTime()), 0);

		globalRadVstupVysSamostatne.addReplicationData(wstatRadVstupVysSamostatne.getAverage(currentTime()), 0);
        //#
        double vyuzitieLekarPercento = (wstatVyuzitieLekar.getAverage(currentTime()) / configPocetLekarov) * 100.0;
        globalVyuzitieLekar.addReplicationData(vyuzitieLekarPercento, 0);
		
		double vyuzitieSestraPercento = (wstatVyuzitieSestra.getAverage(currentTime()) / configPocetSestier) * 100.0;
        globalVyuzitieSestra.addReplicationData(vyuzitieSestraPercento, 0);

        globalVybaveniPacienti.addReplicationData(statVybaveniPacienti.getAverage() * statVybaveniPacienti.getCount(), 0);

		//??
		System.out.println("End of Replication: " + currentReplication());
        System.out.println("Avg Time in System: " + statCasVSysteme.getAverage() + " seconds");
	}

	@Override
	public void simulationFinished()
	{
		// Display simulation results
		super.simulationFinished();

		//??
		System.out.println("\n====== SIMULATION FINISHED ======");
        System.out.println("GLOBAL Avg Wait for Treatment Samostatne: " + globalCasCakaniaOsetreniaSamostatne.getGlobalAverage());
        System.out.println("95% CI Wait for Treatment: +/- " + globalCasCakaniaOsetreniaSamostatne.getConfidenceIntervalHalfWidth());

		System.out.println("GLOBAL Avg Wait for Treatment Sanitka: " + globalCasCakaniaOsetreniaSanitkou.getGlobalAverage());
        System.out.println("95% CI Wait for Treatment: +/- " + globalCasCakaniaOsetreniaSanitkou.getConfidenceIntervalHalfWidth());

        
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
	public void zahrievanieSkonciloCheck(double currentTime) {
        if (trvanieZahrievania > 0 && !zahriate && currentTime >= trvanieZahrievania) {
			statCasVSysteme.clear();
			statCasCakaniaVstup.clear();
			statCakanieSanitka.clear();
			statCakanieSamostatne.clear();
			statVybaveniPacienti.clear();

			ManagerVstupVysetrenia manVstup = (ManagerVstupVysetrenia) agentVstupVysetrenia().myManager();
            ManagerUrgentPrijmu manUrgent = (ManagerUrgentPrijmu) agentUrgentPrijmu().myManager();
            
            wstatradVstupVysetrenieSanitkou.warmUp(currentTime, manVstup.radSantikouVstupVysetrenie.size());
			wstatRadVstupVysSamostatne.warmUp(currentTime, manVstup.radVstupVysSamostatne.size());
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
                if (manVstup.radSantikouVstupVysetrenie != null) qSanitka = manVstup.radSantikouVstupVysetrenie.size();
                if (manVstup.radVstupVysSamostatne != null) qSamostatne = manVstup.radVstupVysSamostatne.size();
            }

            try (java.io.FileWriter writer = new java.io.FileWriter("warmup_data.csv", true)) {
                writer.write(currentTime + "," 
                     + (currentTime / 3600.0) + "," 
                     + statCasVSysteme.getAverage() + "," 
                    //  + statCasCakaniaOsetrenie.getAverage() + "," 
                     + qSanitka + "," 
                     + qSamostatne + "\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

	public void initStaticAnimation() {
		try {
			java.awt.image.BufferedImage pozadie = javax.imageio.ImageIO.read(new java.io.File("hospital.png"));
			if (pozadie != null) {
				animator().setBackgroundImage(pozadie);
			}
		} catch (Exception e) {
			System.err.println("Failed" + e.getMessage());
		}

		for (int i = 0; i < 5; i++) {
            double startY = 580.0 + (i * 30.0); 
            animRadyOsetrenie[i] = new OSPAnimator.AnimQueue(
                animator(),
                new java.awt.geom.Point2D.Double(500.0, startY),
                new java.awt.geom.Point2D.Double(250.0, startY),
                1.0
            );
            animator().register(new AnimQueueItem(animRadyOsetrenie[i]));
        }

		animRadSamostatne = new OSPAnimator.AnimQueue(
            animator(),
            new java.awt.geom.Point2D.Double(500.0, 270.0),
            new java.awt.geom.Point2D.Double(200.0, 270.0),
            1.0
        );
        animator().register(new AnimQueueItem(animRadSamostatne));

        animRadSanitka = new OSPAnimator.AnimQueue(
            animator(),
            new java.awt.geom.Point2D.Double(500.0, bodVchodSanitka.y),
            new java.awt.geom.Point2D.Double(200.0, bodVchodSanitka.y),
            1.0
        );
        animator().register(new AnimQueueItem(animRadSanitka));
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
