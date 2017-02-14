package bildbetrachter.gui;

import bildbetrachter.bild.Farbbild;
import bildbetrachter.bild.Filter;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Diese Klasse erstellt ein Fenster, welches zur Parameterabfrage genutzt
 * werden kann. Rechts wird ein Vorschaubild angezeigt.
 *
 * @author eih
 * @author jharbeck
 */
public class Parameter extends JComponent {

    private final int value;
    private final int standardWert;
    private final String titel;
    private final String labelText;
    private final LinkedList<JComponent> list;
    private final Farbbild bild;
    private Farbbild kopie;
    private final Filter filter;
    private JFrame frame;
    private Bildflaeche vorschauFlaeche;
    private BufferedImage vorschauBild;

    /**
     * Generiert ein Frame zur Parametereingabe.
     *
     * @param labelText Inhalt des Beschreibungsfeldes
     * @param list Liste aus JComponenten die alle Parameter des Filters enthält
     * @param bild das Farbbild
     * @param filter der Filter, der das Frame aufruft (also this eingeben)
     */
    public Parameter(String labelText,
            LinkedList<JComponent> list, Farbbild bild, final Filter filter) {
        this.titel = "Parametereingabe";
        frame = new JFrame(this.titel);
        // Symbol des Fensters festlegen
        BufferedImage image = null;
        try {
            image = ImageIO.read(Bildbetrachter.class.getResource("/bildbetrachter/images/run.png"));
        } catch (IllegalArgumentException | IOException e) {
            System.out.println(e.toString());
        }
        if (image != null) {
            frame.setIconImage(image);
        }
        this.value = 0;
        this.standardWert = 0;
        this.labelText = labelText;
        this.list = list;
        this.bild = bild;
        this.filter = filter;
        run();
    }

    /**
     * Generiert ein Frame zur Parametereingabe, übergeben werden die Parameter
     * für einen JSlider.
     *
     * @param standard Standardwert, sollte in der Regel der Mittelwert sein
     * @param labelText Inhalt des Beschreibungsfeldes
     * @param min Minimaler Wert des Parameters
     * @param max Maximaler Wert des Parameters
     * @param majorTickSpacing Haupteinteilung des Schiebereglers
     * @param minorTickSpacing Untereinteilung des Schiebereglers
     * @param labelTable hiermit man Labels definieren, die an der
     * entsprechenden Stelle angezeigt werden. Hierzu kann man eine Hashtable
     * registrieren, der man zu den passenden Werten (angegeben als Integer)
     * entsprechende JLabels zuordnet, die dann unter den Strichen angezeigt
     * werden.
     * @param bild das Farbbild, wenn kein Vorschaubild gewünscht, null
     * übergeben
     * @param filter der Filter, der das Frame aufruft (also this eingeben)
     */
    public Parameter(int standard, String labelText,
            int min, int max, int majorTickSpacing,
            int minorTickSpacing, Hashtable labelTable,
            Farbbild bild, final Filter filter) {
        this.titel = "Parametereingabe";
        frame = new JFrame(this.titel);
        // Symbol des Fensters festlegen
        BufferedImage image = null;
        try {
            image = ImageIO.read(Bildbetrachter.class.getResource("/bildbetrachter/images/run.png"));
        } catch (IllegalArgumentException | IOException e) {
            System.out.println(e.toString());
        }
        if (image != null) {
            frame.setIconImage(image);
        }
        JSlider slider = new JSlider(min, max, standard);
        // ticks
        slider.setMajorTickSpacing(majorTickSpacing);
        slider.setMinorTickSpacing(minorTickSpacing);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setLabelTable(labelTable);
        list = new LinkedList<>();
        list.add(slider);

        this.value = slider.getValue();
        this.standardWert = standard;
        this.labelText = labelText;
        this.bild = bild;
        this.filter = filter;
        run();
    }

    /**
     * Stellt ein, ob Slider nur an den Ticks einrasten soll
     *
     * @param b true wenn Slider nur an den Ticks einrasten soll
     */
    public void setSnapToTicks(Boolean b) {
        if (list.getFirst() instanceof JSlider) {
            JSlider slider = (JSlider) list.getFirst();
            slider.setSnapToTicks(b);
        }
    }

