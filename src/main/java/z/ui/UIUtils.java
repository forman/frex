package z.ui;

import z.StringLiterals;
import z.frex.Frex;
import z.ui.dialog.MessageDialog;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.io.File;
import java.text.MessageFormat;

public class UIUtils {
    public static void centerComponent(Component component) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = component.getSize();
        component.setLocation((screenSize.width - frameSize.width) >> 1,
                              (screenSize.height - frameSize.height) >> 1);
    }

    public static JMenu createMenu(String s) {
        return configureButton(s, new JMenu());
    }

    public static JButton createButton(String s) {
        return configureButton(s, new JButton());
    }

    public static <T extends AbstractButton> T configureButton(String s, T button) {
        MenuText menuText = MenuText.create(s);
        button.setText(menuText.getText());
        if (menuText.getMnemonic() != -1) {
            button.setMnemonic(menuText.getMnemonic());
        }
        return button;
    }

    public static Shape createSliderShape(float b) {
        GeneralPath gp = new GeneralPath();
        gp.moveTo(0.0f, -b);
        gp.lineTo(b, b);
        gp.lineTo(-b, b);
        gp.closePath();
        return gp;
    }

    public static class MenuText {
        private final String text;
        private final int mnemonic;

        private MenuText(String text, int mnemonic) {
            this.text = text;
            this.mnemonic = mnemonic;
        }

        public String getText() {
            return text;
        }

        public int getMnemonic() {
            return mnemonic;
        }

        public static MenuText create(String s) {
            int mnemonic = -1;
            boolean mnemonicSeen = false;
            StringBuilder sb = new StringBuilder(s.length());
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (!mnemonicSeen && c == '&' && i < s.length() - 1) {
                    c = s.charAt(++i);
                    if (c != '&') {
                        mnemonic = (int) c;
                        mnemonicSeen = true;
                    }
                }
                sb.append(c);
            }
            return new MenuText(sb.toString(), mnemonic);
        }

    }

    public static File showOpenDialog(Window parent,
                                      String title,
                                      String lastDirPropertyName,
                                      String fileName,
                                      FileExtensionFileFilter... fileFilters) {

        String lastDir = Frex.getPreferences().get(lastDirPropertyName,
                                                   System.getProperty("user.home")); // NON-NLS
        JFileChooser dialog = new JFileChooser(lastDir);
        dialog.setDialogTitle(title);
        dialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
        dialog.setAcceptAllFileFilterUsed(false);
        for (int i = 0; i < fileFilters.length; i++) {
            FileFilter fileFilter = fileFilters[i];
            dialog.addChoosableFileFilter(fileFilter);
            if (i == 0) {
                dialog.setFileFilter(fileFilter);
            }
        }
        if (fileName != null && !fileName.isEmpty()) {
            File file = new File(lastDir, fileName);
            if (file.exists()) {
                dialog.setSelectedFile(file);
            }
        }
        int resp = dialog.showOpenDialog(parent);
        File currentDirectory = dialog.getCurrentDirectory();
        Frex.getPreferences().put(lastDirPropertyName,
                                  currentDirectory.getPath());
        if (resp == JFileChooser.APPROVE_OPTION) {
            return dialog.getSelectedFile();
        }
        return null;
    }


    public static File showSafeDialog(Window parent,
                                      String title,
                                      String lastDirPropertyName,
                                      String fileName,
                                      FileExtensionFileFilter[] selectedFileFilter,
                                      FileExtensionFileFilter... fileFilters) {
        String lastDir = Frex.getPreferences().get(lastDirPropertyName,
                                                   System.getProperty("user.home")); // NON-NLS
        JFileChooser dialog = new JFileChooser(lastDir);
        dialog.setDialogTitle(title);
        dialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
        dialog.setAcceptAllFileFilterUsed(false);
        for (FileExtensionFileFilter fileFilter : fileFilters) {
            dialog.addChoosableFileFilter(fileFilter);
        }
        if (selectedFileFilter[0] != null) {
            dialog.setFileFilter(selectedFileFilter[0]);
        } else if (fileFilters.length > 0) {
            dialog.setFileFilter(fileFilters[0]);
        }

        if (fileName != null && !fileName.isEmpty()) {
            File file = new File(lastDir, fileName);
            if (file.exists()) {
                dialog.setSelectedFile(file);
            }
        }

        while (true) {
            int resp = dialog.showSaveDialog(parent);
            Frex.getPreferences().put(lastDirPropertyName,
                                      dialog.getCurrentDirectory().getPath());
            if (resp == JFileChooser.APPROVE_OPTION) {
                FileExtensionFileFilter fileFilter = (FileExtensionFileFilter) dialog.getFileFilter();
                selectedFileFilter[0] = fileFilter;
                File selectedFile = fileFilter.appendMissingFileExtension(dialog.getSelectedFile());
                if (selectedFile.exists()) {
                    resp = MessageDialog.confirmYesNoCancel(parent,
                                                            title,
                                                            MessageFormat.format(StringLiterals.getString("gui.msg.errorFileExists"),
                                                                                 selectedFile.getName()));
                    if (resp == JOptionPane.YES_OPTION) {
                        return selectedFile;
                    } else if (resp == JOptionPane.CANCEL_OPTION) {
                        return null;
                    }
                } else {
                    return selectedFile;
                }
            } else {
                return null;
            }
        }
    }

}
