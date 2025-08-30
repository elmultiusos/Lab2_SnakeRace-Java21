package co.eci.snake.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class SnakeTest {

    @Test
    void testSnakeTurnValid() {
        Snake s = Snake.of(5, 5, Direction.UP);
        s.turn(Direction.LEFT);
        assertEquals(Direction.LEFT, s.direction());
    }

    @Test
    void testSnakeTurnInvalidOpposite() {
        Snake s = Snake.of(5, 5, Direction.UP);
        s.turn(Direction.DOWN);
        assertEquals(Direction.UP, s.direction(), "No debería girar en dirección opuesta");
    }

    @Test
    void testSnakeAdvanceAndGrow() {
        Snake s = Snake.of(0, 0, Direction.RIGHT);
        s.advance(new Position(1, 0), true);
        assertEquals(2, s.snapshot().size(), "Debe crecer al comer");
    }
}
