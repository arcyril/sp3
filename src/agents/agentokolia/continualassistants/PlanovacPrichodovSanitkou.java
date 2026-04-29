package agents.agentokolia.continualassistants;

import OSPABA.*;
import agents.agentokolia.*;
import generators.EmpirickyGenerator;
import simulation.*;

//meta! id="22"
public class PlanovacPrichodovSanitkou extends OSPABA.Scheduler
{
	private EmpirickyGenerator genSanitka = new EmpirickyGenerator(2);

	public PlanovacPrichodovSanitkou(int id, Simulation mySim, CommonAgent myAgent)
	{
		super(id, mySim, myAgent);
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication
	}

	//meta! userInfo="Process messages defined in code", id="0"
	public void processDefault(MessageForm message)
	{
		switch (message.code())
		{
			case Mc.start:
				message.setCode(Mc.prisielSanitkou);
				hold(genSanitka.sample(), message);
				break;
			default:
				System.out.println("Time: " + String.format("%.2f", mySim().currentTime()) + ", pacient prisiel sanitkou");

				MyMessage novyPacient = new MyMessage(mySim());
				novyPacient.setTypPacienta("SANITKOU"); 
				novyPacient.setCasPrichodu(mySim().currentTime()); 
				
				String[] info = {
					String.valueOf(novyPacient.idPacienta), 
					"SANITKOU", 
					"Ide na urgent. príjem", 
					String.format("%.2f", mySim().currentTime())
				};
				((MySimulation)mySim()).aktualniPacienti.put(novyPacient.idPacienta, info);
				((MySimulation)mySim()).refreshUI();

				
				novyPacient.setAddressee(myAgent());				
				novyPacient.setCode(Mc.prisielSanitkou); 
				notice(novyPacient);

				hold(genSanitka.sample(), message);
				break;
		}
	}

	//meta! userInfo="Generated code: do not modify", tag="begin"
	@Override
	public void processMessage(MessageForm message)
	{
		switch (message.code())
		{
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
