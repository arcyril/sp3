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
	// private TrojuholnikovyGenerator genCasPresunuPersonalu;
	private SpojityEmpirickyGenerator casOsetreniaSamostatneGen;
    private UniformGenerator casOsetreniaSanitkouGen;

	public ProcesOsetrenia(int id, Simulation mySim, CommonAgent myAgent)
	{
		super(id, mySim, myAgent);

		// genCasPresunuPersonalu = new TrojuholnikovyGenerator(15.0, 20.0, 45.0, ((MySimulation)mySim()).masterRandom.nextInt());
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
		//!! ANIMACIA
		if (mySim().animatorExists() && pacient.animaciaPacienta != null) {
            ((MySimulation)mySim()).animRadyOsetrenie[pacient.priorita - 1].remove(pacient.animaciaPacienta);
        }
		
		if (pacient.typPacienta.equals(simulation.Constants.PACIENT_SAMOSTATNE)) {
            casOsetreniaSekundy = casOsetreniaSamostatneGen.sample() * 60.0; 
			System.out.println("11 ProcesOsetrenia SAM");
		} else {
			casOsetreniaSekundy = casOsetreniaSanitkouGen.sample() * 60.0;
			System.out.println("11 ProcesOsetrenia SANITKOU");
		}

		// double casPresunuPersonalu = genCasPresunuPersonalu.sample();
		// double celkovyCas = casOsetreniaSekundy + casPresunuPersonalu;

		//!! ANIMACIA OSETRENIE
		if (mySim().animatorExists() && pacient.animaciaPacienta != null) {
            double t0 = mySim().currentTime();
            double dt = pacient.casPresunu / 3.0;
            double dtFinal = pacient.casPresunu - (2.0 * dt);
            double epsilon = 0.0001;
            double corridorX = 830.0; 

            double targetY;
            if (pacient.priradenaMiestnost.typ.equals("A")) {
                targetY = 478.0 + ((pacient.priradenaMiestnost.id - 1) * 66.0);
            } else {
                targetY = (pacient.priradenaMiestnost.id - 1) * 66.0 + 4;
            }

            java.awt.geom.Point2D p1Pac = new java.awt.geom.Point2D.Double(corridorX, pacient.animaciaPacienta.getPosition(t0).getY());
            java.awt.geom.Point2D p2Pac = new java.awt.geom.Point2D.Double(corridorX, targetY);
            java.awt.geom.Point2D p3Pac = new java.awt.geom.Point2D.Double(corridorX - 48.0, targetY);
            
            pacient.animaciaPacienta.moveTo(t0, dt, p1Pac);
            pacient.animaciaPacienta.moveTo(t0 + dt + epsilon, dt, p2Pac);
            pacient.animaciaPacienta.moveTo(t0 + 2.0 * dt + (2.0 * epsilon), dtFinal, p3Pac);

            if (pacient.priradenaSestra != null && pacient.priradenaSestra.animaciaPracovnika != null) {
                java.awt.geom.Point2D posSestra = pacient.priradenaSestra.animaciaPracovnika.getPosition(t0);
                java.awt.geom.Point2D p1Ses = new java.awt.geom.Point2D.Double(corridorX, posSestra.getY());
                java.awt.geom.Point2D p2Ses = new java.awt.geom.Point2D.Double(corridorX, targetY);
                java.awt.geom.Point2D p3Ses = new java.awt.geom.Point2D.Double(corridorX + 10.0, targetY);

                pacient.priradenaSestra.animaciaPracovnika.moveTo(t0, dt, p1Ses);
                pacient.priradenaSestra.animaciaPracovnika.moveTo(t0 + dt + epsilon, dt, p2Ses);
                pacient.priradenaSestra.animaciaPracovnika.moveTo(t0 + 2.0 * dt + (2.0 * epsilon), dtFinal, p3Ses);
            }

            if (pacient.priradenyLekar != null && pacient.priradenyLekar.animaciaPracovnika != null) {
                java.awt.geom.Point2D posLekar = pacient.priradenyLekar.animaciaPracovnika.getPosition(t0);
                java.awt.geom.Point2D p1Doc = new java.awt.geom.Point2D.Double(corridorX, posLekar.getY());
                java.awt.geom.Point2D p2Doc = new java.awt.geom.Point2D.Double(corridorX, targetY);
                java.awt.geom.Point2D p3Doc = new java.awt.geom.Point2D.Double(corridorX + 72.0, targetY);

                pacient.priradenyLekar.animaciaPracovnika.moveTo(t0, dt, p1Doc);
                pacient.priradenyLekar.animaciaPracovnika.moveTo(t0 + dt + epsilon, dt, p2Doc);
                pacient.priradenyLekar.animaciaPracovnika.moveTo(t0 + 2.0 * dt + (2.0 * epsilon), dtFinal, p3Doc);
            }
        }

		message.setCode(Mc.finish);
        hold(casOsetreniaSekundy + pacient.casPresunu, message);
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
