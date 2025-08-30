package co.eci.snake.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class PositionTest {

    @Test
    void testWrapWithinBounds() {
        Position p = new Position(3, 4);
        assertEquals(new Position(3, 4), p.wrap(10, 10));
    }

    @Test
    void testWrapNegativeCoordinates() {
        Position p = new Position(-1, -1);
        assertEquals(new Position(9, 9), p.wrap(10, 10));
    }

    @Test
    void testWrapOverflowCoordinates() {
        Position p = new Position(11, 12);
        assertEquals(new Position(1, 2), p.wrap(10, 10));
    }
}
