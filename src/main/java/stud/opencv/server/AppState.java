package stud.opencv.server;

/**
 * Created by dialight on 06.11.16.
 */
public class AppState {

    public static volatile boolean alive = true;

    public static final Condition cond = new Condition();

    private static class Condition {

        private final Object monitor = new Object();
        private boolean myApp;

        public void wait(int seconds) {
            waitms(seconds * 1000);
        }

        public void waitms(int milliseconds) {
            myApp = false;
            synchronized (monitor) {
                while(!myApp) {
                    try {
                        monitor.wait(milliseconds);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void notifyAll_() {
            myApp = true;
            synchronized (monitor) {
                monitor.notifyAll();
            }
        }

    }

}
