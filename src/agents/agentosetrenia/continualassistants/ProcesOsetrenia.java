package agents.agentosetrenia.continualassistants;

import OSPABA.*;
import agents.agentosetrenia.*;
import generators.SpojityEmpirickyGenerator;
import generators.TrojuholnikovyGenerator;
import generators.UniformGenerator;
import simulation.*;
import OSPABA.Process;

//meta! id="36"
public class ProcesOsetrenia extends OSPABA.Process
{
	private TrojuholnikovyGenerator genCasPresunuPersonalu;
	private SpojityEmpirickyGenerator casOsetreniaSamostatneGen;
    private UniformGenerator casOsetreniaSanitkouGen;

	public ProcesOsetrenia(int id, Simulation mySim, CommonAgent myAgent)
	{
		super(id, mySim, myAgent);

		genCasPresunuPersonalu = new TrojuholnikovyGenerator(15.0, 20.0, 45.0, ((MySimulation)mySim()).masterRandom.nextInt());
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication

		double[] pravdepodobnosti = {0.1, 0.6, 0.3};
		double[] minCas = {10.0, 12.0, 14.0};
		double[] maxCas = {12.0, 14.0, 18.0};

		casOsetreniaSamostatneGen = new SpojityEmpirickyGenerator(pravdepodobnosti, minCas, maxCas, ((MySimulation)mySim()).masterRandom.nextInt());
		casOsetreniaSanitkouGen = new UniformGenerator(15.0, 30.0, ((MySimulation)mySim()).masterRandom.nextInt());
	}

	//meta! sender="AgentOsetrenia", id="37", type="Start"
	public void processStart(MessageForm message)
	{
		MyMessage pacient = (MyMessage) message;
        double casOsetreniaSekundy;
		
		if (pacient.typPacienta.equals("SAMOSTATNE")) {
            casOsetreniaSekundy = casOsetreniaSamostatneGen.sample() * 60.0; 
			System.out.println("11 ProcesOsetrenia SAM");
		} else {
			casOsetreniaSekundy = casOsetreniaSanitkouGen.sample() * 60.0;
			System.out.println("11 ProcesOsetrenia SANITKOU");
		}

		double casPresunuPersonalu = genCasPresunuPersonalu.sample();
		double celkovyCas = casOsetreniaSekundy + casPresunuPersonalu;

		message.setCode(Mc.finish);
        hold(celkovyCas, message);
	}

	//meta! userInfo="Process messages defined in code", id="0"
	public void processDefault(MessageForm message)
	{
		// switch (message.code())
		// {
		// }
		System.out.println("11.5 ProcesOsetrenia IS FINISHING");
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
	public AgentOsetrenia myAgent()
	{
		return (AgentOsetrenia)super.myAgent();
	}

}
