package co.eci.snake.core;

/**
 * Snake represents the snake in the game, including its body positions, current
 * direction, and methods to turn, advance, and grow.
 */
import java.util.ArrayDeque;
import java.util.Deque;

public final class Snake {

    private final Deque<Position> body = new ArrayDeque<>();
    private volatile Direction direction;
    private int maxLength = 5;

    private Snake(Position start, Direction dir) {
        body.addFirst(start);
        this.direction = dir;
    }

    public static Snake of(int x, int y, Direction dir) {
        return new Snake(new Position(x, y), dir);
    }

    public Direction direction() {
        return direction;
    }

    /**
     * Turns the snake to the specified direction, unless it is directly
     * opposite to its current direction.
     *
     * @param dir the new direction
     */
    public void turn(Direction dir) {
        if ((direction == Direction.UP && dir == Direction.DOWN)
                || (direction == Direction.DOWN && dir == Direction.UP)
                || (direction == Direction.LEFT && dir == Direction.RIGHT)
                || (direction == Direction.RIGHT && dir == Direction.LEFT)) {
            return;
        }
        this.direction = dir;
    }

    public Position head() {
        return body.peekFirst();
    }

    public Deque<Position> snapshot() {
        return new ArrayDeque<>(body);
    }

    /**
     * Advances the snake by adding a new head position. If the snake is not
     * growing, the tail is removed to maintain its length.
     *
     * @param newHead the new head position
     * @param grow whether the snake should grow (not remove the tail)
     */
    public void advance(Position newHead, boolean grow) {
        body.addFirst(newHead);
        if (grow) {
            maxLength++;
        }
        while (body.size() > maxLength) {
            body.removeLast();
        }
    }
}
