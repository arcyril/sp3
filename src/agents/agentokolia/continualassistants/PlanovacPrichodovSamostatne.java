package agents.agentokolia.continualassistants;

import OSPABA.*;
import agents.agentokolia.*;
import generators.LognormalnyGenerator;
import simulation.*;

//meta! id="20"
public class PlanovacPrichodovSamostatne extends OSPABA.Scheduler
{
	//## to configure
	private LognormalnyGenerator prichodSamGen;

	public PlanovacPrichodovSamostatne(int id, Simulation mySim, CommonAgent myAgent)
	{
		super(id, mySim, myAgent);

		prichodSamGen = new LognormalnyGenerator(6.05, 0.83, ((MySimulation)mySim()).masterRandom.nextInt());
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
		// System.out.println("planovac prichodov sam. Kod " + message.code() + ", Mc.start = " + Mc.start);
		switch (message.code())
		{
			case Mc.start:
				double prvyPrichod = prichodSamGen.sample();
				// System.out.println("Planujem prvy prichod o: " + prvyPrichod + " sekund");
				//!!
				message.setCode(Mc.prisielSamostatne);
				hold(prvyPrichod, message);
				break;
				
			default:
				// System.out.println("čas: " + mySim().currentTime() + ", pacient prisiel");
				//!!
				MyMessage novyPacient = new MyMessage(mySim());
				novyPacient.setTypPacienta(simulation.Constants.PACIENT_SAMOSTATNE); 
				novyPacient.setCasPrichodu(mySim().currentTime()); 

				//** LLM usage, to specify
				String[] info = {
					String.valueOf(novyPacient.idPacienta), 
					simulation.Constants.PACIENT_SAMOSTATNE,
					simulation.Constants.STAV_PRICHADZA_URGENT, 
					String.format("%.2f", mySim().currentTime())
				};
				((MySimulation)mySim()).aktualniPacienti.put(novyPacient.idPacienta, info);
				((MySimulation)mySim()).refreshUI();
						
				if (mySim().animatorExists()) {
                    novyPacient.animaciaPacienta = new OSPAnimator.AnimImageItem("./assets/pacient_samostatne.png");
                    novyPacient.animaciaPacienta.setPosition(((MySimulation)mySim()).bodVchodSamostatne);
                    mySim().animator().register(novyPacient.animaciaPacienta);
                }
				//!!
				novyPacient.setAddressee(myAgent());
				//!!
				novyPacient.setCode(Mc.prisielSamostatne); 
				notice(novyPacient);

				//!!
				hold(prichodSamGen.sample(), message);
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
