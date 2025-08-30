package co.eci.snake.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Board represents the game board, including its dimensions and the positions
 * of mice, obstacles, turbo boosts, and teleport pairs. It provides methods to
 * query the board state and to update it as the snake moves.
 */
public final class Board {

    private final int width;
    private final int height;

    private final Set<Position> mice = new HashSet<>();
    private final Set<Position> obstacles = new HashSet<>();
    private final Set<Position> turbo = new HashSet<>();
    private final Map<Position, Position> teleports = new HashMap<>();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public enum MoveResult {
        MOVED, ATE_MOUSE, HIT_OBSTACLE, ATE_TURBO, TELEPORTED
    }

    public Board(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Board dimensions must be positive");
        }
        this.width = width;
        this.height = height;
        for (int i = 0; i < 6; i++) {
            mice.add(randomEmpty());
        }
        for (int i = 0; i < 4; i++) {
            obstacles.add(randomEmpty());
        }
        for (int i = 0; i < 3; i++) {
            turbo.add(randomEmpty());
        }
        createTeleportPairs(2);
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public Set<Position> mice() {
        lock.readLock().lock();
        try {
            return new HashSet<>(mice);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Set<Position> obstacles() {
        lock.readLock().lock();
        try {
            return new HashSet<>(obstacles);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Set<Position> turbo() {
        lock.readLock().lock();
        try {
            return new HashSet<>(turbo);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Map<Position, Position> teleports() {
        lock.readLock().lock();
        try {
            return new HashMap<>(teleports);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Attempts to move the snake one step in its current direction. Handles
     * interactions with mice, obstacles, turbo boosts, and teleports.
     *
     * @param snake the snake to move
     * @return the result of the move attempt
     */
    public MoveResult step(Snake snake) {
        Objects.requireNonNull(snake, "snake");
        var head = snake.head();
        var dir = snake.direction();
        Position next = new Position(head.x() + dir.dx, head.y() + dir.dy).wrap(width, height);

        // Read obstacles and teleports without blocking the entire function
        boolean teleported = false;
        Position finalNext = next; // to be used within the synchronized block
        boolean ateMouse = false;
        boolean ateTurbo = false;

        // Concurrent reading: checks obstacles and teleports
        lock.writeLock().lock();
        try {
            if (obstacles.contains(finalNext)) {
                return MoveResult.HIT_OBSTACLE;
            }

            if (teleports.containsKey(finalNext)) {
                finalNext = teleports.get(finalNext);
                teleported = true;
            }

            ateMouse = mice.remove(finalNext);
            ateTurbo = turbo.remove(finalNext);

            if (ateMouse) {
                mice.add(randomEmpty());
                obstacles.add(randomEmpty());
                if (ThreadLocalRandom.current().nextDouble() < 0.2) {
                    turbo.add(randomEmpty());
                }
            }
        } finally {
            lock.writeLock().unlock();
        }

        // Advance the snake out of the blockage
        snake.advance(finalNext, ateMouse);

        if (ateTurbo) {
            return MoveResult.ATE_TURBO;
        }
        if (ateMouse) {
            return MoveResult.ATE_MOUSE;
        }
        if (teleported) {
            return MoveResult.TELEPORTED;
        }
        return MoveResult.MOVED;
    }

    /**
     * Creates pairs of teleport positions on the board. Each pair consists of
     * two positions that teleport to each other.
     *
     * @param pairs the number of teleport pairs to create
     */
    private void createTeleportPairs(int pairs) {
        for (int i = 0; i < pairs; i++) {
            Position a = randomEmpty();
            Position b = randomEmpty();
            teleports.put(a, b);
            teleports.put(b, a);
        }
    }

    /**
     * Generates a random empty position on the board that is not occupied by
     * mice, obstacles, turbo boosts, or teleports.
     *
     * @return a random empty position
     */
    private Position randomEmpty() {
        var rnd = ThreadLocalRandom.current();
        Position p;
        int guard = 0;
        do {
            p = new Position(rnd.nextInt(width), rnd.nextInt(height));
            guard++;
            if (guard > width * height * 2) {
                break;
            }
        } while (mice.contains(p) || obstacles.contains(p) || turbo.contains(p) || teleports.containsKey(p));
        return p;
    }
}
