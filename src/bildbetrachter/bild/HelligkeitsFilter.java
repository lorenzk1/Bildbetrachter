package bildbetrachter.bild;

import bildbetrachter.gui.Parameter;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

/**
 * Verändert die Helligkeit des Bildes
 *
 * @author eih
 */
public class HelligkeitsFilter extends Filter {

    public HelligkeitsFilter(String name) {
        super(name);
        final int SHORTCUT_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        setKey(KeyStroke.getKeyStroke(KeyEvent.VK_H, SHORTCUT_MASK));
    }

    @Override
    public void anwenden(final Farbbild kopie) {
        int standard = 0;
        int min = -255;
        int max = 255;
        String labelText = "Stellen Sie die gewünschte Helligkeit ein.";
        int majorTickSpacing = 100;
        int minorTickSpacing = 20;
        Hashtable labelTabelle = new Hashtable();
        int[] werte = {-255, -127, 0, 127, 255};
        for (int i = 0; i < werte.length; i++) {
            labelTabelle.put(werte[i], new JLabel(("" + werte[i])));
        }
        Parameter parameter = new Parameter(standard, labelText, min, max,
                majorTickSpacing, minorTickSpacing, labelTabelle, kopie, this);
        parameter.setSnapToTicks(false);
        parameter.addPropertyChangeListener(new PropertyChangeListener() {
            //OK wurde geklickt

            @Override
            public void propertyChange(PropertyChangeEvent event) {
                ausfuehren(kopie, (Integer) event.getNewValue());
                anzeigeAktualisieren();
            }
        });
    }

    @Override
    public void ausfuehren(Farbbild bild, int value) {
        int height = bild.getHeight();
        int width = bild.getWidth();
        //auf alle Bildpunkte anwenden        
        if (-255 <= value && value <= 255) {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    Color c = bild.gibPunktfarbe(i, j);
                    boolean heller = value > 0;
                    int r = c.getRed() + value;
                    int g = c.getGreen() + value;
                    int b = c.getBlue() + value;

                    if (r < 0 || r > 255) {
                        if (heller) {
                            r = 255;
                        } else {
                            r = 0;
                        }
                    }
                    if (g < 0 || g > 255) {
                        if (heller) {
                            g = 255;
                        } else {
                            g = 0;
                        }
                    }
                    if (b < 0 || b > 255) {
                        if (heller) {
                            b = 255;
                        } else {
                            b = 0;
                        }
                    }
                    Color neu = new Color(r, g, b);
                    bild.setzePunktfarbe(i, j, neu);
                }
            }
        }
    }
}
