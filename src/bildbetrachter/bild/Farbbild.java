package bildbetrachter.bild;

import java.awt.Color;
import java.awt.datatransfer.*;
import java.awt.image.*;
import java.io.IOException;

/**
 * Farbbild ist eine Klasse, die Farbbilder mit einer bequemen
 * Schnittstelle definiert.
 * Die Farbbilder fungieren dabei auch als Transferobjekte, um z.B. in das Systemclipboard
 * kopiert werden zu können.
 * @version 1.1
 */
public class Farbbild extends BufferedImage implements Transferable {
    
    private String dateiname;
    private String dateityp;

    /**
     * Erzeuge ein Farbbild als Kopie von einem BufferedImage.
     * @param image das zu kopierende BufferedImage.
     */
    public Farbbild(BufferedImage image) {
        super(image.getColorModel(), image.copyData(null),
                image.isAlphaPremultiplied(), null);
        dateiname = "";
        dateityp = "";
    }

    /**
     * Erzeuge ein Farbbild mit der angegebenen Größe mit
     * undefiniertem Inhalt.
     * @param breite die Breite des Bildes.
     * @param hoehe die Hoehe des Bildes.
     */
    public Farbbild(int breite, int hoehe) {
        super(breite, hoehe, TYPE_INT_RGB);
        dateiname = "";
        dateityp = "";
    }

    /**
     * Setze den angegebenen Bildpunkt dieses Bildes auf die
     * angegebene Farbe.
     * @param x die x-Koordinate des Bildpunktes.
     * @param y die y-Koordinate des Bildpunktes.
     * @param col die Farbe des Bildpunktes.
     */
    public void setzePunktfarbe(int x, int y, Color col) {
        int punktfarbe = col.getRGB();
        setRGB(x, y, punktfarbe);
    }

    /**
     * Liefere die Farbe des angegebenen Bildpunktes.
     * @param x die x-Koordinate des Bildpunktes.
     * @param y die y-Koordinate des Bildpunktes.
     * @return die Farbe des Bildpunktes an der angegebenen Position.
     */
    public Color gibPunktfarbe(int x, int y) {
        int punktfarbe = getRGB(x, y);
        return new Color(punktfarbe);
    }
    
    /**
     * Gibt die Metadaten dieses Objektes für den Transfer in das Clipboard wieder.
     * @return DataFlavor Bild-Metadaten
     */
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {DataFlavor.imageFlavor};
    }

    /**
     * Gibt zurück, ob die als Parameter angegebenen Metadaten mit denen von diesem
     * Objekt definierten kompatibel sind.
     * @param flavor vergleichs-metadaten
     * @return boolean flavor kompatibel oder nicht
     */
    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return DataFlavor.imageFlavor.equals(flavor);
    }

    /**
     * Gibt ein neues Objekt zurück, dass die zu transferierenden Daten enthält.
     * @param flavor gewünschte Metadaten des Objektes
     * @return Objekt mit Transferdaten.
     * @throws UnsupportedFlavorException
     * @throws IOException 
     */
    @Override
    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException {
        if (!DataFlavor.imageFlavor.equals(flavor)) {
            throw new UnsupportedFlavorException(flavor);
        }
        return (BufferedImage) this;
    }

    /**
     * Liefert den Dateinamen des Bildobjekts.
     * @return dateiname
     */
    public String getDateiname() {
        return dateiname;
    }

    /**
     * Legt den Dateinamen für das Bildobjekt fest.
     * @param dateiname
     */
    public void setDateiname(String dateiname) {
        this.dateiname = dateiname;
    }

    /**
     * Liefert den Dateityp des Bildes
     * @return dateityp
     */
    public String getDateityp() {
        return dateityp;
    }

    /**
     * Legt den Dateityp für das Bildobjekt fest.
     * @param dateityp
     */
    public void setDateityp(String dateityp) {
        this.dateityp = dateityp;
    }

    /**
     * Erzeugt eine Kopie des Objektes 
     * @return Kopie des Farbbild Objektes
     */
    @Override
    public Object clone() {
        Farbbild copy = new Farbbild(this.getWidth(), this.getHeight());
        copy.dateiname = this.dateiname;
        copy.dateityp = this.dateityp;
        for (int i = 0; i < this.getWidth(); i++) {
            for (int j = 0; j < this.getHeight(); j++) {
                copy.setRGB(i, j, this.getRGB(i, j));
            }
        }
        return copy;
    }

    /**
     * Überprüft, ob zwei Farbbilder die gleichen Bildinformationen besitzen.
     * @param obj Ein Vergleichsobjekt; sollte vom Typ Farbbild sein.
     * @return wahr wenn der Inhalt der Farbbild Objekte gleich ist. Wenn andere Datentypen
     *         als Parameter übergeben werden oder die Farbbilder unterschiedliche
     *         Bildinformationen enthalten, wird unwahr zurückgegeben.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Farbbild)) {
            return false;
        }
        
        if (obj == this) {
            return true;
        }
        
        if (this.getWidth() != ((Farbbild) obj).getWidth()
                || this.getHeight() != ((Farbbild) obj).getHeight()
                || !this.dateiname.equals(((Farbbild) obj).getDateiname())
                || !this.dateityp.equals(((Farbbild) obj).getDateityp())) {
            return false;
        }
        
        for (int i = 0; i < this.getWidth(); i++) {
            for (int j = 0; j < this.getHeight(); j++) {
                if (this.getRGB(i, j) != ((Farbbild) obj).getRGB(i, j)) {
                    return false;   
                }
            }
        }
        return true;
    }
}
