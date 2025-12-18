package modulProject;

import java.awt.Point;
import java.util.*;

/**
 * AI Player class - Implements an intelligent maze-solving algorithm
 * Uses depth-first search with backtracking to navigate through the maze
 */
public class AIPlayer {
    private Point position;              // Current position of the AI player
    private boolean[][] visited;         // Track which cells have been visited
    private Stack<Point> path;           // Stack to maintain the path taken (for backtracking)
    private Set<Point> deadEnds;         // Set of identified dead-end positions
    private Random random;               // Random number generator for move selection
    private int mazeWidth;               // Width of the maze
    private int mazeHeight;              // Height of the maze
    
    /**
     * Constructor - Initializes the AI player at the starting position
     * @param startPosition Initial position in the maze
     * @param mazeWidth Width of the maze grid
     * @param mazeHeight Height of the maze grid
     */
    public AIPlayer(Point startPosition, int mazeWidth, int mazeHeight) {
        this.position = new Point(startPosition);
        this.mazeWidth = mazeWidth;
        this.mazeHeight = mazeHeight;
        this.visited = new boolean[mazeHeight][mazeWidth];
        this.path = new Stack<>();
        this.deadEnds = new HashSet<>();
        this.random = new Random();
        
        // Mark starting position as visited and add to path
        visited[position.y][position.x] = true;
        path.push(new Point(position.x, position.y));
    }
    
    /**
     * Move the AI player one step towards the exit
     * Uses DFS algorithm with dead-end detection and backtracking
     * @param maze 2D array representing the maze (0 = path, 1 = wall)
     * @param exit Target exit position
     */
    
    public void move(int[][] maze, Point exit) {
        // If already at exit, stop moving
        if (position.equals(exit)) {
            return;
        }
        
        // Find all available valid moves from current position
        List<Point> availableMoves = new ArrayList<>();
        // Direction vectors: right, down, left, up
        int[][] dirs = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        
        // Check each direction for valid moves
        for (int[] dir : dirs) {
            int nx = position.x + dir[0];
            int ny = position.y + dir[1];
            Point next = new Point(nx, ny);
            
            // Valid move if: within bounds, not a wall, not visited, not a known dead end
            if (nx >= 0 && nx < mazeWidth && ny >= 0 && ny < mazeHeight 
                && maze[ny][nx] == 0 && !visited[ny][nx] && !deadEnds.contains(next)) {
                availableMoves.add(next);
            }
        }
        
        if (!availableMoves.isEmpty()) {
            // If there are available moves, randomly select one
            Point nextMove = availableMoves.get(random.nextInt(availableMoves.size()));
            position.x = nextMove.x;
            position.y = nextMove.y;
            // Mark new position as visited and add to path
            visited[position.y][position.x] = true;
            path.push(new Point(position.x, position.y));
        } else {
            // No available moves - this is a dead end, backtrack
            if (!path.isEmpty()) {
                // Mark current position as a dead end
                deadEnds.add(new Point(position.x, position.y));
                path.pop();
                // Move back to previous position in the path
                if (!path.isEmpty()) {
                    Point previous = path.peek();
                    position.x = previous.x;
                    position.y = previous.y;
                }
            }
        }
    }
    
    /**
     * Get current position of the AI player
     * @return Copy of current position
     */
    public Point getPosition() {
        return new Point(position);
    }
    
    /**
     * Reset AI player to initial state for a new game
     * @param startPosition New starting position
     */
    public void reset(Point startPosition) {
        this.position = new Point(startPosition);
        this.visited = new boolean[mazeHeight][mazeWidth];
        this.path = new Stack<>();
        this.deadEnds = new HashSet<>();
        
        // Mark starting position as visited and add to path
        visited[position.y][position.x] = true;
        path.push(new Point(position.x, position.y));
    }
}