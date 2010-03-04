package z.ui;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JMenu;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.geom.GeneralPath;

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
}
