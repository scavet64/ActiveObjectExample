package activeobject.game.gameobject;

import java.util.ArrayList;
import java.util.List;

/**
 * Can be used to produce entities. Registers all created entities for later cleanup.
 */
public class GameObjectFactory {

    private List<GameObject> gameObjects = new ArrayList<>();

    /**
     * Creates a simple, non-thread-safe game object.
     *
     * @return a new game object
     */
    public GameObject getSimpleGameObject() {
        GameObject gameObject = new SimpleGameObject();
        gameObjects.add(gameObject);
        return gameObject;
    }

    /**
     * Creates a thread-safe game object.
     *
     * @return a new game object
     */
    public GameObject getActiveGameObject() {
        GameObject gameObject = new ActiveGameObject();
        gameObjects.add(gameObject);
        return gameObject;
    }

    /**
     * Releases resources of all game objects.
     */
    public void shutdown() {
        gameObjects.forEach(GameObject::kill);
    }
}
