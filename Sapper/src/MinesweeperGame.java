import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 15;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private int countFlags;
    private boolean isGameStopped;
    private int countClosedTiles=SIDE*SIDE;
    private int score;

    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x, y);
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        if(isGameStopped==true) restart();
        else
            openTile(x,y);
    }

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                setCellValue(x,y,"");
                boolean isMine = getRandomNumber(10) < 2;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.AQUAMARINE);
            }
        }
        countMineNeighbors();
        countFlags=countMinesOnField;
        score=0;
    }

    private void markTile(int x, int y) {
        if (isGameStopped == false){
            if (gameField[y][x].isOpen == true || (countFlags == 0 && gameField[y][x].isFlag == false)) {
            } else {
                if (gameField[y][x].isFlag == false) {
                    gameField[y][x].isFlag = true;
                    countFlags--;
                    setCellValue(x, y, FLAG);
                    setCellColor(x, y, Color.YELLOW);
                } else {
                    gameField[y][x].isFlag = false;
                    countFlags++;
                    setCellColor(x, y, Color.AQUAMARINE);
                    setCellValue(x, y, "");
                }

            }
        }
        else;
    }

    private void openTile(int x, int y) {

        if(gameField[y][x].isOpen==true || gameField[y][x].isFlag==true || isGameStopped==true) return;
        if (gameField[y][x].isMine == true) {
            setCellValueEx(x,y,Color.RED,MINE);
            gameOver();
            return;
        }
        gameField[y][x].isOpen = true;
        countClosedTiles--;
        setCellColor(x, y, Color.GREEN);
        if (gameField[y][x].isMine == true) {
            setCellValue(x, y, MINE);
        } else {
            if (gameField[y][x].countMineNeighbors != 0) {
                setCellNumber(x, y, gameField[y][x].countMineNeighbors);
            } else setCellValue(x, y, "");
        }
        if (gameField[y][x].countMineNeighbors == 0 && gameField[y][x].isMine == false) {
            List<GameObject> list = getNeighbors(gameField[y][x]);
            for (GameObject v : list) {
                if (v.isOpen == false) {
                    openTile(v.x, v.y);
                }
            }
        }
        if(gameField[y][x].isOpen==true && gameField[y][x].isMine==false) score+=5;
        setScore(score);
        if(countClosedTiles==countMinesOnField && gameField[y][x].isMine==false) win();
    }

    private void restart(){
        isGameStopped=false;
        countClosedTiles=SIDE*SIDE;
        countMinesOnField=0;
        score=0;
        setScore(score);
        createGame();

    }

    private void gameOver(){
        isGameStopped=true;
        showMessageDialog(Color.BLACK, "Game Over", Color.RED, 70);
    }

    private void win(){
        isGameStopped=true;
        showMessageDialog(Color.ORANGE, "You Win!!!", Color.GREENYELLOW, 70);
    }

    private void countMineNeighbors(){
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                if(gameField[y][x].isMine==false) {
                    int count = 0;
                    List<GameObject> list = getNeighbors(gameField[y][x]);
                    for (GameObject i : list) {
                        if (i.isMine == true) count++;
                    }
                    gameField[y][x].countMineNeighbors = count;
                }
            }
        }
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }
}
