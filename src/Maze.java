public class Maze {
    public enum Cell {
        PATH, WALL, VISITED, BAD, ACTOR
    }

    private Cell[][] cellList;
    private Actor actor;

    public Maze(Cell[][] cellList) {
        this.cellList = cellList;
        actor = new Actor(this, 2, 0);

        cellList[actor.getPositionY()][actor.getPositionX()] = Cell.ACTOR;
    }

    public Maze(Cell[][] cellList, Actor actor) {
        this.cellList = cellList;
        this.actor = actor;
        this.actor.setMaze(this);

        cellList[actor.getPositionY()][actor.getPositionX()] = Cell.ACTOR;
    }

    public static Cell[][] charToCell(char[][] cellList) {
        Cell[][] list = new Cell[cellList.length][cellList[0].length];

        for (int i = 0; i < cellList.length; i++) {
            for (int j = 0; j < cellList[0].length; j++) {
                switch(cellList[i][j]) {
                    case 0:
                        list[i][j] = Cell.WALL;
                        break;
                    case 1:
                        list[i][j] = Cell.PATH;
                        break;
                }
            }
        }

        return list;
    }

    public String rowToString(Cell[] row) {
        String string = "";

        for (Cell bite : row) {
            switch(bite) {
                case PATH:
                    string = string.concat(" ");
                    break;
                case WALL:
                    string = string.concat("#");
                    break;
                case VISITED:
                    string = string.concat("~");
                    break;
                case BAD:
                    string = string.concat("x");
                    break;
                case ACTOR:
                    string = string.concat("@");
                    break;
            }
        }

        return string;
    }

    public String paintMaze() {
        String paint = "";

        for (Cell[] cellRow : cellList) {
            paint = paint.concat(rowToString(cellRow).concat("\n"));
        }

        return paint;
    }

    /* dimension accessor */
    public int getWidth()   { return cellList[0].length; }
    public int getHeight()  { return cellList.length; }

    /* cell accessor */
    public Cell getCell(int x, int y) {
        if (x >= 0 && y >= 0) return cellList[y][x];
        else return null;
    }

    /* actor accessor and mutator */
    public Actor getActor()             { return actor; }
    public void setActor(Actor actor)   { this.actor = actor; }

    /* update actor's position and print results */
    public void update() {
        if (actor.isPositionChanged()) {
            cellList[actor.lastPositionY()][actor.lastPositionX()] =
                    (getCell(actor.getPositionX(), actor.getPositionY()) == Cell.VISITED) ? Cell.BAD : Cell.VISITED;
            cellList[actor.getPositionY()][actor.getPositionX()] = Cell.ACTOR;
            actor.flush();
        }
    }

    @Override
    public String toString() {
        update();
        return paintMaze();
    }
}
