import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class FillinPuzzle {

	int[][] counts;
	Stack<Placeholder> placeholders;
	char[][] grid;
	List<Set<String>> words;
	int numberOfColumns;
	int numberOfRows;
	int numberOfWords;
	int attempts = 0;

	/**
	 * to take input from the user and create a puzzle
	 * 
	 * @param stream
	 * @return true if the puzzle is loaded according to the input provided
	 */
	public boolean loadPuzzle(BufferedReader stream) {
		try {
			BufferedReader br = stream;
			// to get size and number of words of the grid
			String line = br.readLine();
			String[] gridValues = line.split(" ");
			numberOfColumns = Integer.parseInt(gridValues[0]);
			numberOfRows = Integer.parseInt(gridValues[1]);
			numberOfWords = Integer.parseInt(gridValues[2]);

			grid = new char[numberOfRows + 2][numberOfColumns + 2];
			// to initialize the 2-d array and assign blank value to each position
			for (int i = numberOfRows + 1; i >= 0; i--) {
				for (int j = 0; j <= numberOfColumns + 1; j++) {
					grid[i][j] = ' ';
				}
			}
			// to assign # where we need to add values
			for (int i = 0; i < Integer.valueOf(gridValues[2]); i++) {
				String[] crossword = br.readLine().split(" ");
				if (crossword[3].equals("h")) {
					int counter = Integer.valueOf(crossword[2]);
					int rightMovement = Integer.valueOf(crossword[0]) + 1;
					while (counter != 0) {
						// insert values horizontally left to right
						grid[Integer.valueOf(crossword[1]) + 1][rightMovement] = '#';
						rightMovement++;
						counter--;
					}
				} else if (crossword[3].equals("v")) {
					int counter = Integer.valueOf(crossword[2]);
					int bottomMovement = Integer.valueOf(crossword[1]) + 1;
					while (counter != 0) {
						// insert values vertically top to bottom
						grid[bottomMovement][Integer.valueOf(crossword[0]) + 1] = '#';
						bottomMovement--;
						counter--;
					}
				} else {
					return false;
				}
			}
			for (int i = 0; i < (grid.length / 2); i++) {
				char[] temp = grid[i];
				grid[i] = grid[grid.length - i - 1];
				grid[grid.length - i - 1] = temp;
			}

			// to take input as words
			words = new ArrayList<>();
			counts = new int[numberOfRows + 2][numberOfColumns + 2];
			for (int i = 2; i <= 20; i++)
				words.add(new HashSet<String>());
			for (int i = 0; i < numberOfWords; i++) {
				line = br.readLine();
				words.get(line.length()).add(line);
			}

			// Iterate over horizontal and vertical positions
			placeholders = new Stack<>();
			for (int i = 1; i <= numberOfRows; i++)
				for (int j = 1; j <= numberOfColumns; j++)
					if (grid[i][j - 1] == ' ' && grid[i][j] == '#' && grid[i][j + 1] == '#') {
						int xx = j;
						int ll = 0;
						while (grid[i][j] == '#') {
							ll++;
							j++;
						}
						placeholders.push(new Placeholder(xx, i, ll, true));
					}

			for (int j = 1; j <= numberOfColumns; j++)
				for (int i = 1; i <= numberOfRows; i++)
					if (grid[i - 1][j] == ' ' && grid[i][j] == '#' && grid[i + 1][j] == '#') {
						int yy = i;
						int ll = 0;
						while (grid[i][j] == '#') {
							ll++;
							i++;
						}
						placeholders.push(new Placeholder(j, yy, ll, false));
					}

		} catch (IOException ex) {
			return false;
		}

		return true;

	}

	/**
	 * to call the solvePuzzle method
	 * 
	 * @return
	 */
	public boolean solve() {
		grid = solvePuzzle(grid, placeholders);

		return true;
	}

	/**
	 * to print the final solved crossword puzzle
	 * 
	 * @param outstream
	 */
	public void print(PrintWriter outstream) {
		PrintWriter pr = outstream;
		for (int i = 1; i <= numberOfRows; i++) {
			for (int j = 1; j <= numberOfColumns; j++) {

				pr.print(grid[i][j]);

			}
			pr.println();
			pr.flush();
		}

	}

	/**
	 * to solve the puzzle recursively
	 * 
	 * @param grid
	 * @param placeholders
	 * @return the grid of 2-d array as solved puzzle
	 */
	char[][] solvePuzzle(char[][] grid, Stack<Placeholder> placeholders) {
		if (placeholders.isEmpty())
			return grid;

		Placeholder pl = placeholders.pop();
		for (String word : words.get(pl.l)) {
			if (fillPuzzle(grid, word, pl)) {
				char[][] ret = solvePuzzle(grid, placeholders);
				if (ret != null)
					return ret;
				unfillPuzzle(grid, pl);
			}
		}
		placeholders.push(pl);
		return null;
	}

	/**
	 * to fill the words in the puzzle
	 * 
	 * @param grid
	 * @param word
	 * @param pl
	 * @return true when a word is added horizontally or vertically
	 */
	boolean fillPuzzle(char[][] grid, String word, Placeholder pl) {
		if (pl.h) {
			for (int i = pl.x; i < pl.x + pl.l; i++)
				if (grid[pl.y][i] != '#' && grid[pl.y][i] != word.charAt(i - pl.x))
					return false;
			for (int i = pl.x; i < pl.x + pl.l; i++) {
				grid[pl.y][i] = word.charAt(i - pl.x);
				counts[pl.y][i]++;
			}
			return true;

		} else {
			for (int i = pl.y; i < pl.y + pl.l; i++)
				if (grid[i][pl.x] != '#' && grid[i][pl.x] != word.charAt(i - pl.y))
					return false;
			for (int i = pl.y; i < pl.y + pl.l; i++) {
				grid[i][pl.x] = word.charAt(i - pl.y);
				counts[i][pl.x]++;
			}
			return true;
		}
	}

	/**
	 * to check number of attempts solving the puzzle
	 * 
	 * @return number of attempts in solving the puzzle
	 */
	public int choices() {
		return attempts;
	}

	/**
	 * to remove words from the puzzle
	 * 
	 * @param grid
	 * @param pl
	 */
	void unfillPuzzle(char[][] grid, Placeholder pl) {

		if (pl.h) {
			for (int i = pl.x; i < pl.x + pl.l; i++)
				if (--counts[pl.y][i] == 0)
					grid[pl.y][i] = '#';
		} else {
			for (int i = pl.y; i < pl.y + pl.l; i++)
				if (--counts[i][pl.x] == 0)
					grid[i][pl.x] = '#';
		}
		attempts++;
	}

}
