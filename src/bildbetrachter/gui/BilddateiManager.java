package bildbetrachter.gui;

import bildbetrachter.bild.Farbbild;
import java.awt.image.*;
import java.io.*;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import javax.imageio.*;
import javax.imageio.plugins.bmp.BMPImageWriteParam;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.swing.*;

/**
 * BilddateiManager ist eine kleine Hilfsklasse mit statischen Methoden zum
 * Laden und Speichern von Bildern. Sie zeigt ein Vorschaubild im
 * Dateiauswahldialog an.
 *
 * Zu lesende Dateien können im JPG- , GIF- oder im PNG-Format vorliegen. Das
 * Format von Dateien, die von dieser Klasse geschrieben werden, wird durch die
 * Konstante BILDFORMATE festgelegt.
 *
 * @author eih
 * @author lweikopf
 * @version 2.1
 */
public class BilddateiManager {

    // Konstanten, die die gültigen Dateiendungen für geladene und gespeicherte
    // Dateien festgelegen. Der erste Name eines inneren Arrays steht immer für
    // die formale Dateiendung und das Format der geschriebenen Dateien.
    private static final String BILDFORMATE[][] = {{"jpg", "jpeg", "jpe", "jfif"}, {"png"}, {"bmp", "dib"}};
    private static final DateiFilter[] FILTER = {new DateiFilter("JPEG", BILDFORMATE[0]),
        new DateiFilter("PNG", BILDFORMATE[1]), new DateiFilter("BMP", BILDFORMATE[2])};

    // Attribut zum Auswählen von Dateien, Start im Heimatverzeichnis des Anwenders
    private static JFileChooser dateiauswahldialog = new JFileChooser(System.getProperty("%userdir%"));
    private static JFileChooser ordnerauswahldialog = new JFileChooser(System.getProperty("%userdir%"));

    /**
     * Öffne einen Dateiauswahldialog und lasse den Benutzer eine Bilddatei aus
     * dem Dateisystem auswählen. Lade dann dieses Bild und liefere es als ein
     * Farbbild zurück. Diese Methode kann JPG- und GIF-Formate lesen. Bei einem
     * Problem (Datei existiert nicht, hat das falsche Format oder es gibt einen
     * anderen Lesefehler) liefert diese Methode null.
     *
     * @param fenster Das Quellfenster, uas der Dialog gestartet wird. Kann auch
     * null sein.
     * @return Das Bild-Objekt oder null, falls keine gültige Bilddatei
     * selektiert wurde.
     */
    public static Farbbild gibBild(JFrame fenster) {
        dateiauswahldialog.resetChoosableFileFilters();
        dateiauswahldialog.setMultiSelectionEnabled(false);
        // Alle Bilddateien mit registrierten Erweiterungen akzeptieren (s.o.)
        dateiauswahldialog.resetChoosableFileFilters();
        dateiauswahldialog.setAcceptAllFileFilterUsed(false);
        

        final DateiFilter filterAll = new DateiFilter();
        filterAll.setDescription("Alle Bilddateien");
        for (String s[] : BILDFORMATE) {
            for (String t : s) {
                filterAll.addExtension(t);
            }
        }

        //Dateiauswahldialoge anzeigen
        dateiauswahldialog.addChoosableFileFilter(filterAll);
        for (DateiFilter filter : FILTER) {
            dateiauswahldialog.addChoosableFileFilter(filter);
        }

        int ergebnis = dateiauswahldialog.showOpenDialog(fenster);

        if (ergebnis != JFileChooser.APPROVE_OPTION) {
            return null;  // abgebrochen
        }

        return ladeBild(dateiauswahldialog.getSelectedFile());
    }

