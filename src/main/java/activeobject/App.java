package activeobject;

import activeobject.game.GameEngine;
import activeobject.game.gameobject.GameObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class App {

    private static int MOVER_THREADS_PER_GAMEOBJECT = 10000;

    public static void main(String[] args) throws InterruptedException, TimeoutException, ExecutionException {

        //Create the engine
        GameEngine gameEngine = new GameEngine();

        //Create the Game objects
        GameObject simpleGameObject = gameEngine.getGameObjectFactory().getSimpleGameObject();
        GameObject activeGameObject = gameEngine.getGameObjectFactory().getActiveGameObject();

        //Create and start the mover threads. Wait until they are finished
        List<Thread> moverThreads = new ArrayList<>();
        startMoverThreads(simpleGameObject, activeGameObject, moverThreads);
        waitForMoverThreads(moverThreads);

        //Print the game object positions
        System.out.println(String.format("The simple game object isn't threadsafe and located at %s",
                simpleGameObject.getPosition()));
        System.out.println(String.format("The active game object is threadsafe and located at %s",
                activeGameObject.getPosition()));

        boolean threadingIssueRevealed = !(simpleGameObject.getPosition().equals(activeGameObject.getPosition()));
        if (threadingIssueRevealed) {
            System.out
                    .println("The two game objects were moved an equal amount, but don't have the same position because of threading issues.");
        } else {
            System.out
                    .println("The two game objects were moved an equal amount and have the same position, but that's just because you're lucky. "
                            + "The threading issue is still silently present. You can try to spawn more threads via App.MOVER_THREADS_PER_GAMEOBJECT to "
                            + "increase the probability.");
        }

        // This call does the heavy task within another thread, so it returns immediately and processes async.
        // The result is probably not yet available.
        Future<String> activeGameObjectsHeavyDutyResult = activeGameObject.stemHeavyDuty();
        // This call does the heavy task in the current thread, so it blocks and makes the result available immediately.
        Future<String> simpleGameObjectsHeavyDutyResult = simpleGameObject.stemHeavyDuty();

        // This call blocks the current thread for max 100ms if the result is not yet available.
        System.out.println(activeGameObjectsHeavyDutyResult.get(100, TimeUnit.MILLISECONDS));
        // This call has just to be done because the GameObject interface's stemHeavyDuty-method returns a Future
        // of a String instead of a String.
        System.out.println(simpleGameObjectsHeavyDutyResult.get(100, TimeUnit.MILLISECONDS));

        gameEngine.shutdown();

    }

    /**
     * Wait for the current threads to finish and join the main thread
     * @param moverThreads list of threads to join
     * @throws InterruptedException
     */
    private static void waitForMoverThreads(List<Thread> moverThreads) throws InterruptedException {
        for (Thread thread : moverThreads) {
            thread.join();
        }
    }

    /**
     * Start the mover threads
     *
     * @param simpleGameObject
     * @param activeGameObject
     * @param moverThreads
     */
    private static void startMoverThreads(GameObject simpleGameObject, GameObject activeGameObject,
                                          List<Thread> moverThreads) {
        for (int i = 0; i < MOVER_THREADS_PER_GAMEOBJECT; i++) {
            Vec3f amount = new Vec3f(1, 0, 0);
            moverThreads.add(startMoverThread(String.format("SimpleMoverThread %d", i), simpleGameObject,
                    amount));
            moverThreads.add(startMoverThread(String.format("ActiveMoverThread %d", i), activeGameObject,
                    amount));
        }
    }

    private static Thread startMoverThread(String threadName, GameObject gameObject, Vec3f amount) {
        final int moveTimes = 200;
        Thread thread = new Thread(() -> {
            for (int i = 0; i < moveTimes; i++) {
                gameObject.move(amount);
            }
        });
        thread.setName(threadName);
        thread.start();
        return thread;
    }
}
