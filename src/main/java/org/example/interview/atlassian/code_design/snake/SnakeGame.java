package org.example.interview.atlassian.code_design.snake;

import lombok.Data;

import java.util.*;

@Data
public class SnakeGame {
    private final int rows, cols;
    private final Deque<Position> snakeBody;
    private final Set<Position> occupied;
    private final Queue<Position> foodQueue;
    private boolean isAlive;
    private int score;

    public SnakeGame(int rows, int cols, List<Position> foodList){
        this.rows = rows;
        this.cols = cols;
        this.snakeBody = new LinkedList<>();
        this.occupied = new HashSet<>();
        this.foodQueue = new LinkedList<>(foodList);
        Position start = new Position(0, 0);
        snakeBody.addFirst(start);
        occupied.add(start);
        isAlive = true;
        score = 0;
    }

    public boolean move(Direction direction){
        if(!isAlive) return false;

        Position currentHead = snakeBody.peekFirst();
        assert currentHead != null;
        Position newHead = currentHead.move(direction);

        if(newHead.x<0 || newHead.x>=rows || newHead.y<0 || newHead.y>=cols){
            isAlive = false;
            return false;
        }

        Position tail = snakeBody.peekLast();
        occupied.remove(tail);

        if(occupied.contains(newHead)){
            isAlive = false;
            return false;
        }

        snakeBody.addFirst(newHead);
        occupied.add(newHead);

        if(!foodQueue.isEmpty() && foodQueue.peek().equals(newHead)){
            foodQueue.poll();
            score++;
        } else {
            snakeBody.removeLast();
        }

        occupied.add(tail);
        return true;
    }

    public static void main(String[] args) {
        List<Position> food = List.of(
                new Position(0, 1),
                new Position(1, 1),
                new Position(2, 1)
        );

        SnakeGame game = new SnakeGame(5, 5, food);

        System.out.println("Move RIGHT: " + game.move(Direction.RIGHT)); // eats food
        System.out.println("Score: " + game.getScore());

        System.out.println("Move DOWN: " + game.move(Direction.DOWN)); // eats food
        System.out.println("Score: " + game.getScore());

        System.out.println("Move DOWN: " + game.move(Direction.DOWN)); // moves only
        System.out.println("Move LEFT: " + game.move(Direction.LEFT));
        System.out.println("Move UP: " + game.move(Direction.UP)); // dies

        System.out.println("Is Alive: " + game.isAlive());
        System.out.println("Final Score: " + game.getScore());
        System.out.println("Snake Body: " + game.getSnakeBody());
    }
}
