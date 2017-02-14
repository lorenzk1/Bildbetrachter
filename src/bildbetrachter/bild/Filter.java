package bildbetrachter.bild;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 * abstrakte Oberklasse für die Bildfilter
 *
 * @author eih
 */
public abstract class Filter extends JComponent {

    private final String name;
    private final PropertyChangeSupport change;
    private KeyStroke key;

    public Filter(String name) {
        change = new PropertyChangeSupport(this);
        this.name = name;
    }

    /**
     * liefert kurzen Namen des Filters für Menü z. B.
     *
     * @return Name des Filters
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * liefert einen KeyStroke, der für die Erstellung eines Shortcuts genutz
     * wereden kann.
     *
     * @return Key des Filters
     */
    public KeyStroke getKey() {
        return key;
    }

    /**
     * 
     * @param key 
     */
    public void setKey(KeyStroke key) {
        this.key = key;
    }

    /**
     * Öffnet einen Benutzerdialog, indem Änderungen eingestellt werden können
     *
     * @param bild Das zu veränderende Farbbild
     */
    public abstract void anwenden(Farbbild bild);

    /**
     * wendet den Filter an
     *
     * @param kopie das Bild als Kopie
     * @param value Wert, um den das Bild geändert werden soll.
     */
    public abstract void ausfuehren(Farbbild kopie, int value);

    public void anzeigeAktualisieren() {
        change.firePropertyChange("Bild geändert.", 0, 1);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        change.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        change.removePropertyChangeListener(l);
    }
}
