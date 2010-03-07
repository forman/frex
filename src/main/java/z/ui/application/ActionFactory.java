package z.ui.application;

import z.StringLiterals;
import z.ui.dialog.MessageDialog;

public abstract class ActionFactory {
    public static ActionFactory QUIT = new ActionFactory() {
        @Override
        public String getId() {
            return ExitAction.ID;
        }

        @Override
        public Action create(final ApplicationWindow window) {
            return new ExitAction(window);
        }
    };

    public static ActionFactory ABOUT = new ActionFactory() {
        @Override
        public String getId() {
            return AboutAction.ID;
        }

        @Override
        public Action create(final ApplicationWindow window) {
            return new AboutAction(window);
        }
    };

    public abstract Action create(final ApplicationWindow window);

    public abstract String getId();

    public static class ExitAction extends Action {
        public static final String ID = "exit"; // NON-NLS
        private final ApplicationWindow window;

        public ExitAction(ApplicationWindow window) {
            super(ID);
            this.window = window;
            setText(StringLiterals.getString("gui.action.text.exit"));
        }

        @Override
        public void run() {
            window.close();
        }
    }

    public static class AboutAction extends Action {
        public static final String ID = "about";   // NON-NLS
        private final ApplicationWindow window;

        public AboutAction(ApplicationWindow window) {
            super(ID);
            this.window = window;
            setText(StringLiterals.getString("gui.action.text.about"));
        }

        @Override
        public void run() {
            String aboutTitle = getValue("aboutTitle").toString();  // NON-NLS
            String aboutMessage = getValue("aboutMessage").toString(); // NON-NLS
            // todo:  use logo, version, etc
            MessageDialog.openInfo(window.getShell(), aboutTitle, aboutMessage);
        }
    }
}