    /**
     * Liest eine Bilddatei ein und liefere sie als ein Bild zurück. Diese
     * Methode kann Dateien im JPG- und im GIF-Format lesen. Bei Problemen
     * (etwa, wenn die Datei nicht existiert oder ein nicht lesbares Format hat
     * oder es einen sonstigen Lesefehler gibt) liefert diese Methode null.
     *
     * @param bilddatei Die zu ladende Bilddatei.
     * @return Das Bild-Objekt oder null, falls die Datei nicht lesbar ist.
     */
    public static Farbbild ladeBild(File bilddatei) {
        try {
            BufferedImage bild = ImageIO.read(bilddatei);
            if (bild == null || (bild.getWidth(null) < 0)) {
                // Bild konnte nicht geladen werden - vermutlich falsches Format
                return null;
            }
            Farbbild farbbild = new Farbbild(bild);
            farbbild.setDateiname(bilddatei.getPath());
            // Dateiname wird beibehalten
            farbbild.setDateityp(bilddatei.getName()
                    .substring(bilddatei.getName().lastIndexOf('.') + 1).toLowerCase());
            return farbbild;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Schreibe das gegebene Bild in eine Bilddatei im JPG-Format. Die
     * Speicherqualität kann gewählt werden. Bei etwaigen Problemen beendet sich
     * diese Methode stillschweigend.
     *
     * @param bild Das zu speichernde Bild.
     * @param fenster Das Quellfenster, wodurch der Dialog gestartet wird. Kann
     * auch null sein.
     * @return 0: ohne Fehler ausgeführt; 1: durch Benutzer abgebrochen; 2:
     * Keine Schreibrechte im Zielpfad, 3: IO-Exception/Fehler
     */
    public static int speichereBild(Farbbild bild, JFrame fenster) {
        File dateiName;

        dateiauswahldialog.resetChoosableFileFilters();
        dateiauswahldialog.setAcceptAllFileFilterUsed(false);

        for (int i = 0; i < BILDFORMATE.length; i++) {
            if (bild.getDateityp().equals(BILDFORMATE[i][0])) {
                dateiauswahldialog.setFileFilter(FILTER[i]);
            }
        }

        for (DateiFilter filter : FILTER) {
            dateiauswahldialog.addChoosableFileFilter(filter);
        }

        do {
            int ergebnis = dateiauswahldialog.showSaveDialog(null);
            if (ergebnis != JFileChooser.APPROVE_OPTION) {
                return 1;  // abgebrochen
            }
            dateiName = dateiauswahldialog.getSelectedFile();
            if (!dateiauswahldialog.accept(dateiName)) {
                JOptionPane.showMessageDialog(null,
                        "Ungültige Dateinamenserweiterung für den gewählten Dateitypen.",
                        "Bilddatei speichern", JOptionPane.ERROR_MESSAGE);
            }
        } while (!dateiauswahldialog.accept(dateiName));

        if (dateiauswahldialog.getFileFilter() == FILTER[0]) {
            return speichereJPG(dateiName, bild);
        } else if (dateiauswahldialog.getFileFilter() == FILTER[1]) {
            return speicherePNG(dateiName, bild);
        } else if (dateiauswahldialog.getFileFilter() == FILTER[2]) {
            return speichereBMP(dateiName, bild);
        }
        return 3;
    }

    /**
     * Funktion, zum expliziten Speichern von Bilddateien im JPEG-Format.
     *
     * @param dateiName Der zur Datei gehörige Dateiname.
     * @param bild Das zu speichernde Bild.
     * @return 0: ohne Fehler ausgeführt; 2: Keine Schreibrechte im Zielpfad, 3:
     * IO-Exception
     */
    private static int speichereJPG(File dateiName, Farbbild bild) {
        try {
            Iterator iterator = ImageIO.getImageWritersBySuffix("jpg");
            ImageWriter imageWriter = (ImageWriter) iterator.next();
            JPEGImageWriteParam imageWriteParam = new JPEGImageWriteParam(Locale.getDefault());
            imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

            // Standard-Modus für die Kompression der Bilddatei
            imageWriteParam.setCompressionQuality(ImageWriteParam.MODE_DEFAULT);
//					float compression = 0.75f;
//          imageWriteParam.setCompressionQuality(compression);
            IIOImage iioImage = new IIOImage(bild, null, null);
            File f = new File(dateiName.getPath());
            java.nio.file.FileSystem local = FileSystems.getDefault();
            Path targetPath = local.getPath(f.getParent());
            if (Files.isWritable(targetPath)) {
                imageWriter.setOutput(ImageIO.createImageOutputStream(f));
                imageWriter.write(null, iioImage, imageWriteParam);
                bild.setDateiname(dateiName.getPath());
                return 0;
            } else {
                System.err.println(f.getAbsolutePath() + ": Zugriff verweigert.");
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException e) {
            return 2;
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
            return 3;
        }
    }

    /**
     * Funktion, zum expliziten Speichern von Bilddateien im PNG-Format.
     *
     * @param dateiName Der zur Datei gehörige Dateiname.
     * @param bild Das zu speichernde Bild.
     * @return 0: ohne Fehler ausgeführt; 2: Keine Schreibrechte im Zielpfad, 3:
     * IO-Exception
     */
    private static int speicherePNG(File dateiName, Farbbild bild) {
        try {
            Iterator iterator = ImageIO.getImageWritersBySuffix("png");
            ImageWriter imageWriter = (ImageWriter) iterator.next();
            IIOImage iioImage = new IIOImage(bild, null, null);
            File f = new File(dateiName.getPath());
            java.nio.file.FileSystem local = FileSystems.getDefault();
            Path targetPath = local.getPath(f.getParent());
            if (Files.isWritable(targetPath)) {
                imageWriter.setOutput(ImageIO.createImageOutputStream(f));
                imageWriter.write(null, iioImage, null);
                bild.setDateiname(dateiName.getPath());
                return 0;
            } else {
                System.err.println(f.getAbsolutePath() + ": Zugriff verweigert.");
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException e) {
            return 2;
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
            return 3;
        }
    }

    /**
     * Funktion, zum expliziten Speichern von Bilddateien im bitmap Format.
     *
     * @param dateiName Der zur Datei gehörige Dateiname.
     * @param bild Das zu speichernde Bild.
     * @return 0: ohne Fehler ausgeführt; 2: Keine Schreibrechte im Zielpfad, 3:
     * IO-Exception
     */
    private static int speichereBMP(File dateiName, Farbbild bild) {
        try {
            Iterator iterator = ImageIO.getImageWritersBySuffix("bmp");
            ImageWriter imageWriter = (ImageWriter) iterator.next();
            BMPImageWriteParam imageWriteParam = new BMPImageWriteParam(Locale.getDefault());

            // Standard-Modus für die Kompression der Bilddatei
            imageWriteParam.setCompressionMode(ImageWriteParam.MODE_DEFAULT);
            IIOImage iioImage = new IIOImage(bild, null, null);
            File f = new File(dateiName.getPath());
            java.nio.file.FileSystem local = FileSystems.getDefault();
            Path targetPath = local.getPath(f.getParent());
            if (Files.isWritable(targetPath)) {
                imageWriter.setOutput(ImageIO.createImageOutputStream(f));
                imageWriter.write(null, iioImage, imageWriteParam);
                bild.setDateiname(dateiName.getPath());
                return 0;
            } else {
                System.err.println(f.getAbsolutePath() + ": Zugriff verweigert.");
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException e) {
            return 2;
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
            return 3;
        }
    }

    /**
     * Öffne einen Ordnerauswahldialog und lasse den Benutzer einen Ordner aus
     * dem Dateisystem auswählen. Lies dann dessen Dateien aus und liefere deren
     * Pfade als Liste zurück.
     *
     * @param fenster Das Quellfenster, uas der Dialog gestartet wird. Kann auch
     * null sein.
     * @return Die Liste mit den absolute Pfaden der Bilddateien als String
     */
    public static LinkedList diashowOrdnerWählen(JFrame fenster) {
        LinkedList<String> liste;
        liste = new LinkedList<>();
        ordnerauswahldialog.setMultiSelectionEnabled(false);
        ordnerauswahldialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //Ordnerauswahldialog anzeigen
        int ergebnis = ordnerauswahldialog.showOpenDialog(fenster);

        if (ergebnis != JFileChooser.APPROVE_OPTION) {
            return liste;  // abgebrochen, leere Liste wird zurückgegeben
        }
        File selektierterOrdner = ordnerauswahldialog.getSelectedFile();
        //filter, der die endung überprüft
        final FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File f) {
                String name = f.getName();
                //prüfen auf gültige Endungen
                for (String s[] : BILDFORMATE) {
                    for (String endung : s) {
                        if (name.endsWith(endung)) {
                            return true;
                        }
                    }
                }
                return false;
            }
        };
        for (File f : selektierterOrdner.listFiles(filter)) {
            liste.add(f.getAbsolutePath());
        }
        return liste;
    }

    /**
     * Öffne zwecks Anzeige im Diashowmodus eine Datei aus einem angegebenen
     * Pfad. Lade dann dieses Bild und liefere es als ein Farbbild zurück. Diese
     * Methode kann JPG- ,PNG- und GIF-Formate lesen. Bei einem Problem (Datei
     * existiert nicht, hat das falsche Format oder es gibt einen anderen
     * Lesefehler) liefert diese Methode null.
     *
     * @param pfad Der Ordner, aus dem Bilder geladen werden.
     * @return Das Bild-Objekt oder null, falls keine gültige Bilddatei
     * selektiert wurde.
     */
    public static Farbbild gibDiashowBild(String pfad) {
        File file = new File(pfad);
        return ladeBild(file);
    }

    public static Farbbild gibBildFromURL(URL url) {
        File file = new File(url.getPath());
        return ladeBild(file);
    }
}
