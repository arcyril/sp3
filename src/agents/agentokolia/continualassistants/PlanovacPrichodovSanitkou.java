package agents.agentokolia.continualassistants;

import OSPABA.*;
import agents.agentokolia.*;
import generators.ExponencialnyGenerator;
import simulation.*;

//meta! id="22"
public class PlanovacPrichodovSanitkou extends OSPABA.Scheduler
{
	private ExponencialnyGenerator prichodSanitkouGen;

	public PlanovacPrichodovSanitkou(int id, Simulation mySim, CommonAgent myAgent)
	{
		super(id, mySim, myAgent);

		prichodSanitkouGen = new ExponencialnyGenerator(1.0 / 351.0, ((MySimulation)mySim()).masterRandom.nextInt());
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
				hold(prichodSanitkouGen.sample(), message);
				break;
			default:
				System.out.println("Time: " + String.format("%.2f", mySim().currentTime()) + ", pacient prisiel sanitkou");

				MyMessage novyPacient = new MyMessage(mySim());
				novyPacient.setTypPacienta(simulation.Constants.PACIENT_SANITKOU); 
				novyPacient.setCasPrichodu(mySim().currentTime()); 

				// if (mySim().animatorExists()) {
				// 	novyPacient.animaciaPacienta = new OSPAnimator.AnimImageItem("pacient_sanitkou.png");
				// 	novyPacient.animaciaPacienta.setPosition(((MySimulation)mySim()).bodVchodSanitka);
				// 	mySim().animator().register(novyPacient.animaciaPacienta);
				// }
				
				String[] info = {
					String.valueOf(novyPacient.idPacienta), 
					simulation.Constants.PACIENT_SANITKOU,
					simulation.Constants.STAV_IDE_URGENT, 
					String.format("%.2f", mySim().currentTime())
				};
				((MySimulation)mySim()).aktualniPacienti.put(novyPacient.idPacienta, info);
				((MySimulation)mySim()).refreshUI();

				if (mySim().animatorExists()) {
                    novyPacient.animaciaPacienta = new OSPAnimator.AnimImageItem("pacient_sanitkou.png");
                    novyPacient.animaciaPacienta.setPosition(((MySimulation)mySim()).bodVchodSanitka);
                    mySim().animator().register(novyPacient.animaciaPacienta);
                }

				novyPacient.setAddressee(myAgent());				
				novyPacient.setCode(Mc.prisielSanitkou); 
				notice(novyPacient);

				hold(prichodSanitkouGen.sample(), message);
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
