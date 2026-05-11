package agents.agentvstupvysetrenia.continualassistants;

import OSPABA.*;
import simulation.*;
import agents.agentvstupvysetrenia.*;
import generators.RovnomernyDisktretnyGenerator;
import generators.SpojityEmpirickyGenerator;
import generators.UniformGenerator;
import OSPABA.Process;

//meta! id="34"
public class ProcesVstupVysetrenia extends OSPABA.Process
{
	// private TrojuholnikovyGenerator genCasPresunuPersonalu;
	private SpojityEmpirickyGenerator casVstupVysSamostatneGen;
    private RovnomernyDisktretnyGenerator casVstupVysSanitkouGen;
	private UniformGenerator prioritaGen;

	public ProcesVstupVysetrenia(int id, Simulation mySim, CommonAgent myAgent)
	{
		super(id, mySim, myAgent);

		// genCasPresunuPersonalu = new TrojuholnikovyGenerator(15.0, 20.0, 45.0, ((MySimulation)mySim()).masterRandom.nextInt());
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication
		double[] pravdepodobnosti = {0.6, 0.4};
		double[] minCas = {3.0, 5.0};
		double[] maxCas = {5.0, 9.0};

		casVstupVysSamostatneGen = new SpojityEmpirickyGenerator(pravdepodobnosti, minCas, maxCas, ((MySimulation)mySim()).masterRandom.nextInt());
		casVstupVysSanitkouGen = new RovnomernyDisktretnyGenerator(4, 8, ((MySimulation)mySim()).masterRandom.nextInt());
		prioritaGen = new UniformGenerator(0.0, 1.0, ((MySimulation)mySim()).masterRandom.nextInt());
	}

