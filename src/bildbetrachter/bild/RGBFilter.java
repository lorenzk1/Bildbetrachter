package bildbetrachter.bild;

import bildbetrachter.gui.Parameter;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import java.util.LinkedList;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.KeyStroke;

/**
 * Verändert die Farbwerte des Bildes
 *
 * @author eih
 */
public class RGBFilter extends Filter {

    private String labelText;
    private JSlider rSlider, gSlider, bSlider, aSlider;
    private LinkedList<JComponent> guiList;

    public RGBFilter(String name) {
        super(name);
        final int SHORTCUT_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        setKey(KeyStroke.getKeyStroke(KeyEvent.VK_R, SHORTCUT_MASK));
    }

    @Override
    public void anwenden(final Farbbild kopie) {
        initComponents();
        Parameter parameter = new Parameter(labelText, guiList, kopie, this);
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
        int rSld = rSlider.getValue();
        int gSld = gSlider.getValue();
        int bSld = bSlider.getValue();
        int aSld = aSlider.getValue();
        Color neu = new Color(0);

        if (-255 <= rSld && rSld <= 255
                || -255 <= gSld && gSld <= 255
                || -255 <= bSld && bSld <= 255
                || -255 <= aSld && aSld <= 255) {
            //auf alle Bildpunkte anwenden
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    Color c = bild.gibPunktfarbe(i, j);
                    int r = c.getRed() + rSld;
                    int g = c.getGreen() + gSld;
                    int b = c.getBlue() + bSld;
                    int a = c.getAlpha() + aSld;

                    if (r < 0 || r > 255) {
                        if (r > 255) {
                            r = 255;
                        } else {
                            r = 0;
                        }
                    }
                    if (g < 0 || g > 255) {
                        if (g > 255) {
                            g = 255;
                        } else {
                            g = 0;
                        }
                    }
                    if (b < 0 || b > 255) {
                        if (b > 255) {
                            b = 255;
                        } else {
                            b = 0;
                        }
                    }
                    if (a < 0 || a > 255) {
                        if (a > 255) {
                            a = 255;
                        } else {
                            a = 0;
                        }
                    }
                    neu = new Color(r, g, b, a);
                    bild.setzePunktfarbe(i, j, neu);
                }
            }
        }
    }

    private void initComponents() {
        labelText = "Stellen Sie die gewünschten Farbwerte ein.";
        int standard = 0;
        int min = -255;
        int max = 255;
        int majorTickSpacing = 100;
        int minorTickSpacing = 20;
        Hashtable labelTable = new Hashtable();
        int[] werte = {-255, -127, 0, 127, 255};
        for (int i = 0; i < werte.length; i++) {
            labelTable.put(werte[i], new JLabel(("" + werte[i])));
        }
        guiList = new LinkedList<>();
        //rot        
        JLabel rLabel = new JLabel("rot");
        rLabel.setForeground(Color.RED);
        guiList.add(rLabel);
        rSlider = new JSlider(min, max, standard);
        rSlider.setMajorTickSpacing(majorTickSpacing);
        rSlider.setMinorTickSpacing(minorTickSpacing);
        rSlider.setPaintTicks(true);
        rSlider.setPaintLabels(true);
        rSlider.setLabelTable(labelTable);
        guiList.add(rSlider);
        //grün        
        JLabel gLabel = new JLabel("grün");
        gLabel.setForeground(Color.GREEN);
        guiList.add(gLabel);
        gSlider = new JSlider(min, max, standard);
        gSlider.setMajorTickSpacing(majorTickSpacing);
        gSlider.setMinorTickSpacing(minorTickSpacing);
        gSlider.setPaintTicks(true);
        gSlider.setPaintLabels(true);
        gSlider.setLabelTable(labelTable);
        guiList.add(gSlider);
        //blau          
        JLabel bLabel = new JLabel("blau");
        bLabel.setForeground(Color.BLUE);
        guiList.add(bLabel);
        bSlider = new JSlider(min, max, standard);
        bSlider.setMajorTickSpacing(majorTickSpacing);
        bSlider.setMinorTickSpacing(minorTickSpacing);
        bSlider.setPaintTicks(true);
        bSlider.setPaintLabels(true);
        bSlider.setLabelTable(labelTable);
        guiList.add(bSlider);
        //alpha        
        JLabel aLabel = new JLabel("alpha");
        guiList.add(aLabel);
        aSlider = new JSlider(min, max, standard);
        aSlider.setMajorTickSpacing(majorTickSpacing);
        aSlider.setMinorTickSpacing(minorTickSpacing);
        aSlider.setPaintTicks(true);
        aSlider.setPaintLabels(true);
        aSlider.setLabelTable(labelTable);
        guiList.add(aSlider);
    }
}
