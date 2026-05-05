package agents.agenturgentprijmu.continualassistants;

import OSPABA.*;
import simulation.*;
import agents.agenturgentprijmu.*;
// import OSPABA.Process;
import generators.TrojuholnikovyGenerator;
// import java.awt.Color;
// import java.awt.Graphics;
// import java.awt.image.BufferedImage;
import generators.UniformGenerator;

//meta! id="29"
public class ProcesPresunu extends OSPABA.Process
{
	private TrojuholnikovyGenerator genCasPresunuSamostatne;
    private UniformGenerator genCasPresunuSanitkou;
	
	public ProcesPresunu(int id, Simulation mySim, CommonAgent myAgent)
	{
		super(id, mySim, myAgent);
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication
		//# what of seed
		genCasPresunuSamostatne = new TrojuholnikovyGenerator(120.0, 150.0, 300.0, 1);
        genCasPresunuSanitkou = new UniformGenerator(90.0, 200.0, 1);
	}

	//meta! sender="AgentUrgentPrijmu", id="30", type="Start"
	public void processStart(MessageForm message)
	{
		// System.out.println("4 processStart ProcesPresunu, defining time travel");
		System.out.println("Time: " + mySim().currentTime() + " | 4 processStart ProcesPresunu");

		MyMessage pacient = (MyMessage) message;
        double casPresunu = 0.0;

		if (pacient.typPacienta.equals("SAMOSTATNE")) {
            casPresunu = genCasPresunuSamostatne.sample();
        } else {
            casPresunu = genCasPresunuSanitkou.sample();
        }
		
		message.setCode(Mc.finish);
		hold(casPresunu, message);
	}

	//meta! userInfo="Process messages defined in code", id="0"
	public void processDefault(MessageForm message)
	{
				System.out.println("4.5");

		assistantFinished(message);
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
