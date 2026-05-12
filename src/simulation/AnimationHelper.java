package simulation;

public class AnimationHelper {

    public static void animatePresun(MySimulation sim, MyMessage pacient, double casPresunu) {
        if (sim.animatorExists() && pacient.animaciaPacienta != null) {
            double t0 = sim.currentTime();
            double dt = casPresunu / 3.0;
            double dtFinal = casPresunu - (2.0 * dt); 

            if (pacient.typPacienta.equals(simulation.Constants.PACIENT_SAMOSTATNE)) {
                double startX = sim.bodVchodSamostatne.x;
                double startY = sim.bodVchodSamostatne.y;

                java.awt.geom.Point2D p1 = new java.awt.geom.Point2D.Double(startX + 100.0, startY);
                java.awt.geom.Point2D p2 = new java.awt.geom.Point2D.Double(startX + 100.0, 200.0);
                java.awt.geom.Point2D p3 = new java.awt.geom.Point2D.Double(500.0, 270.0); 

                pacient.animaciaPacienta.moveTo(t0, dt, p1);
                pacient.animaciaPacienta.moveTo(t0 + dt, dt, p2);
                pacient.animaciaPacienta.moveTo(t0 + 2.0 * dt, dtFinal, p3);
            } else {
                double cielX = 500.0;
                java.awt.geom.Point2D ciel = new java.awt.geom.Point2D.Double(cielX, sim.bodVchodSanitka.y);
                pacient.animaciaPacienta.moveTo(t0, casPresunu, ciel);
            }
        }
    }

    public static void animateVstupVysetrenie(MySimulation sim, MyMessage pacient) {
        if (sim.animatorExists() && pacient.animaciaPacienta != null && pacient.priradenaSestra != null) {
            double t0 = sim.currentTime();
            double dt = pacient.casPresunu / 3.0;
            double dtFinal = pacient.casPresunu - (2.0 * dt);
            double epsilon = 0.0001;

            double targetY = (pacient.priradenaMiestnost.id - 1) * 66.0;
            double corridorX = 830.0;

            java.awt.geom.Point2D posPacient = pacient.animaciaPacienta.getPosition(t0);
            java.awt.geom.Point2D p1Pac = new java.awt.geom.Point2D.Double(corridorX, posPacient.getY());
            java.awt.geom.Point2D p2Pac = new java.awt.geom.Point2D.Double(corridorX, targetY);
            java.awt.geom.Point2D p3Pac = new java.awt.geom.Point2D.Double(corridorX - 48.0, targetY);

            pacient.animaciaPacienta.moveTo(t0, dt, p1Pac);
            pacient.animaciaPacienta.moveTo(t0 + dt + epsilon, dt, p2Pac);
            pacient.animaciaPacienta.moveTo(t0 + 2.0 * dt + (2.0 * epsilon), dtFinal, p3Pac);

            java.awt.geom.Point2D posSestra = pacient.priradenaSestra.animaciaPracovnika.getPosition(t0);
            java.awt.geom.Point2D p1Ses = new java.awt.geom.Point2D.Double(corridorX, posSestra.getY());
            java.awt.geom.Point2D p2Ses = new java.awt.geom.Point2D.Double(corridorX, targetY);
            java.awt.geom.Point2D p3Ses = new java.awt.geom.Point2D.Double(corridorX + 10.0, targetY);

            pacient.priradenaSestra.animaciaPracovnika.moveTo(t0, dt, p1Ses);
            pacient.priradenaSestra.animaciaPracovnika.moveTo(t0 + dt + epsilon, dt, p2Ses);
            pacient.priradenaSestra.animaciaPracovnika.moveTo(t0 + 2.0 * dt + (2.0 * epsilon), dtFinal, p3Ses);
        }
    }

    public static void animateOsetrenie(MySimulation sim, MyMessage pacient) {
        if (sim.animatorExists() && pacient.animaciaPacienta != null) {
            sim.animRadyOsetrenie[pacient.priorita - 1].remove(pacient.animaciaPacienta);

            double t0 = sim.currentTime();
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
    }

    public static void pridatDoRaduOsetrenia(MySimulation sim, MyMessage pacient, int pocetVrade) {
        if (sim.animatorExists() && pacient.animaciaPacienta != null) {
            sim.animRadyOsetrenie[pacient.priorita - 1].insert(pacient.animaciaPacienta);

            if (pocetVrade < 0) pocetVrade = 0;

            double frontX = 730.0;
            double targetX = frontX - (pocetVrade * 60.0);
            double targetY = (7 * 66.0) + 200.0; 
            
            if (pacient.typPacienta.equals(simulation.Constants.PACIENT_SAMOSTATNE)) {
                targetY += 40.0; 
            }

            // Okamžite ho presunie na správnu pozíciu
            pacient.animaciaPacienta.setPosition(new java.awt.geom.Point2D.Double(targetX, targetY));
        }
    }
}