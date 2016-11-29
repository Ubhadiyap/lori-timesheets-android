package rx.plugins;

/**
 * @author artemik
 */

public class RxJavaTestPlugins extends RxJavaPlugins {
    RxJavaTestPlugins() {
        super();
    }

    public static void resetPlugins(){
        getInstance().reset();
    }
}
