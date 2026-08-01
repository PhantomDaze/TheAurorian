package shiroroku.theaurorian.Demo;

/**
 * One watchable demo case. Lifecycle: {@link #setup} → short beat → {@link #run}
 * → {@link #verify}. The runner keeps the camera on {@link VisibleDemoRunner.DemoContext#focus}.
 */
public interface VisibleDemoCase {

    String id();

    String title();

    default String subtitle() {
        return "";
    }

    /** Stage size in blocks (width along player-right, depth forward, height clear). */
    default int stageWidth() {
        return 7;
    }

    default int stageDepth() {
        return 7;
    }

    default int stageHeight() {
        return 5;
    }

    /** Place blocks / prepare state. Camera focuses after this. */
    void setup(VisibleDemoRunner.DemoContext ctx) throws Exception;

    /** Perform the action the player should see. */
    void run(VisibleDemoRunner.DemoContext ctx) throws Exception;

    /** Assert outcome. Return false or call {@code ctx.fail} on failure. */
    boolean verify(VisibleDemoRunner.DemoContext ctx) throws Exception;
}
