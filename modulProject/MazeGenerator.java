package modulProject;

import java.awt.Point;
import java.util.*;

/**
 * Maze Generator class - Creates random mazes using recursive backtracking algorithm
 * Generates perfect mazes (single solution path with no loops)
 */
public class MazeGenerator {
    private int width;    // Width of the maze grid
    private int height;   // Height of the maze grid
    
    /**
     * Constructor - Initializes maze dimensions
     * @param width Width of the maze
     * @param height Height of the maze
     */
    public MazeGenerator(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    /**
     * Generate a new random maze using depth-first search algorithm
     * @return 2D integer array where 0 = path and 1 = wall
     */
    public int[][] generate() {
        int[][] maze = new int[height][width];
        
        // Initialize all cells as walls
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                maze[i][j] = 1;
            }
        }
        
        // Create maze using DFS (Depth-First Search) recursive backtracking
        Stack<Point> stack = new Stack<>();
        Point start = new Point(1, 1);
        maze[1][1] = 0;  // Mark starting cell as path
        stack.push(start);
        
        // Direction vectors for moving 2 cells at a time (right, down, left, up)
        int[][] dirs = {{0, 2}, {2, 0}, {0, -2}, {-2, 0}};
        Random rand = new Random();
        
        // Continue until all reachable cells are visited
        while (!stack.isEmpty()) {
            Point current = stack.peek();
            List<Point> neighbors = new ArrayList<>();
            
            // Find all unvisited neighbors (2 cells away)
            for (int[] dir : dirs) {
                int ny = current.y + dir[0];
                int nx = current.x + dir[1];
                
                // Check if neighbor is within bounds and still a wall (unvisited)
                if (ny > 0 && ny < height - 1 && nx > 0 && nx < width - 1 && maze[ny][nx] == 1) {
                    neighbors.add(new Point(nx, ny));
                }
            }
            
            if (!neighbors.isEmpty()) {
                // Randomly select a neighbor to visit
                Point next = neighbors.get(rand.nextInt(neighbors.size()));
                // Carve path between current cell and selected neighbor
                maze[(current.y + next.y) / 2][(current.x + next.x) / 2] = 0;
                maze[next.y][next.x] = 0;
                stack.push(next);
            } else {
                // No unvisited neighbors - backtrack
                stack.pop();
            }
        }
        
        // Ensure exit cell is open
        maze[height - 2][width - 2] = 0;
        
        return maze;
    }
    
    /**
     * Get the starting position for the maze
     * @return Starting point coordinates
     */
    public Point getStartPosition() {
        return new Point(1, 1);
    }
    
    /**
     * Get the exit position for the maze
     * @return Exit point coordinates
     */
    public Point getExitPosition() {
        return new Point(width - 2, height - 2);
    }
}