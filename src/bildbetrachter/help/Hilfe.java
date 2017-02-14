package bildbetrachter.help;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 *
 * @author eih
 */
public class Hilfe {

    public Hilfe() {
    }  

    /**
     * Zeigt das Hilfefenster, in welchem eine HTML Datei angezeigt wird
     *
     */
    public void zeigeHilfe() {
        // Einstellungen vom Fenster
        JFrame win = new JFrame();
        win.setTitle("Hilfe");
        win.getContentPane().setLayout(new BorderLayout());
        int w1 = (int) (Toolkit.getDefaultToolkit().getScreenSize().width * 0.5);
        int w2 = 900;
        int w = Math.max(w1, w2);
        int h = (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.75);
        win.setSize(w, h);
        // Symbol des Fensters festlegen        
        BufferedImage image = null;
        try {
            image = ImageIO.read(Hilfe.class.getResource("/bildbetrachter/images/help.png"));
        } catch (IllegalArgumentException | IOException e) {
            System.out.println(e.toString());
        }
        if (image != null) {
            win.setIconImage(image);
        }
        // Inhalt
        final JTextPane htmlPane = new JTextPane();
        htmlPane.setEditable(false);
        String fileName = "/bildbetrachter/help/hilfe.html";
        URL helpURL = Hilfe.class.getResource(fileName);
        if (helpURL != null) {
            try {
                htmlPane.setPage(helpURL);
            } catch (IOException e) {
                System.err.println("Bad URL: " + helpURL);
            }
        } else {
            System.err.println("Datei nicht gefunden: " + fileName);
        }
        htmlPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                HyperlinkEvent.EventType typ = e.getEventType();

                if (typ == HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        htmlPane.setPage(e.getURL());
                    } catch (IOException ev) {
                        System.out.println("Can’t follow link to "
                                + e.getURL().toExternalForm());
                    }
                }
            }
        });
        JScrollPane scrollPaneHelp = new JScrollPane(htmlPane);
        win.add(scrollPaneHelp);
        win.setVisible(true);
    }

    /**
     * 'Info'-Funktion: Zeige Informationen zur Anwendung.
     */
    public void zeigeInfo(JFrame fenster, String version) {
        String[] types = ImageIO.getWriterMIMETypes();
        JOptionPane.showMessageDialog(fenster,
                "Bildbetrachter GIT-Fortbildung 2017\n" + version + "\nUnterstützte Formate: "
                + Arrays.toString(types),
                "Info zu Bildbetrachter",
                JOptionPane.INFORMATION_MESSAGE);
    }   
}
