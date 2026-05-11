package gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

//** LLM */
public class PanelGrafu extends JPanel {
    private List<Double> dataSanitka;
    private List<Double> ciSanitka;
    private List<Double> dataSamostatne;
    private List<Double> ciSamostatne;

    public PanelGrafu() {
        this.dataSanitka = new ArrayList<>();
        this.ciSanitka = new ArrayList<>();
        this.dataSamostatne = new ArrayList<>();
        this.ciSamostatne = new ArrayList<>();
    }

    public void nastavData(List<Double> dataSanitka, List<Double> ciSanitka, List<Double> dataSamostatne, List<Double> ciSamostatne) {
        this.dataSanitka = dataSanitka;
        this.ciSanitka = ciSanitka;
        this.dataSamostatne = dataSamostatne;
        this.ciSamostatne = ciSamostatne;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int sirka = getWidth();
        int vyska = getHeight();
        int okraj = 60;

        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, sirka, vyska);

        g2.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i <= 10; i++) {
            int y = vyska - okraj - (i * (vyska - 2 * okraj) / 10);
            g2.drawLine(okraj, y, sirka - okraj, y);
        }

        g2.setColor(Color.BLACK);
        g2.drawLine(okraj, vyska - okraj, sirka - okraj, vyska - okraj);
        g2.drawLine(okraj, vyska - okraj, okraj, okraj);

        if (dataSanitka.isEmpty() || dataSamostatne.isEmpty()) {
            return;
        }

        double maxHodnota = 0;
        for (int i = 0; i < dataSanitka.size(); i++) {
            maxHodnota = Math.max(maxHodnota, dataSanitka.get(i) + ciSanitka.get(i));
        }
        for (int i = 0; i < dataSamostatne.size(); i++) {
            maxHodnota = Math.max(maxHodnota, dataSamostatne.get(i) + ciSamostatne.get(i));
        }
        maxHodnota *= 1.1;

        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        FontMetrics fontMetrics = g2.getFontMetrics();

        for (int i = 0; i <= 10; i++) {
            int y = vyska - okraj - (i * (vyska - 2 * okraj) / 10);
            String hodnotaLabel = String.format("%.0f", (maxHodnota / 10) * i);
            int labelSirka = fontMetrics.stringWidth(hodnotaLabel);
            g2.drawString(hodnotaLabel, okraj - labelSirka - 10, y + (fontMetrics.getAscent() / 2));
        }

        int pocetBodov = dataSanitka.size();
        double xKrok = (double) (sirka - 2 * okraj) / Math.max(1, pocetBodov - 1);
        double yKrok = (vyska - 2 * okraj) / maxHodnota;

        Polygon polySanitka = new Polygon();
        for (int i = 0; i < pocetBodov; i++) {
            int x = okraj + (int) (i * xKrok);
            int y = vyska - okraj - (int) ((dataSanitka.get(i) + ciSanitka.get(i)) * yKrok);
            polySanitka.addPoint(x, y);
        }
        for (int i = pocetBodov - 1; i >= 0; i--) {
            int x = okraj + (int) (i * xKrok);
            int y = vyska - okraj - (int) (Math.max(0, dataSanitka.get(i) - ciSanitka.get(i)) * yKrok);
            polySanitka.addPoint(x, y);
        }
        
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
        g2.setColor(Color.BLUE);
        g2.fillPolygon(polySanitka);

        Polygon polySamostatne = new Polygon();
        for (int i = 0; i < pocetBodov; i++) {
            int x = okraj + (int) (i * xKrok);
            int y = vyska - okraj - (int) ((dataSamostatne.get(i) + ciSamostatne.get(i)) * yKrok);
            polySamostatne.addPoint(x, y);
        }
        for (int i = pocetBodov - 1; i >= 0; i--) {
            int x = okraj + (int) (i * xKrok);
            int y = vyska - okraj - (int) (Math.max(0, dataSamostatne.get(i) - ciSamostatne.get(i)) * yKrok);
            polySamostatne.addPoint(x, y);
        }

        g2.setColor(Color.RED);
        g2.fillPolygon(polySamostatne);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        g2.setColor(Color.BLUE);
        for (int i = 0; i < pocetBodov - 1; i++) {
            int x1 = okraj + (int) (i * xKrok);
            int y1 = vyska - okraj - (int) (dataSanitka.get(i) * yKrok);
            int x2 = okraj + (int) ((i + 1) * xKrok);
            int y2 = vyska - okraj - (int) (dataSanitka.get(i + 1) * yKrok);
            g2.drawLine(x1, y1, x2, y2);
        }

        g2.setColor(Color.RED);
        for (int i = 0; i < pocetBodov - 1; i++) {
            int x1 = okraj + (int) (i * xKrok);
            int y1 = vyska - okraj - (int) (dataSamostatne.get(i) * yKrok);
            int x2 = okraj + (int) ((i + 1) * xKrok);
            int y2 = vyska - okraj - (int) (dataSamostatne.get(i + 1) * yKrok);
            g2.drawLine(x1, y1, x2, y2);
        }

        g2.setColor(Color.BLUE);
        g2.fillRect(sirka / 2 - 120, 20, 15, 15);
        g2.setColor(Color.BLACK);
        g2.drawString("Sanitka (Priemer + 95% IS)", sirka / 2 - 100, 32);

        g2.setColor(Color.RED);
        g2.fillRect(sirka / 2 + 50, 20, 15, 15);
        g2.setColor(Color.BLACK);
        g2.drawString("Samostatne (Priemer + 95% IS)", sirka / 2 + 70, 32);
    }
}