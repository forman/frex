package z.ui.application;

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
        public static final String ID = "exit";
        private final ApplicationWindow window;

        public ExitAction(ApplicationWindow window) {
            super(ID);
            this.window = window;
            setText("&Beenden");
        }

        @Override
        public void run() {
            window.close();
        }
    }

    public static class AboutAction extends Action {
        public static final String ID = "about";
        private final ApplicationWindow window;

        public AboutAction(ApplicationWindow window) {
            super(ID);
            this.window = window;
            setText("&Über...");
        }

        @Override
        public void run() {
            String aboutTitle = getValue("aboutTitle").toString();
            String aboutMessage = getValue("aboutMessage").toString();
            // todo:  use logo, version, etc
            MessageDialog.openInfo(window.getShell(), aboutTitle, aboutMessage);
        }
    }
}
