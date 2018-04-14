package activeobject.game.gameobject;

import com.sun.javafx.geom.Vec3f;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Implements the GameObject interface non-thread-safe.
 */
public class SimpleGameObject implements GameObject {
    protected Vec3f position = new Vec3f();

    SimpleGameObject() {
    }

    public void move(Vec3f amount) {
        this.position.add(amount);
    }

    public Vec3f getPosition() {
        return position;
    }

    /**
     * This implementation does the processing in the current task and therefore blocks. The result
     * can be obtained from the future immediately afterwards. If no async executions are expected to
     * be implemented, the return type of the interface should be changed to return a String value
     * directly.
     *
     * @return a completed future of the processed String value.
     */
    @Override
    public Future<String> stemHeavyDuty() {
        try {
            Thread.sleep(50);
            return CompletableFuture.completedFuture(String.format("Heavy task completed by %s!", Thread
                    .currentThread().getName()));
        } catch (InterruptedException e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            return CompletableFuture
                    .completedFuture("Error occurred while stemming heavy duty task in a SimpleGameObject!"
                            + "\n" + writer.toString() + "\n");
        }
    }
}