    /**
     * Fügt einen Änderungsereignisempfänger hinzu
     *
     * @param listener der hinzuzufügende Empfänger
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listenerList.add(PropertyChangeListener.class, listener);
    }

    /**
     * Entfernt einen Änderungsereignisempfänger
     *
     * @param listener der zu entfernende Empfänger
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listenerList.remove(PropertyChangeListener.class, listener);
    }

    private void run() {
        //Componenten (Schalter, Schieberegler, etc.) aus der Liste einfügen
        JPanel box = new JPanel();
        box.setLayout(new javax.swing.BoxLayout(box, javax.swing.BoxLayout.Y_AXIS));
        JLabel leer = new JLabel(" ");
        box.add(leer); //Vergrößert den Abstand nach oben
        for (JComponent e : list) {
            box.add(e);
        }
        frame.add(box, BorderLayout.CENTER);

        final JLabel label = new JLabel(this.labelText);
        frame.add(label, BorderLayout.NORTH);

        final JButton okButton = new JButton("Anwenden");
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                frame.setEnabled(false);
                firePropertyChangeEvent(new PropertyChangeEvent(this,
                        titel, standardWert, getValue()));
            }
        });
        frame.add(okButton, BorderLayout.SOUTH);
        if (bild != null) {
            addPreviewPicture();
        }

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setAlwaysOnTop(true);
        addListeners(list);

        //Nebenläufigkeit des Fensters erzeugen
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                frame.setVisible(true);
                while (frame.isVisible()) {
                    try {
                        Thread.sleep(100);
                        if (!frame.isVisible()) {
                            frame.dispose();
                        }
                    } catch (InterruptedException e) {
                    }
                }
            }
        });
        t.start(); //Macht neuen Thread auf
        if (filter != null) {
            filter.ausfuehren(kopie, getValue());
        }
        if (bild != null) {
            vorschauFlaeche.repaint();
        }
    }

    /**
     * Erzeugt ein verkleinertes Vorschaubild und fügt es im Frame ein
     *
     * @param bild Das anzuzeigende Bild
     */
    private void addPreviewPicture() {
        vorschauFlaeche = new Bildflaeche();
        frame.add(vorschauFlaeche, BorderLayout.EAST);
        int gr = 200;
        int width = bild.getWidth();
        int height = bild.getHeight();
        if (bild.getWidth() > gr || bild.getHeight() > gr) {
            //Berechnung der Größe für Vorschaubild
            if (bild.getWidth() > bild.getHeight()) {
                width = gr;
                float faktor = (float) bild.getHeight() / (float) bild.getWidth();
                height = (int) (width * faktor);
            } else {
                height = gr;
                float faktor = (float) bild.getWidth() / (float) bild.getHeight();
                width = (int) (height * faktor);
            }
        }
        //Bild auf Vorschaufläche skalieren
        Image scaledImage = bild.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        vorschauBild = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = vorschauBild.getGraphics();
        g.drawImage(scaledImage, 0, 0, null);
        g.dispose();
        //Originalkopie aufheben, damit Vorschaubild immer vom Original aus
        //erzeugt werden
        kopie = new Farbbild(vorschauBild);
        vorschauFlaeche.setzeBild(kopie);
    }

    /**
     * fügt zu Komponenten Änderungs-Listener zu sorgt dafür, dass Änderungen am
     * Slider in der Vorschau angezeigt werden und dass Änderungen der
     * Parameterbedienelemente durchgeführt werden
     *
     * Sollte es ein veränderliches Element geben, dass eine Aktualisierung des
     * Vorschaubildes bewirken soll und ist dieses nicht ein AbstractButton oder
     * JSlider (es kann auch eine Ableitung davon sein), so muss diese Klasse
     * zusätzlich implementiert werden nach dem unten stehendem Schema.
     *
     */
    private void addListeners(LinkedList<JComponent> list) {
        for (JComponent e : list) {
            if (e instanceof AbstractButton) {
                AbstractButton element = (AbstractButton) e;
                element.addChangeListener(new ChangeListener() {

                    @Override
                    public void stateChanged(ChangeEvent e) {

                        changePreview();
                    }
                });
            }
            if (e instanceof JSlider) {
                JSlider element = (JSlider) e;
                element.addChangeListener(new ChangeListener() {

                    @Override
                    public void stateChanged(ChangeEvent e) {
                        changePreview();
                    }
                });
            }
            if (e instanceof JPanel) {
                JPanel element = (JPanel) e;
                LinkedList<JComponent> listJPanel = new LinkedList<>();
                for (int i = 0; i < element.getComponentCount(); i++) {
                    listJPanel.add((JComponent) element.getComponent(i));
                }
                addListeners(listJPanel);
            }
        }
    }

    //aktualisiert die Vorschau
    private void changePreview() {
        if (kopie != null && filter != null) {
            kopie = new Farbbild(vorschauBild);
            vorschauFlaeche.setzeBild(kopie);
            filter.ausfuehren(kopie, getValue());
            vorschauFlaeche.repaint();
        }
    }

    /**
     * Wenn das erste Element der list ein JSlider ist, wird der aktuelle Wert
     * dessen zurückgegeben. Anderenfalls wird der letzte Wert von value
     * zurückgegeben.
     *
     * @return Value
     */
    private int getValue() {
        int localValue;
        if (list.getFirst() instanceof JSlider) {
            JSlider slider = (JSlider) list.getFirst();
            localValue = slider.getValue();
        } else {
            localValue = this.value;
        }
        return localValue;
    }

    /**
     * informiert die Listener über eine Änderung eines Wertes
     *
     * @param propertyChangeEvent
     */
    private void firePropertyChangeEvent(PropertyChangeEvent propertyChangeEvent) {
        EventListener[] listeners = listenerList.getListeners(PropertyChangeListener.class);
        for (EventListener l : listeners) {
            ((PropertyChangeListener) l).propertyChange(propertyChangeEvent);
        }
    }
}
