package org.example.interview.atlassian.code_design.snake;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@Data
@AllArgsConstructor
public class Position {
    int x,y;
    public Position move(Direction direction){
        return switch (direction){
            case UP  -> new Position(x-1, y);
            case DOWN -> new Position(x+1, y);
            case RIGHT -> new Position(x, y+1);
            case LEFT -> new Position(x, y-1);
        };
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(obj ==null || getClass() !=obj.getClass()) return false;
        Position position = (Position) obj;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode(){
        return Objects.hash(x,y);
    }

    @Override
    public String toString(){
        return String.format("(%s, %s)", x,y);
    }
}
