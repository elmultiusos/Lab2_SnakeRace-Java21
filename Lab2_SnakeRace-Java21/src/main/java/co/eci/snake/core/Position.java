package co.eci.snake.core;

/**
 * Position represents a coordinate on the game board with x and y values. It
 * includes a method to wrap the position around the board dimensions.
 */
public record Position(int x, int y) {

    public Position wrap(int width, int height) {
        int nx = ((x % width) + width) % width;
        int ny = ((y % height) + height) % height;
        return new Position(nx, ny);
    }
}
