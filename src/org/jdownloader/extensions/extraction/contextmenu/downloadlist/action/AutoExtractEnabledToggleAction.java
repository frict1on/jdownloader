package org.jdownloader.extensions.extraction.contextmenu.downloadlist.action;

import java.awt.event.ActionEvent;
import java.util.List;

import jd.gui.swing.jdgui.JDGui;
import jd.gui.swing.jdgui.WarnLevel;

import org.appwork.utils.swing.dialog.Dialog;
import org.jdownloader.extensions.extraction.Archive;
import org.jdownloader.extensions.extraction.BooleanStatus;
import org.jdownloader.extensions.extraction.contextmenu.downloadlist.AbstractExtractionContextAction;

public class AutoExtractEnabledToggleAction extends AbstractExtractionContextAction {

    public AutoExtractEnabledToggleAction() {
        super();
        setName(org.jdownloader.extensions.extraction.translate.T._.contextmenu_autoextract());
        setSmallIcon(new ExtractIconVariant("refresh", 18, 14, 0, 0).crop());
        setSelected(false);

    }

    public void setSelected(final boolean selected) {
        super.setSelected(selected);
        if (isSelected()) {
            setName(org.jdownloader.extensions.extraction.translate.T._.contextmenu_disable_auto_extract2());
        } else {
            setName(org.jdownloader.extensions.extraction.translate.T._.contextmenu_disable_auto_extract2());
        }
    }

    @Override
    protected void onAsyncInitDone() {
        super.onAsyncInitDone();
        final List<Archive> lArchives = getArchives();
        if (lArchives != null && lArchives.size() > 0) {
            setSelected(_getExtension().isAutoExtractEnabled(lArchives.get(0)));
        }
    }

    public void actionPerformed(ActionEvent e) {
        final List<Archive> lArchives = getArchives();
        if (!isEnabled() || lArchives == null) {
            return;
        } else {
            for (Archive archive : lArchives) {
                archive.setAutoExtract(isSelected() ? BooleanStatus.TRUE : BooleanStatus.FALSE);
            }
            if (JDGui.bugme(WarnLevel.NORMAL)) {
                Dialog.getInstance().showMessageDialog(Dialog.STYLE_SHOW_DO_NOT_DISPLAY_AGAIN, isSelected() ? org.jdownloader.extensions.extraction.translate.T._.set_autoextract_true() : org.jdownloader.extensions.extraction.translate.T._.set_autoextract_false());
            }
        }
    }

}
