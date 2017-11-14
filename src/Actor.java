public class Actor {
    // directions which the actor might face
    public enum Direction { NORTH, SOUTH, EAST, WEST };

    // statuses that can be thrown for moveForward()
    public enum ForwardStatus { SUCCESS, WALL, OUTBOUNDS, VISITED, BAD };

    // grid on which the actor lies
    Maze maze;

    // direction which the actor is facing
    Direction direction;

    // actor position on the cell grid
    private int x, y;

    // actor position on the grid in the last step
    private int lastX, lastY;

    private boolean backTrace;
    private boolean exhausted;
    private boolean foundExit;

    public Actor(int x, int y) {
        direction = Direction.SOUTH;
        foundExit = false;

        backTrace = false;
        exhausted = false;

        this.maze = null;

        this.x = x;
        this.y = y;

        this.flush();
    }

    public Actor(Maze maze, int x, int y) {
        direction = Direction.SOUTH;
        foundExit = false;

        this.maze = maze;

        backTrace = false;
        exhausted = false;

        this.x = x;
        this.y = y;

        this.flush();
    }

    // check if the position has changed since last flush
    public boolean isPositionChanged() {
        return (x != lastX || y != lastY);
    }

    public void moveDown() { y += 1; }

    /* mutator for actor's maze */
    public void setMaze(Maze maze)  { this.maze = maze; }
    public void clearMaze()         { this.maze = null; }

    /* accessors and mutators for actor position */
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public int  getPositionX()      { return x; }
    public int  getPositionY()      { return y; }
    public int  lastPositionX()     { return lastX; }
    public int  lastPositionY()     { return lastY; }

    /* moves actor forward, returns "SUCCESS" if the action was successful, otherwise will return these enums:
    *   OUTBOUNDS: was moving out of the maze boundary
    *   WALL: was moving into a wall
    *   VISITED: was moving into a visited spot
    *
    *   @PARAMS:
    *       boolean moveVisited: allows the actor to move into visited space. */
    public ForwardStatus moveForward(boolean moveVisited) {
        switch (direction) {
            case NORTH:
                if (y - 1 < 0) {
                    return ForwardStatus.OUTBOUNDS;
                } else switch (maze.getCell(x, y - 1)) {
                    case WALL:
                        return ForwardStatus.WALL;

                    case VISITED:
                        if (moveVisited) {
                            y -= 1;
                        }
                        else return ForwardStatus.VISITED;
                        break;

                    case PATH:
                        y -= 1;
                }
                break;

            case SOUTH:
                if (y + 1 > maze.getHeight() - 1) {
                    return ForwardStatus.OUTBOUNDS;
                } else switch (maze.getCell(x, y + 1)) {
                    case WALL:
                        return ForwardStatus.WALL;

                    case VISITED:
                        if (moveVisited) {
                            y += 1;
                        }
                        else return ForwardStatus.VISITED;
                        break;

                    case PATH:
                        y += 1;
                }
                break;

            case EAST:
                if (x + 1 > maze.getWidth() - 1) {
                    return ForwardStatus.OUTBOUNDS;
                } else switch (maze.getCell(x + 1, y)) {
                    case WALL:
                        return ForwardStatus.WALL;

                    case VISITED:
                        if (moveVisited) {
                            x += 1;
                        }
                        else return ForwardStatus.VISITED;
                        break;

                    case PATH:
                        x += 1;
                }
                break;

            case WEST:
                if (x - 1 < 0) {
                    return ForwardStatus.OUTBOUNDS;
                } else switch (maze.getCell(x - 1, y)) {
                    case WALL:
                        return ForwardStatus.WALL;

                    case VISITED:
                        if (moveVisited) {
                            x -= 1;
                        }
                        else return ForwardStatus.VISITED;
                        break;

                    case PATH:
                        x -= 1;
                }
                break;
        }

        maze.update();
        return ForwardStatus.SUCCESS;
    }

    /* accessor for backtracing status */
    public boolean isBacktracing() { return backTrace; }

    /* accessor and mutator for the actor direction */
    public void setDirection(Direction direction)   { this.direction = direction; }
    public Direction getDirection()                 { return direction; }
    public void turnCW() {
        switch (direction) {
            case NORTH:
                setDirection(Direction.EAST);
                break;
            case EAST:
                setDirection(Direction.SOUTH);
                break;
            case SOUTH:
                setDirection(Direction.WEST);
                break;
            case WEST:
                setDirection(Direction.NORTH);
                break;
        }
    }

    public void turnCCW() {
        switch (direction) {
            case NORTH:
                setDirection(Direction.WEST);
                break;
            case EAST:
                setDirection(Direction.NORTH);
                break;
            case SOUTH:
                setDirection(Direction.EAST);
                break;
            case WEST:
                setDirection(Direction.SOUTH);
                break;
        }
    }

    /* take a step */
    public void takeStep() {
        takeStep(backTrace);
    }

    public void takeStep(boolean moveVisited) {
        if (!foundExit) {
            int i = 0;

            if (!backTrace) {
                turnCW();
                if (moveForward(false) != ForwardStatus.SUCCESS)
                    if (moveForward(false) == ForwardStatus.OUTBOUNDS) foundExit = true;
                        else {
                            turnCCW();
                            while (i < 4 && moveForward(false) != ForwardStatus.SUCCESS) {
                                turnCW();
                                i++;
                            }

                            // start going backwards if reached dead end
                            if ( i == 4 ) {
                                for (int j = 0; j < 2; j++) turnCW();
                                backTrace = true;
                                takeStep(backTrace);
                            }
                        }
            } else if (!exhausted) {
                // actor is going backwards, and may travel through visited cells.
                // however, the actor must check all sides except behind to find a spot it has not yet visited before
                // doing so.
                if (moveForward(false) == ForwardStatus.SUCCESS)
                    backTrace = false;
                else {
                    turnCW();
                    if (moveForward(false) == ForwardStatus.SUCCESS) {
                        backTrace = false;
                    } else {
                        turnCW(); turnCW();
                        if (moveForward(false) == ForwardStatus.SUCCESS)
                            backTrace = false;
                        else {
                            turnCW();
                            if (moveForward(true) != ForwardStatus.SUCCESS)
                                exhausted = true;
                        }
                    }
                }
            } else {
                //
            }
        }
    }

    /* update last positions */
    public void flush() {
        lastX = x;
        lastY = y;
    }
}
