package bildbetrachter.gui;

import bildbetrachter.bild.*;
import bildbetrachter.help.Hilfe;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;

/**
 * Bildbetrachter ist die Hauptklasse der Bildbetrachter-Anwendung. Sie erstellt
 * die GUI der Anwendung, zeigt sie an und initialisiert alle anderen
 * Komponenten.
 *
 * @version 0.9
 */
public class Bildbetrachter {

    private JFrame fenster;
    private Bildflaeche bildflaeche;
    private JScrollPane scrollPane;
    private JLabel dateinameLabel;
    private JLabel statusLabel;
    private Farbbild bild;
    private int lastSavedIndex;
    private JMenuItem menuItemSpeichern;
    private JMenuItem menuItemSchliessen;
    private JMenu filterMenu, bearbeitenMenu;
    private JPopupMenu popupMenu;
    private JButton speichernButton, rueckgaengigButton, vorwaertsButton;
    private JButton drehRButton, drehLButton, zoomButton;
    private JToggleButton textButton;
    private JButton vorheriges;
    private JButton nachfolgendes;
    private ArrayList<Filter> filterListe;
    private Hilfe hilfe;
    private static final int SHORTCUT_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    private static final String VERSION = "Version 0.9";

    /**
     * Main-Methode zum Start
     *
     * @param args
     */
    public static void main(String[] args) {
        new Bildbetrachter().fensterErzeugen();
    }

