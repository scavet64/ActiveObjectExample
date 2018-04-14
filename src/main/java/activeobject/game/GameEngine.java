package activeobject.game;

import activeobject.game.gameobject.GameObjectFactory;

/**
 * Game environment class for the sake of completeness. Offers the game object factory to produce
 * entities. Has to be shut down properly.
 */
public class GameEngine {

    private GameObjectFactory gameObjectFactory = new GameObjectFactory();

    public GameObjectFactory getGameObjectFactory() {
        return gameObjectFactory;
    }

    /**
     * Shuts down services and managers and releases resources.
     */
    public void shutdown() {
        gameObjectFactory.shutdown();
    }
}
