package agents.agentokolia;

import OSPABA.*;
// import generators.TrojuholnikovyGenerator;
import generators.UniformGenerator;
import simulation.*;

//meta! id="1"
public class ManagerOkolia extends OSPABA.Manager
{
private UniformGenerator genCasOdchodu;
	
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

		// System.out.println("manager okolia test, time " + mySim().currentTime());
		// System.out.println("Mc.start " + Mc.start);
		
		genCasOdchodu = new UniformGenerator(150.0, 240.0, ((MySimulation)mySim()).masterRandom.nextInt());
		//?? maybe if statement
		MyMessage startSamostatne = new MyMessage(mySim());
		startSamostatne.setAddressee(Id.planovacPrichodovSamostatne);
		//?? apparently mc.start is automatic anyway
		startSamostatne.setCode(Mc.start);
		startContinualAssistant(startSamostatne);

		MyMessage startSanitkou = new MyMessage(mySim());
		startSanitkou.setAddressee(Id.planovacPrichodovSanitkou);
		startSanitkou.setCode(Mc.start);
		startContinualAssistant(startSanitkou);

		try {
			// System.out.println("startContinualAssistant");
		} catch (Exception e) {
			System.out.println("ERROR" + e.getMessage());
			e.printStackTrace();
		}
	}

	//meta! sender="AgentModelu", id="15", type="Notice"
	public void processPacientOdisiel(MessageForm message)
	{
		MyMessage pacient = (MyMessage) message;
		MySimulation sim = (MySimulation) mySim();
		
		double casOdchodu = genCasOdchodu.sample();
		double casVSysteme = mySim().currentTime() + casOdchodu - pacient.casPrichodu;

		sim.zahrievanieSkonciloCheck(sim.currentTime());
        if (sim.currentTime() >= sim.trvanieZahrievania) {
            sim.statCasVSysteme.addValue(casVSysteme);
			sim.statVybaveniPacienti.addValue(1.0);
        }

		((MySimulation)mySim()).aktualniPacienti.remove(pacient.idPacienta);

		((MySimulation)mySim()).logWarmupData(mySim().currentTime());
	}

	//meta! sender="PlanovacPrichodovSamostatne", id="26", type="Notice"
	public void processPrisielSamostatne(MessageForm message)
	{
		// System.out.println("1 processPrisielSamostatne");
		message.setAddressee(Id.agentModelu); // ((MySimulation)mySim()).agentModelu()
		message.setCode(Mc.pacientPrisiel); 
		notice(message);

		((MySimulation)mySim()).logWarmupData(mySim().currentTime());
	}

	//meta! sender="PlanovacPrichodovSanitkou", id="27", type="Notice"
	public void processPrisielSanitkou(MessageForm message)
	{
		message.setAddressee(Id.agentModelu);
		message.setCode(Mc.pacientPrisiel);
		notice(message);

		((MySimulation)mySim()).logWarmupData(mySim().currentTime());
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
		case Mc.pacientOdisiel:
			processPacientOdisiel(message);
		break;

		case Mc.prisielSanitkou:
			processPrisielSanitkou(message);
		break;

		case Mc.prisielSamostatne:
			processPrisielSamostatne(message);
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
