package hyperskill;

import static java.lang.Math.abs;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.hyperskill.hstest.stage.StageTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.hyperskill.hstest.testcase.TestCase;

class Grid {

    char[][] rows;
    Grid(String[] rows) throws Exception {
        this.rows = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            this.rows[i] = rows[i].toCharArray();
            for (char c : this.rows[i]) {
                if (c != '/'
                    && c != 'x'
                    && c != '.'
                    && c != '*'
                    && !(c >= '0' && c <= '9')) {
                    throw new Exception(
                        "A row of the grid should contain " +
                            "'/', 'X', '.' or '*' or numbers. \n" +
                            "Found: '" + c + "' in row \"" + rows[i] + "\""
                    );
                }
            }
        }
    }

    @Override
    public String toString() {
        String res = "";
        for (int i = 0; i < this.rows.length; i++) {
            res += new String(this.rows[i]) + "\n";
        }
        return res.trim();
    }

    int count(char c) {
        int sum = 0;
        for (char[] row : rows) {
            for (char ch : row) {
                sum += ch == c ? 1 : 0;
            }
        }
        return sum;
    }

    int countAround(int x, int y, char c) {
        int[] around = new int[] {-1, 0, 1};
        int count = 0;
        for (int dx : around) {
            for (int dy : around) {

                int newX = x + dx;
                int newY = y + dy;

                if (1 <= newX && newX <= 9 &&
                    1 <= newY && newY <= 9) {
                    if (get(newX, newY) == c) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    int distanceToCenter(int x, int y) {
        return abs(x - 5) + abs(y - 5);
    }

    void replaceAround(int x, int y, char from, char to) {
        int[] around = new int[] {-1, 0, 1};
        int count = 0;
        for (int dx : around) {
            for (int dy : around) {

                int newX = x + dx;
                int newY = y + dy;

                if (1 <= newX && newX <= 9 &&
                    1 <= newY && newY <= 9) {
                    if (get(newX, newY) == from) {
                        set(newX, newY, to);
                    }
                }
            }
        }
    }

    char get(int x, int y) {
        return rows[y-1][x-1];
    }

    void set(int x, int y, char c) {
        rows[y-1][x-1] = c;
    }

    Grid copy() {
        String[] rows = new String[this.rows.length];
        for (int i = 0; i < this.rows.length; i++) {
            rows[i] = new String(this.rows[i]);
        }
        try {
            return new Grid(rows);
        } catch (Exception ex) {
            return null;
        }
    }

    int differences(Grid other) {
        int diff = 0;
        for (int x = 1; x <= 9; x++) {
            for (int y = 1; y <= 9; y++) {
                diff += get(x, y) != other.get(x, y) ? 1 : 0;
            }
        }
        return diff;
    }

    void checkField(boolean withRealMines) throws Exception {
        for (int x = 1; x <= 9; x++) {
            for (int y = 1; y <= 9; y++) {
                char c = get(x, y);
                if (!withRealMines && c == 'x') {
                    throw new Exception(
                        "The word \"failed\" was not found, " +
                            "but the last grid contains 'X' characters. " +
                            "This should not be the case."
                    );
                }
                if (c == '/') {
                    int dotsAround = countAround(x, y, '.');
                    if (dotsAround != 0) {
                        throw new Exception(
                            "The last grid contains '.' and '/' " +
                                "characters that are next to each other. " +
                                "This situation is impossible."
                        );
                    }
                    if (withRealMines) {
                        int minesAround = countAround(x, y, 'x');
                        if (minesAround != 0) {
                            throw new Exception(
                                "The last grid contains 'X' and '/' " +
                                    "characters that are next to each other. " +
                                    "This situation is impossible."
                            );
                        }
                    }
                }
                if (c >= '1' && c <= '9') {
                    int num = c - '0';
                    int freePlacesAround =
                        countAround(x, y, '.') +
                            countAround(x, y, '*');

                    if (withRealMines) {
                        freePlacesAround += countAround(x, y, 'x');
                    }

                    if (num > freePlacesAround) {
                        throw new Exception(
                            "There is a number " + num + " in the last grid, " +
                                "but there are fewer free fields " +
                                "around which to put a mine. " +
                                "This situation is impossible."
                        );
                    }
                }
                if (c == '*') {
                    int guaranteedEmptyAround = countAround(x, y, '/');
                    if (guaranteedEmptyAround != 0) {
                        throw new Exception(
                            "The last grid contains '*' and '/' " +
                                "characters that are next to each other. " +
                                "This situation is impossible. If there is " +
                                "'*' character that is " +
                                "next to '/' it should be replaced to '/' " +
                                "or to a number."
                        );
                    }
                }
            }
        }
    }

    void checkMiddleGame() throws Exception {
        checkField(false);
    }

    void checkFail() throws Exception {
        checkField(true);
    }

    static List<Grid> parse(String output) throws Exception {

        output = output.replaceAll("\u2502", "|");
        output = output.replaceAll("—", "-");

        List<Grid> grids = new LinkedList<>();
        String[] lines = output.split("\n");

        boolean gridStarted = false;
        List<String> newGrid = new LinkedList<>();
        for (String line : lines) {
            line = line.trim();
            if (line.contains("-|--")) {
                gridStarted = !gridStarted;
                if (gridStarted) {
                    newGrid = new LinkedList<>();
                } else {
                    if (newGrid.size() != 9) {
                        throw new Exception(
                            "Found grid that contains " + newGrid.size() +
                                " but grid should contain 9 lines. \n" +
                                "The tests assume that the grid is " +
                                "between the lines containing the line \"-\u2502--\"."
                        );
                    }
                    grids.add(
                        new Grid(newGrid.toArray(new String[0]))
                    );
                }
                continue;
            }
            if (gridStarted) {

                char toFind = '|';

                long countBrackets =
                    line.chars().filter(c -> c == toFind).count();

                if (countBrackets != 2) {
                    throw new Exception(
                        "Grid should contain " +
                            "two '|' symbols, at the beginning " +
                            "(after row number) " +
                            "and at the end of the row. \n" +
                            "Your line: \"" + line + "\"."
                    );
                }

                int first = line.indexOf(toFind) + 1;
                int second = line.indexOf(toFind, first);

                int rowSize = second - first;

                if (rowSize != 9) {
                    throw new Exception(
                        "Every row of the grid should contain " +
                            "9 symbols between '|' chars. \nThis line has " +
                            rowSize + " symbols: \"" + line + "\"."
                    );
                }

                String row = line.substring(first, second);

                newGrid.add(row);
            }
        }

        return grids;
    }

}

class Coords {
    int x;
    int y;
    Coords(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

enum FirstPressStatus {
    NOT_PRESSED_FREE, PRESSED_FREE, VERIFIED_OK
}

enum Action {
    NONE, MINE, FREE
}

class State {
    int minesCount = 0;
    FirstPressStatus isStart = FirstPressStatus.NOT_PRESSED_FREE;
    List<Coords> marks = new ArrayList<>();

    int actionX = 0;
    int actionY = 0;
    Action lastAction = Action.NONE;
    char lastCharAtCoords = '\0';
    String fullAction = null;
}

public class MinesweeperTest extends StageTest<State> {

    @Override
    public List<TestCase<State>> generate() {
        List<TestCase<State>> tests = new ArrayList<>();

        for (int i = 1; i < 70; i += i < 10 ? 1 : 5) {
            for (int j = 0; j < (i < 5 ? 20 : 2); j++) {
                State state = new State();
                state.minesCount = i;
                tests.add(new TestCase<State>()
                    .addInput("" + i)
                    .addInfInput(out -> createDynamicInput(out, state))
                    .setAttach(state)
                );
            }
        }
        return tests;
    }

    private Object createDynamicInput(String out, State state) {
        out = out.trim().toLowerCase();

        List<Grid> grids;
        try {
            grids = Grid.parse(out);
        } catch (Exception ex) {
            return CheckResult.wrong(ex.getMessage());
        }

        if (grids.size() == 0) {
            return CheckResult.wrong(
                "Cannot find a field after the last input. Make sure you output " +
                    "this field using '|' and '-' characters."
            );
        }

        Grid grid = grids.get(0);

        state.marks.removeIf(elem -> {
            char c = grid.get(elem.x, elem.y);
            boolean isGuaranteedEmptyNow = c == '/';
            boolean isNumberNow = c >= '1' && c <= '9';
            boolean isFailed = c == 'x';
            return isGuaranteedEmptyNow || isNumberNow || isFailed;
        });

        boolean isFailed = out.contains("failed");
        boolean isWin = out.contains("congratulations");

        int starCount = grid.count('*');
        int shouldBeStars = state.marks.size();
        if (starCount != shouldBeStars && !isFailed && !isWin) {
            return CheckResult.wrong(
                "There should be " + shouldBeStars + " '*' " +
                    "symbol" + (starCount > 1? "s": "") + " in the last " +
                    "grid. Found: " + starCount
            );
        }

        if (state.lastAction != Action.NONE) {
            int x = state.actionX;
            int y = state.actionY;
            int oldCell = state.lastCharAtCoords;
            int newCell = grid.get(x, y);

            if (oldCell == newCell) {
                return CheckResult.wrong(
                    "Grid's cell at coordinates \"" + x + " " + y + "\" " +
                        "didn't changed after action \"" + state.fullAction + "\"");
            }

            if (state.lastAction == Action.MINE) {
                if (oldCell == '.' && newCell != '*') {
                    return CheckResult.wrong(
                        "Grid's cell at coordinates \"" + x + " " + y + "\" " +
                            "should be equal to \"*\"");
                } else if (oldCell == '*' && newCell != '.') {
                    return CheckResult.wrong(
                        "Grid's cell at coordinates \"" + x + " " + y + "\" " +
                            "should be equal to \".\"");
                }

            } else if (state.lastAction == Action.FREE) {
                if (newCell != '/' && newCell != 'x' && !(newCell >= '0' && newCell <= '9')) {
                    return CheckResult.wrong(
                        "Grid's cell at coordinates \"" + x + " " + y + "\" " +
                            "should be equal to \"x\", \"/\" or to a number");
                }
            }
        }

        if (isFailed) {
            if (state.isStart != FirstPressStatus.VERIFIED_OK) {
                return CheckResult.wrong(
                    "The user should not lose after the first \"free\" move."
                );
            }
            try {
                grid.checkFail();
                int minesCount = grid.count('x');
                if (minesCount != state.minesCount) {
                    return CheckResult.wrong(
                        "There " + (minesCount > 1? "are" : "is") +
                            " " + minesCount + " mine" + (minesCount > 1? "s": "") +
                            " in the last grid marked 'X'. " +
                            "But initially the user " +
                            "entered " + state.minesCount + " mine" +
                            (state.minesCount > 1? "s": "") +". " +
                            "Every real mine should be marked as 'X' at the end " +
                            "in case of failure."
                    );
                }
                return CheckResult.correct();
            } catch (Exception ex) {
                return CheckResult.wrong(ex.getMessage());
            }
        }

        if (state.isStart == FirstPressStatus.PRESSED_FREE) {
            state.isStart = FirstPressStatus.VERIFIED_OK;
        }

        try {
            grid.checkMiddleGame();
        } catch (Exception ex) {
            return CheckResult.wrong(ex.getMessage());
        }

        if (isWin) {
            int freeCellsCount = grid.count('.') + grid.count('*');
            if (freeCellsCount != state.minesCount &&
                state.marks.size() != state.minesCount) {
                return CheckResult.wrong(
                    "The word \"congratulations\" was found, " +
                        "but not every mine was found. \n" +
                        "Mines to find: " + state.minesCount + "\n" +
                        "Free cells left: " + freeCellsCount

                );
            }
            return CheckResult.correct();
        }

        Random random = new Random();

        int dotsCount = grid.count('.');

        if (starCount != 0 && (random.nextInt(4) == 0 || dotsCount == 0)) {
            int nextMine = random.nextInt(state.marks.size());
            Coords mineToRemove = state.marks.get(nextMine);
            state.marks.remove(mineToRemove);
            int x = mineToRemove.x;
            int y = mineToRemove.y;

            String fullAction = x + " " + y + " mine";

            state.actionX = x;
            state.actionY = y;
            state.lastAction = Action.MINE;
            state.lastCharAtCoords = grid.get(x, y);
            state.fullAction = fullAction;
            return fullAction;
        }

        if (dotsCount == 0) {
            return CheckResult.wrong(
                "There are no '.' cells in the field, " +
                    "but the game is not over. Something is wrong."
            );
        }

        while (true) {
            int x = 1 + random.nextInt(9);
            int y = 1 + random.nextInt(9);

            char c = grid.get(x, y);
            if (c == '.') {
                boolean isMine = random.nextInt(3) == 0;
                if (isMine) {
                    state.marks.add(new Coords(x, y));

                    String fullAction = x + " " + y + " mine";

                    state.actionX = x;
                    state.actionY = y;
                    state.lastAction = Action.MINE;
                    state.lastCharAtCoords = '.';
                    state.fullAction = fullAction;
                    return fullAction;
                } else {
                    if (state.isStart == FirstPressStatus.NOT_PRESSED_FREE) {
                        state.isStart = FirstPressStatus.PRESSED_FREE;
                    }

                    String fullAction = x + " " + y + " free";

                    state.actionX = x;
                    state.actionY = y;
                    state.lastAction = Action.FREE;
                    state.lastCharAtCoords = '.';
                    state.fullAction = fullAction;
                    return fullAction;
                }
            }
        }
    }

    @Override
    public CheckResult check(String reply, State attach) {
        reply = reply.toLowerCase();

        try {
            List<Grid> grids = Grid.parse(reply);
            if (grids.size() <= 1) {
                return CheckResult.wrong(
                    "You should output at least 2 grids, found " + grids.size());
            }
        } catch (Exception ex) {
            return CheckResult.wrong(ex.getMessage());
        }

        boolean isFailed = reply.contains("failed");
        boolean isWin = reply.contains("congratulations");

        if (!isFailed && !isWin) {
            return CheckResult.wrong(
                "No words " +
                    "\"congratulations\" or \"failed\" were found. " +
                    "The program must end in one of these ways."
            );
        }

        return CheckResult.correct();
    }
}
