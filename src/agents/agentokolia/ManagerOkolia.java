package agents.agentokolia;

import OSPABA.*;
import simulation.*;
import agents.agentokolia.continualassistants.*;

//meta! id="1"
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

		System.out.println("manager okolia test, time " + mySim().currentTime());
		System.out.println("Mc.start " + Mc.start);
		MyMessage startSamostatne = new MyMessage(mySim());
		startSamostatne.setAddressee(Id.planovacPrichodovSamostatne);
		startSamostatne.setCode(Mc.start);
		try {
			startContinualAssistant(startSamostatne);
			System.out.println("startContinualAssistant SAMOSTATNE");
		} catch (Exception e) {
			System.out.println("ERROR" + e.getMessage());
			e.printStackTrace();
		}

		MyMessage startSanitkou = new MyMessage(mySim());
		startSanitkou.setAddressee(Id.planovacPrichodovSanitkou);
		startSanitkou.setCode(Mc.start);
		startContinualAssistant(startSanitkou);
	}

	//meta! sender="AgentModelu", id="15", type="Notice"
	public void processPacientPrisiel(MessageForm message)
	{
	}

	//meta! sender="PlanovacPrichodovSamostatne", id="26", type="Notice"
	public void processPrisielSamostatne(MessageForm message)
	{
		//**LLM FOR DEBUGGING
		message.setAddressee(((MySimulation)mySim()).agentModelu());
		// Assuming you have an arrow to AgentModelu named "novyPacient"
		message.setCode(Mc.pacientPrisiel); 
		notice(message);
	}

	//meta! sender="PlanovacPrichodovSanitkou", id="27", type="Notice"
	public void processPrisielSanitkou(MessageForm message)
	{
		message.setAddressee(((MySimulation)mySim()).agentModelu());
		message.setCode(Mc.pacientPrisiel);
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
		case Mc.prisielSamostatne:
			processPrisielSamostatne(message);
		break;

		case Mc.prisielSanitkou:
			processPrisielSanitkou(message);
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
	public AgentOkolia myAgent()
	{
		return (AgentOkolia)super.myAgent();
	}

}
