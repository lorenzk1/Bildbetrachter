package bildbetrachter.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

/**
 *
 * @author eih
 */
public class MenuHelper {

    /**
     * Erstellt einen Checkboxeintrag und fügt ihn in ein Menü hinzu
     *
     * @param title Beschriftung des Eintrages
     * @param keyStroke Tastaturkürzel oder null, wenn es keins geben soll
     * @param menue Das Menü, in das der Eintrag eingefügt wird
     * @param isSelected true, wenn Eintrag ausgewählt sein soll, sonst false
     * @param action die auszuführende Aktion
     * @return der Menüeintrag
     */
    public static JCheckBoxMenuItem createCheckBoxMenuItem(String title, KeyStroke keyStroke, JMenu menue, boolean isSelected, final Runnable action) {
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(title);
        item.setSelected(isSelected);
        item.setAccelerator(keyStroke);
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
        menue.add(item);
        return item;
    }

    /**
     * Erzeugt und initialisiert einen Button für eine Toolbar
     *
     * @param iconPath Pfadangabe zum Bild (absolut)
     * @param toolbar die ToolBar, in die der Button eingefügt werden soll
     * @param toolTipText der Text für den Tooltipp
     * @param action die auszuführende Aktion
     * @return der Button
     */
    public static JButton createButton(String iconPath, JToolBar toolbar, String toolTipText, final Runnable action) {
        JButton btn = new JButton(new ImageIcon(MenuHelper.class.getResource(iconPath)));
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
        btn.setToolTipText(toolTipText);
        toolbar.add(btn);
        return btn;
    }

    /**
     * Erstellt einen Menüeintrag und fügt ihn ein Menü hinzu
     *
     * @param title Beschriftung des Eintrages
     * @param keyStroke Tastaturkürzel oder null, wenn es keins geben soll
     * @param menue Das Menü, in das der Eintrag eingefügt wird
     * @param action die auszuführende Aktion
     * @return der Menüeintrag
     */
    public static JMenuItem createMenuItem(String title, KeyStroke keyStroke, JMenu menue, final Runnable action) {
        JMenuItem item = new JMenuItem(title);
        item.setAccelerator(keyStroke);
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
        menue.add(item);
        return item;
    }

    /**
     * Erstellt einen Menüeintrag für ein Kontextmenü und fügt ihn in ein Menü
     * hinzu
     *
     * @param title Beschriftung des Eintrages
     * @param keyStroke Tastaturkürzel oder null, wenn es keins geben soll
     * @param menue Das Kontextmenü, in das der Eintrag eingefügt wird
     * @param action die auszuführende Aktion
     * @return der Menüeintrag
     */
    public static JMenuItem createMenuItem(String title, KeyStroke keyStroke, JPopupMenu menue, final Runnable action) {
        JMenuItem item = new JMenuItem(title);
        item.setAccelerator(keyStroke);
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
        menue.add(item);
        return item;
    }

    /**
     * Erzeugt und initialisiert einen ToggleButton für eine Toolbar
     *
     * @param iconPath Pfadangabe zum Bild (absolut)
     * @param toolbar die ToolBar, in die der Button eingefügt werden soll
     * @param toolTipText der Text für den Tooltipp
     * @param action die auszuführende Aktion
     * @return der Button
     */
    public static JToggleButton createToggleButton(String iconPath, JToolBar toolbar, String toolTipText, final Runnable action) {
        JToggleButton btn = new JToggleButton(new ImageIcon(Bildbetrachter.class.getResource(iconPath)));
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
        btn.setToolTipText(toolTipText);
        toolbar.add(btn);
        return btn;
    }
}