	//meta! sender="AgentVstupVysetrenia", id="35", type="Start"
	public void processStart(MessageForm message)
	{
		System.out.println("7 processStart of ProcesVstupVysetrenia");
		MyMessage pacient = (MyMessage) message;
		double casVstupVysetreniaSekundy;

		if (pacient.typPacienta.equals(simulation.Constants.PACIENT_SAMOSTATNE)) {
            casVstupVysetreniaSekundy = casVstupVysSamostatneGen.sample() * 60.0;
        } else {
            casVstupVysetreniaSekundy = casVstupVysSanitkouGen.sample() * 60.0;
        }

		// double casPresunuPersonalu = genCasPresunuPersonalu.sample();
		// double celkovyCas = casVstupVysetreniaSekundy + casPresunuPersonalu;

		//!! ANIMACIA TO VSTUP VYSETRENIE
		// if (mySim().animatorExists() && pacient.animaciaPacienta != null && pacient.priradenaSestra != null) {
            
        //     double t0 = mySim().currentTime();
        //     double dt = pacient.casPresunu / 3.0;
        //     double dtFinal = pacient.casPresunu - (2.0 * dt);

        //     double targetY =  (pacient.priradenaMiestnost.id - 1) * 66.0;
        //     double corridorX = 830.0;

        //     java.awt.geom.Point2D posPacient = pacient.animaciaPacienta.getPosition(t0);
        //     double pacStartY = posPacient.getY();

        //     java.awt.geom.Point2D p1Pac = new java.awt.geom.Point2D.Double(corridorX, pacStartY);
        //     java.awt.geom.Point2D p2Pac = new java.awt.geom.Point2D.Double(corridorX, targetY);
        //     java.awt.geom.Point2D p3Pac = new java.awt.geom.Point2D.Double(corridorX + 50.0, targetY);

        //     pacient.animaciaPacienta.moveTo(t0, dt, p1Pac);
        //     pacient.animaciaPacienta.moveTo(t0 + dt, dt, p2Pac);
        //     pacient.animaciaPacienta.moveTo(t0 + 2.0 * dt, dtFinal, p3Pac);


        //     java.awt.geom.Point2D posSestra = pacient.priradenaSestra.animaciaPracovnika.getPosition(t0);
        //     double sesStartY = posSestra.getY();
        //     double sesTargetY = targetY;

        //     java.awt.geom.Point2D p1Ses = new java.awt.geom.Point2D.Double(corridorX, sesStartY);
        //     java.awt.geom.Point2D p2Ses = new java.awt.geom.Point2D.Double(corridorX, sesTargetY);
        //     java.awt.geom.Point2D p3Ses = new java.awt.geom.Point2D.Double(corridorX + 0.0, sesTargetY);

        //     pacient.priradenaSestra.animaciaPracovnika.moveTo(t0, dt, p1Ses);
        //     pacient.priradenaSestra.animaciaPracovnika.moveTo(t0 + dt, dt, p2Ses);
        //     pacient.priradenaSestra.animaciaPracovnika.moveTo(t0 + 2.0 * dt, dtFinal, p3Ses);
        // }

		if (mySim().animatorExists() && pacient.animaciaPacienta != null && pacient.priradenaSestra != null) {
            
            double t0 = mySim().currentTime();
            double dt = pacient.casPresunu / 3.0;
            double dtFinal = pacient.casPresunu - (2.0 * dt);
            double epsilon = 0.0001;

            double targetY = (pacient.priradenaMiestnost.id - 1) * 66.0;
            double corridorX = 830.0;

            java.awt.geom.Point2D posPacient = pacient.animaciaPacienta.getPosition(t0);
            java.awt.geom.Point2D p1Pac = new java.awt.geom.Point2D.Double(corridorX, posPacient.getY());
            java.awt.geom.Point2D p2Pac = new java.awt.geom.Point2D.Double(corridorX, targetY);
            java.awt.geom.Point2D p3Pac = new java.awt.geom.Point2D.Double(corridorX + 50.0, targetY);

            pacient.animaciaPacienta.moveTo(t0, dt, p1Pac);
            pacient.animaciaPacienta.moveTo(t0 + dt + epsilon, dt, p2Pac);
            pacient.animaciaPacienta.moveTo(t0 + 2.0 * dt + (2.0 * epsilon), dtFinal, p3Pac);


            java.awt.geom.Point2D posSestra = pacient.priradenaSestra.animaciaPracovnika.getPosition(t0);
            java.awt.geom.Point2D p1Ses = new java.awt.geom.Point2D.Double(corridorX, posSestra.getY());
            java.awt.geom.Point2D p2Ses = new java.awt.geom.Point2D.Double(corridorX, targetY);
            java.awt.geom.Point2D p3Ses = new java.awt.geom.Point2D.Double(corridorX + 30.0, targetY);

            pacient.priradenaSestra.animaciaPracovnika.moveTo(t0, dt, p1Ses);
            pacient.priradenaSestra.animaciaPracovnika.moveTo(t0 + dt + epsilon, dt, p2Ses);
            pacient.priradenaSestra.animaciaPracovnika.moveTo(t0 + 2.0 * dt + (2.0 * epsilon), dtFinal, p3Ses);
        }

		message.setCode(Mc.finish);
		hold(casVstupVysetreniaSekundy + pacient.casPresunu, message);
	}

	//meta! userInfo="Process messages defined in code", id="0"
	public void processDefault(MessageForm message)
	{
		// switch (message.code())
		// {
		// }
		System.out.println("8 processDefault of ProcesVstupVysetrenia. Runs after hold() expires");
		MyMessage pacient = (MyMessage) message;
            
		double p = prioritaGen.sample();

		if (pacient.typPacienta.equals(simulation.Constants.PACIENT_SAMOSTATNE)) {
			if (p < 0.10) {
				pacient.priorita = 1;
			} else if (p < 0.30) {
				pacient.priorita = 2;
			} else if (p < 0.45) {
				pacient.priorita = 3;
			} else if (p < 0.70) {
				pacient.priorita = 4;
			} else {
				pacient.priorita = 5;
			}
		} 
		else {
			if (p < 0.30) {
				pacient.priorita = 1;
			} else if (p < 0.55) {
				pacient.priorita = 2;
			} else if (p < 0.75) {
				pacient.priorita = 3;
			} else if (p < 0.90) {
				pacient.priorita = 4;
			} else {
				pacient.priorita = 5;
			}
		}
		
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
	public AgentVstupVysetrenia myAgent()
	{
		return (AgentVstupVysetrenia)super.myAgent();
	}

}
