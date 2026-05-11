package agents.agenturgentprijmu.continualassistants;

import OSPABA.*;
import simulation.*;
import agents.agenturgentprijmu.*;
import OSPABA.Process;
import generators.TrojuholnikovyGenerator;
import generators.UniformGenerator;
import agents.agentvstupvysetrenia.ManagerVstupVysetrenia;

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
		genCasPresunuSamostatne = new TrojuholnikovyGenerator(120.0, 150.0, 300.0, ((MySimulation)mySim()).masterRandom.nextInt());
        genCasPresunuSanitkou = new UniformGenerator(90.0, 200.0, ((MySimulation)mySim()).masterRandom.nextInt());
	}

	//meta! sender="AgentUrgentPrijmu", id="30", type="Start"
	public void processStart(MessageForm message)
	{
		System.out.println( "4 processStart ProcesPresunu. Time: " + mySim().currentTime());

		MyMessage pacient = (MyMessage) message;
        double casPresunu = 0.0;

		if (pacient.typPacienta.equals(simulation.Constants.PACIENT_SAMOSTATNE)) {
            casPresunu = genCasPresunuSamostatne.sample();
        } else {
            casPresunu = genCasPresunuSanitkou.sample();
        }

		//!! ANIMACIA
		simulation.AnimationHelper.animatePresun((MySimulation)mySim(), pacient, casPresunu);

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
