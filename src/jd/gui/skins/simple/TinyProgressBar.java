package jd.gui.skins.simple;

import java.awt.Cursor;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import jd.gui.skins.simple.components.DownloadView.JDProgressBar;
import net.miginfocom.swing.MigLayout;

public class TinyProgressBar extends JPanel {

    private static final long serialVersionUID = 8385631080915257786L;
    private JLabel lbl;
    private JDProgressBar prg;

    public TinyProgressBar() {
        this.setLayout(new MigLayout("ins 0", "[grow,fill]1[grow,fill]", "[grow,fill]"));

        this.add(lbl = new JLabel());
        this.add(prg = new JDProgressBar(), "width 10!");
        lbl.setOpaque(false);
        prg.setOpaque(false);
        prg.setOrientation(SwingConstants.VERTICAL);
        //this.setBorder(prg.getBorder());
        prg.setBorder(null);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
     

    }
public void setEnabled(boolean b){
    super.setEnabled(b);
    lbl.setEnabled(b);
    prg.setEnabled(b);
}
    public void setIcon(ImageIcon hosterIcon) {
        lbl.setIcon(hosterIcon);
    }

    public void setMaximum(long max) {
        prg.setMaximum(max);
    }

    public void setValue(long left) {
        prg.setValue(left);
    }

}
