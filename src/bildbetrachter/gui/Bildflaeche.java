package bildbetrachter.gui;

import bildbetrachter.bild.Farbbild;
import java.awt.*;
import javax.swing.*;

/**
 * Bildflaeche ist eine Swing-Komponente, die ein Farbbild anzeigen kann. Sie ist konstruiert als
 * eine Subklasse von JComponent und bietet die zusätzliche Funktionalität, dass ein eingetragenes
 * Farbbild an der Oberfläche dieser Komponente angezeigt wird.
 *
 * @version 1.1
 */
public class Bildflaeche extends JComponent {

    //Standardwerte
    private static final int BREITE = 640;
    private static final int HOEHE = 480;
    // Die aktuelle Breite und Höhe dieser Bildfläche
    private int breite, hoehe;

    // Ein interner Bildpuffer, der zum Zeichnen verwendet wird.
    // Wenn die Fläche tatsächlich angezeigt werden soll, wird dieser
    // Puffer auf den Bildschirm kopiert.
    private Farbbild bild;

    /**
     * Erzeuge eine neue, leere Bildfläche.
     */
    public Bildflaeche() {
        breite = BREITE;
        hoehe = HOEHE;
        bild = null;
    }

    /**
     * Setze das Bild, das diese Bildfläche anzeigen soll.
     *
     * @param bild das anzuzeigende Bild.
     */
    public void setzeBild(Farbbild bild) {
        if (bild != null) {
            breite = bild.getWidth();
            hoehe = bild.getHeight();
        } else {
            breite = BREITE;
            hoehe = HOEHE;
        }
        this.bild = bild;
        repaint();
    }

    /**
     * Lösche die Bildfläche.
     */
    public void loeschen() {
        breite = BREITE;
        hoehe = HOEHE;
        bild = null;
        repaint();
    }

    /**
     * Gib das Bild zurück, das diese Bildfläche anzeigt.
     *
     * @return das angezeigte Bild.
     */
    public Farbbild gibBild() {
        return this.bild;
    }

    // Die folgenden Methoden redefinieren Methoden, die aus
    // Superklassen geerbt wurden.
    /**
     * Teile dem Layout-Manager mit, wie groß diese Komponente sein soll. (Diese Methode wird vom
     * Layout-Manager aufgerufen, um die Komponenten geeignet platzieren zu können.)
     *
     * @return Die bevorzugte Größe (als Dimension) dieser Komponente.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(breite, hoehe);
    }

    /**
     * Diese Komponente soll erneut angezeigt werden. Kopiere das intern gehaltene Bild auf den
     * Bildschirm. (Diese Methode wird von Swing aus jedesmal aufgerufen, wenn diese Komponente
     * angezeigt werden soll.)
     *
     * @param g Der Grafik-Kontext, mit dem auf dieser Komponente gezeichnet werden kann.
     */
    @Override
    public void paintComponent(Graphics g) {
        Dimension size = getSize();
        g.clearRect(0, 0, size.width, size.height);
        if (bild != null) {
            g.drawImage(bild, 0, 0, null);
        }
    }
}
