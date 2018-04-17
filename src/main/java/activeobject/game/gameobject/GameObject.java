package activeobject.game.gameobject;

import activeobject.Vec3f;

import java.util.concurrent.Future;

/**
 * A simple interface for different implementations of a game object. Implementations are expected
 * to care for thread-safeness on their own.
 */
public interface GameObject {

    void move(Vec3f amount);

    Vec3f getPosition();

    /**
     * This is probably a time-consuming task, therefore a Future is returned. How the computation is
     * done can be decided by the one implementing the interface.
     *
     * @return
     */
    Future<String> stemHeavyDuty();

    /**
     * This method can be used to cleanup resources when the application shuts down.
     */
    default void kill() {
    }
}
