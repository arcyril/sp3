package agents.agentosetrenia.continualassistants;

import OSPABA.*;
import agents.agentosetrenia.*;
import generators.SpojityEmpirickyGenerator;
import generators.UniformGenerator;
import simulation.*;
import OSPABA.Process;

//meta! id="36"
public class ProcesOsetrenia extends OSPABA.Process
{
	private SpojityEmpirickyGenerator casOsetreniaSamostatneGen;
    private UniformGenerator casOsetreniaSanitkouGen;

	public ProcesOsetrenia(int id, Simulation mySim, CommonAgent myAgent)
	{
		super(id, mySim, myAgent);
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication

		double[] pravdepodobnosti = {0.1, 0.6, 0.3};
		double[] minCas = {10.0, 12.0, 14.0};
		double[] maxCas = {12.0, 14.0, 18.0};

		casOsetreniaSamostatneGen = new SpojityEmpirickyGenerator(pravdepodobnosti, minCas, maxCas, 1);
		casOsetreniaSanitkouGen = new UniformGenerator(15.0, 30.0, 1); //#;
	}

	//meta! sender="AgentOsetrenia", id="37", type="Start"
	public void processStart(MessageForm message)
	{
		MyMessage pacient = (MyMessage) message;
        double casOsetreniaMinuty;
		
		if (pacient.typPacienta.equals("SAMOSTATNE")) {
            casOsetreniaMinuty = casOsetreniaSamostatneGen.sample(); 
			System.out.println("11 ProcesOsetrenia SAM");
		} else {
			casOsetreniaMinuty = casOsetreniaSanitkouGen.sample();
			System.out.println("11 ProcesOsetrenia SANITKOU");
		}

		double casOsetrenia = casOsetreniaMinuty * 60.0;

		message.setCode(Mc.finish);
        hold(casOsetrenia, message);
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
