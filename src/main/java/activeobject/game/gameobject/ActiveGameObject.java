package activeobject.game.gameobject;

import com.sun.javafx.geom.Vec3f;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Implements the GameObject interface threadsafe with the active object pattern.
 */
public class ActiveGameObject implements GameObject {

    private BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();
    private volatile boolean stopRequested;

    protected Vec3f position = new Vec3f();

    ActiveGameObject() {
        Thread thread = new Thread(() -> {
            while (!stopRequested) {
                try {
                    //Take Retrieves and removes the head of this queue,
                    //waiting if necessary until an element becomes available.
                    Runnable runnable = tasks.take();
                    runnable.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setName("ActiveGameObjectThread");
        thread.start();
    }

    /**
     * Stops the execution of this game object gracefully with a stopRequested-flag.
     */
    @Override
    public void kill() {
        stopRequested = true;
        tasks.add(() -> {
        }); // This reactivates the game object's thread
    }

    public void move(Vec3f amount) {
        tasks.add(() -> position.add(amount));
    }

    /**
     * This implementation does the processing in the game object's own thread and therefore returns
     * immediately. The result of the computation can be obtained from the future afterwards, after
     * the future was completed.
     *
     * @return a future of the processed String value.
     */
    @Override
    public Future<String> stemHeavyDuty() {
        CompletableFuture<String> result = new CompletableFuture<>();

        Runnable runnable = () -> {
            try {
                Thread.sleep(50);
                result.complete(String.format("Heavy task completed by %s!", Thread.currentThread()
                        .getName()));
            } catch (InterruptedException e) {
                StringWriter writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                result.complete("Error occurred while stemming heavy duty task in an ActiveGameObject!"
                        + "\n" + writer.toString() + "\n");
            }
        };

        tasks.add(runnable);

        return result;
    }

    public Vec3f getPosition() {
        return position;
    }
}
