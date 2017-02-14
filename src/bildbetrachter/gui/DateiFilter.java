package bildbetrachter.gui;

import java.io.File;
import java.util.ArrayList;
import javax.swing.filechooser.FileFilter;

/**
 * Dieser Dateifilter vergleicht alle Dateien mit einem
 * gegebenen Satz von Erweiterungen.
 */
public final class DateiFilter extends FileFilter {
    private String beschreibung;
    private ArrayList<String> erweiterungen;
    
    public DateiFilter() {
        this.beschreibung = "";
        this.erweiterungen = new ArrayList<>();
    }
    
    /**
     * Initialisiert den Filter direkt mit einer Beschreibung und den
     * Dateinamenserweiterungen.
     * @param beschreibung Dateifilterbeschreibung
     * @param erweiterung Dateinamenserweiterung
     */
    public DateiFilter(String beschreibung, String erweiterung[]) {
        beschreibung += " (";
        this.erweiterungen = new ArrayList<>();
        for (String s : erweiterung) {
            beschreibung += "*." + s;
            this.addExtension(s);
            if (!s.equals(erweiterung[erweiterung.length - 1]))
                beschreibung += "; "; 
        }
        this.beschreibung = beschreibung + ")";
    }
    
    /**
     *  Hinzufügen einer Erweiterung, die dieser Filter erkennt.
     *  @param erweiterung eine Dateierweiterung (z.B. ".txt" oder "txt")
     */
    public void addExtension(String erweiterung) {
        if (!erweiterung.startsWith(".")) {
            erweiterung = "." + erweiterung;
        }
        erweiterungen.add(erweiterung.toLowerCase());
    }

    /**
     *  Festlegen einer Beschreibung für die Dateigruppe, die
     *  dieser Dateifilter erkennt.
     *  @param beschreibung eine Beschreibung für die Dateigruppe
     */
    public void setDescription(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    /**
     *  Liefert eine Beschreibung für die Dateigruppe, die dieser
     *  Dateifilter erkennt.
     *  @return eine Beschreibung für die Dateigruppe
     */
    @Override
    public String getDescription() {
        return beschreibung;
    }

    @Override
    public boolean accept(File datei) {
        if (datei.isDirectory()) {
            return true;
        }
        String name = datei.getName().toLowerCase();

        // Prüfen, ob der Dateiname mit einer der Erweiterungen endet
        for (String erweiterung : erweiterungen) {
            if (name.endsWith(erweiterung)) {
                return true;
            }
        }
        return false;
    }
}