    /**
     * Erzeuge das Swing-Fenster samt Inhalt.
     */
    private void fensterErzeugen() {
        fenster = new JFrame("Bildbetrachter");
        menuezeileErzeugen();
        Container contentPane = fenster.getContentPane();
        // DO_NOTHING_ON_CLOSE, da das Terminieren des Programmes und daher
        // das Schließen des Fensters von der Funktion beenden() übernommen wird.
        fenster.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        // der besseren Lesbarkeit angegeben, BorderLayout ist Standard
        contentPane.setLayout(new BorderLayout());

        dateinameLabel = new JLabel();
        dateinameAnzeigen(null);
        contentPane.add(dateinameLabel, BorderLayout.NORTH);
        bildflaeche = new Bildflaeche();
        bildflaeche.setBorder(new EtchedBorder());
        scrollPane = new JScrollPane();
        scrollPane.setViewportView(bildflaeche);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        JPanel south = new JPanel();
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        statusLabel = new JLabel("kein Bild geladen");
        south.add(statusLabel);
        contentPane.add(south, BorderLayout.SOUTH);
        buttonsSchalten(false);

        // Aufbau abgeschlossen - Komponenten arrangieren lassen
        fenster.pack();
        fenstergroesseAnpassen();
        // Symbol des Fensters festlegen
        BufferedImage image = null;
        try {
            image = ImageIO.read(Bildbetrachter.class
                    .getResource("/bildbetrachter/images/icon2.gif"));
        } catch (IllegalArgumentException | IOException e) {
            System.out.println(e.toString());
        }
        if (image != null) {
            fenster.setIconImage(image);
        }
        //sorgt für sauberes Beenden beim Klick auf das X
        fenster.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                beenden();
            }
        });
        fenster.setVisible(true);
    }

    /**
     * befüllt die Liste, mit allen implementierten Filtern.
     */
    private void filterlisteErzeugen() {
        filterListe = new ArrayList<>();
        filterListe.add(new RGBFilter("Farbfilter"));
        filterListe.add(new HelligkeitsFilter("Helligkeit"));
//        filterListe.add(new FischaugenFilter("Fischaugenfilter"));
//        filterListe.add(new Graustufenfilter("Graustufenfilter"));
//        filterListe.add(new GrobrasterFilter("Grobraster"));
//        filterListe.add(new KantenerkennungsFilter("Kanten"));
//        filterListe.add(new NegativFilter("Negativfilter"));
//        filterListe.add(new PuzzleFilter("Puzzlefilter"));
//        filterListe.add(new SchwellenwertFilter("Schwellenwert"));
//        filterListe.add(new Spiegelfilter("Spiegelfilter"));
//        filterListe.add(new SolarisationsFilter("Solarisationsfilter"));
//        filterListe.add(new WeichzeichnerFilter("Weichzeichner"));
    }

    /**
     * Die Menüzeile des Hauptfensters erzeugen.
     *
     * @param fenster Das Fenster, in das die Menüzeile eingefügt werden soll.
     */
    private void menuezeileErzeugen() {
        JMenuBar menuezeile = new JMenuBar();
        fenster.setJMenuBar(menuezeile);

        // Das Datei-Menü erzeugen
        JMenu dateiMenu = new JMenu("Datei");
        menuezeile.add(dateiMenu);
        MenuHelper.createMenuItem("Öffnen", KeyStroke.getKeyStroke(KeyEvent.VK_O, SHORTCUT_MASK), dateiMenu, new Runnable() {
            public void run() {
                dateiOeffnen();
                verlaufSchalten();
            }
        });
        menuItemSpeichern = MenuHelper.createMenuItem("Speichern", KeyStroke.getKeyStroke(KeyEvent.VK_S, SHORTCUT_MASK), dateiMenu, new Runnable() {
            public void run() {
                dateiSpeichern();
            }
        });
        menuItemSchliessen = MenuHelper.createMenuItem("Schließen", null, dateiMenu, new Runnable() {
            public void run() {
                if (dateiSchliessen()) {
                    verlaufSchalten();
                }
            }
        });
        dateiMenu.addSeparator();
        MenuHelper.createMenuItem("Beenden", KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK), dateiMenu, new Runnable() {
            public void run() {
                beenden();
            }
        });

        //Das Bearbeiten-Menü
        bearbeitenMenu = new JMenu("Bearbeiten");
        menuezeile.add(bearbeitenMenu);
        MenuHelper.createMenuItem("Zoom", null, bearbeitenMenu, new Runnable() {
            public void run() {
//                zoomen();
            }
        });

        // Das Filter-Menü
        filterlisteErzeugen();
        filterMenu = new JMenu("Filter");
        menuezeile.add(filterMenu);
        JMenuItem item;
        for (final Filter f : filterListe) {
            item = new JMenuItem(f.getName());
            item.setAccelerator(f.getKey());
            item.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    f.addPropertyChangeListener(new PropertyChangeListener() {

                        @Override
                        public void propertyChange(PropertyChangeEvent evt) {
                            statusAnzeigen(f.getName() + " angewendet.");
                            bildflaeche.setzeBild(bild);
                            verlaufSchalten();

                        }
                    });
                    f.anwenden(bild);
                    verlaufSchalten();
                    //System.out.println("Bild Filter hinzugefügt");
                }
            });
            filterMenu.add(item);
        }

        // Das Hilfe-Menü
        JMenu hilfeMenu = new JMenu("Hilfe");
        menuezeile.add(hilfeMenu);
        hilfe = new Hilfe();
        MenuHelper.createMenuItem("Hilfe", KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), hilfeMenu, new Runnable() {
            public void run() {
                hilfe.zeigeHilfe();
            }
        });
        MenuHelper.createMenuItem("Info", KeyStroke.getKeyStroke(KeyEvent.VK_F1, KeyEvent.SHIFT_MASK), hilfeMenu, new Runnable() {
            public void run() {
                hilfe.zeigeInfo(fenster, VERSION);
            }
        });
    }

    /**
     * Methode sorgt dafür, dass Fenster bei Überschreiten der Bildschirmgröße
     * maximal so groß wie Bildschirm wird. Obere linke Ecke liegt bei Pos. 0 0,
     * Berücksichtigung der Windows-Taskleiste (Höhe 34 px) Wird nach allen
     * Operationen aufgerufen, die die Fenstergröße beeinflussen.
     */
    private void fenstergroesseAnpassen() {
        fenster.pack();
        // Bildschirmabmessungen holen
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int bildschirmBreite = screenSize.width;
        int bildschirmHoehe = screenSize.height - 34; // Windows-Taskleiste

        if ((fenster.getWidth() > bildschirmBreite)
                || (fenster.getHeight() > bildschirmHoehe)) {
            fenster.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        fenster.repaint();
    }

    /**
     * 'Datei oeffnen'-Funktion: Öffnet einen Dateiauswahldialog zur Auswahl
     * einer Bilddatei und zeigt das selektierte Bild an.
     */
    private void dateiOeffnen() {
        Farbbild tempBild = BilddateiManager.gibBild(fenster);
        if (tempBild != null) {
            bild = tempBild;
            bildflaeche.setzeBild(bild);
            fenstergroesseAnpassen();
            buttonsSchalten(true);
            fenstergroesseAnpassen();
            dateinameAnzeigen(bild.getDateiname());
            statusAnzeigen("Datei geladen.");
            verlaufSchalten();
            lastSavedIndex = 0;
        } else {
            if (bild != null) {
                dateinameAnzeigen(bild.getDateiname());
            }
            statusAnzeigen("Öffnen abgebrochen oder gescheitert.");
        }
    }

    /**
     * 'Datei speichern'-Funktion
     */
    private int dateiSpeichern() {
        int retval = BilddateiManager.speichereBild(bild, fenster);
        switch (retval) {
            case 0:
                dateinameAnzeigen(bild.getDateiname());
                statusAnzeigen("Datei gespeichert.");
                break;
            case 1:
                statusAnzeigen("Speichern abgebrochen.");
                break;
            case 2:
                statusAnzeigen("Zielordner nicht beschreibbar.");
                break;
            case 3:
                statusAnzeigen("Fehler beim Speichern.");
                break;
            default:
                break;
        }
        return retval;
    }

    /**
     * 'Datei schließen'-Funktion Verwaltet zusätzlich den 'Bild noch nicht
     * gespeichert' schließen-Dialog.
     */
    private boolean dateiSchliessen() {
        if (!saveCloseDialog()) {
            return false;
        }
        bild = null;
        bildflaeche.loeschen();
        dateinameAnzeigen(null);
        statusAnzeigen("Bild geschlossen.");
        buttonsSchalten(false);
        fenstergroesseAnpassen();
        return true;
    }

    /**
     * Funktion steuert den internen Speichern-Schließen-Dialog.
     *
     * @return wahr, wenn eine Option erfolgreich durchgeführt wurde, ansonsten
     * unwahr.
     */
    private boolean saveCloseDialog() {
        //TODO
        return true;
    }

    /**
     * 'Beenden'-Funktion: Beendet die Anwendung.
     */
    private void beenden() {
        if (bild == null || (bild != null && saveCloseDialog())) {
            System.exit(0);
        }
    }

    /**
     * Zeigt den Dateinamen des aktuellen Bildes auf dem Label für den
     * Dateinamen. Der Parameter sollte 'null' sein, wenn kein Bild geladen ist.
     *
     * @param dateiname Der anzuzeigende Dateiname, oder null für 'keine Datei'.
     */
    private void dateinameAnzeigen(String dateiname) {
        if (dateiname == null) {
            dateinameLabel.setText("Keine Datei angezeigt.");
        } else {
            dateinameLabel.setText("Datei: " + dateiname);
        }
    }

    /**
     * Zeige den gegebenen Text in der Statuszeile am unteren Rand des Fensters.
     *
     * @param text der anzuzeigende Statustext.
     */
    private void statusAnzeigen(String text) {
        statusLabel.setText(text);
    }

    /**
     * schaltet Buttons und Menüeiträge für Bildmanipulation.
     */
    private void buttonsSchalten(boolean status) {
        menuItemSpeichern.setEnabled(status);
        menuItemSchliessen.setEnabled(status);
        bearbeitenMenu.setEnabled(status);
        verlaufSchalten();
        filterMenu.setEnabled(status);
        bildflaeche.setComponentPopupMenu(status ? popupMenu : null);
    }

    /**
     * schaltet die vorwärts-/rückwärts-Buttons und Einträge
     */
    public void verlaufSchalten() {
        //TODO
    }

//    /**
//     * Vergrößern Funktion
//     */
//    private void zoomen() {
//        Zoom z = new Zoom();
//        z.anwenden(bild);
//        z.addPropertyChangeListener(new PropertyChangeListener() {
//            //reagiere darauf, wenn sich Bild geändert hat
//
//            @Override
//            public void propertyChange(PropertyChangeEvent event) {
//                bild = (Farbbild) event.getNewValue();
//                bildflaeche.setzeBild(bild);
//                verlaufSchalten();
//                statusAnzeigen("Bild auf " + event.getOldValue() + "% gezoomt.");
//                bildflaeche.repaint();
//                fenster.repaint();
//                fenstergroesseAnpassen();
//            }
//        });
//        verlaufSchalten();
//    }
}
