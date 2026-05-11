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
		if (mySim().animatorExists() && pacient.animaciaPacienta != null) {
            double t0 = mySim().currentTime();
            
            // Fix floating point math so it perfectly equals casPresunu
            double dt = casPresunu / 3.0;
            double dtFinal = casPresunu - (2.0 * dt); 

            if (pacient.typPacienta.equals(simulation.Constants.PACIENT_SAMOSTATNE)) {
                double startX = ((MySimulation) mySim()).bodVchodSamostatne.x;
                double startY = ((MySimulation) mySim()).bodVchodSamostatne.y;

                // Move 1: Right down the hall
                java.awt.geom.Point2D p1 = new java.awt.geom.Point2D.Double(startX + 100.0, startY);
                // Move 2: Up the hall
                java.awt.geom.Point2D p2 = new java.awt.geom.Point2D.Double(startX + 100.0, 200.0);
                // Move 3: Right, stopping EXACTLY at the entrance to the waiting room
                java.awt.geom.Point2D p3 = new java.awt.geom.Point2D.Double(500.0, 270.0); 

                pacient.animaciaPacienta.moveTo(t0, dt, p1);
                pacient.animaciaPacienta.moveTo(t0 + dt, dt, p2);
                pacient.animaciaPacienta.moveTo(t0 + 2.0 * dt, dtFinal, p3);
                
            } else {
                double cielX = 500.0;
                java.awt.geom.Point2D ciel = new java.awt.geom.Point2D.Double(cielX, ((MySimulation) mySim()).bodVchodSanitka.y);
                pacient.animaciaPacienta.moveTo(t0, casPresunu, ciel);
            }
        }

		message.setCode(Mc.finish);

		// if (mySim().animatorExists() && pacient.animaciaPacienta != null) {
		// 	pacient.animaciaPacienta.moveTo(
		// 		mySim().currentTime(),
		// 		casPresunu,
		// 		((MySimulation) mySim()).bodVstupneVysetrenie
		// 	);
		// }

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
