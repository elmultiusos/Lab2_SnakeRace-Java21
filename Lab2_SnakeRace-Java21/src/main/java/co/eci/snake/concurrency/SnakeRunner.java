package co.eci.snake.concurrency;

import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

import co.eci.snake.core.Board;
import co.eci.snake.core.Direction;
import co.eci.snake.core.Snake;

/**
 * SnakeRunner is a Runnable that controls the movement of a snake on the board.
 * It handles automatic turning, turbo boosts, and pausing/resuming based on a
 * semaphore signal.
 */
public final class SnakeRunner implements Runnable {

    private final Snake snake;
    private final Board board;
    private final Semaphore pauseSignal;

    private final int baseSleepMs = 80;
    private final int turboSleepMs = 40;
    private int turboTicks = 0;

    public SnakeRunner(Snake snake, Board board, Semaphore pauseSignal) {
        this.snake = snake;
        this.board = board;
        this.pauseSignal = pauseSignal;
    }

    /*
    * The run method contains the main loop for the snake's movement. It checks
    * for pause signals, randomly turns the snake, and moves it on the board.
    * If the snake hits an obstacle, it makes a random turn. If it eats a turbo
    * boost, it speeds up for a set number of ticks.
     */
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                pauseSignal.acquire();
                pauseSignal.release();

                maybeTurn();
                var res = board.step(snake);
                if (res == Board.MoveResult.HIT_OBSTACLE) {
                    randomTurn();
                } else if (res == Board.MoveResult.ATE_TURBO) {
                    turboTicks = 100;
                }
                int sleep = (turboTicks > 0) ? turboSleepMs : baseSleepMs;
                if (turboTicks > 0) {
                    turboTicks--;
                }
                Thread.sleep(sleep);
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    private void maybeTurn() {
        double p = (turboTicks > 0) ? 0.05 : 0.10;
        if (ThreadLocalRandom.current().nextDouble() < p) {
            randomTurn();
        }
    }

    private void randomTurn() {
        var dirs = Direction.values();
        snake.turn(dirs[ThreadLocalRandom.current().nextInt(dirs.length)]);
    }
}
