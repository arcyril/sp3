package agents.agenturgentprijmu.continualassistants;

import OSPABA.*;
import simulation.*;
import agents.agenturgentprijmu.*;
// import OSPABA.Process;

// import java.awt.Color;
// import java.awt.Graphics;
// import java.awt.image.BufferedImage;
import generators.UniformGenerator;

//meta! id="29"
public class ProcesPresunu extends OSPABA.Process
{
	private UniformGenerator genCasPresunu = new UniformGenerator(10.0, 30.0, 4);

	public ProcesPresunu(int id, Simulation mySim, CommonAgent myAgent)
	{
		super(id, mySim, myAgent);
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication
	}

	//meta! sender="AgentUrgentPrijmu", id="30", type="Start"
	public void processStart(MessageForm message)
	{
		double casPresunu = genCasPresunu.sample();
        hold(casPresunu, message);
	}

	//meta! userInfo="Process messages defined in code", id="0"
	public void processDefault(MessageForm message)
	{
		MyMessage pacient = (MyMessage) message;

		if (((MySimulation)mySim()).aktualniPacienti.containsKey(pacient.idPacienta)) {
			((MySimulation)mySim()).aktualniPacienti.get(pacient.idPacienta)[2] = "čaka v rade na vyšetrenie";
		}
		((MySimulation)mySim()).refreshUI();
		assistantFinished(message);
		// switch (message.code())
		// {
		// }
	}

	//meta! userInfo="Generated code: do not modify", tag="begin"
	@Override
	public void processMessage(MessageForm message)
	{
		switch (message.code())
		{
		case Mc.start:
			processStart(message);
		break;

		default:
			processDefault(message);
		break;
		}
	}
	//meta! tag="end"

	@Override
	public AgentUrgentPrijmu myAgent()
	{
		return (AgentUrgentPrijmu)super.myAgent();
	}

}
