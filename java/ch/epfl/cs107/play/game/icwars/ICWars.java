package ch.epfl.cs107.play.game.icwars;

import ch.epfl.cs107.play.game.actor.SoundAcoustics;
import ch.epfl.cs107.play.game.areagame.AreaGame;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActor;
import ch.epfl.cs107.play.game.icwars.actor.players.AIPlayer;
import ch.epfl.cs107.play.game.icwars.actor.players.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.players.RealPlayer;
import ch.epfl.cs107.play.game.icwars.actor.unit.Soldier;
import ch.epfl.cs107.play.game.icwars.actor.unit.Tank;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.area.*;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ICWars extends AreaGame {

    public enum GameState{
        INIT,
        CHOOSE_PLAYER,
        START_PLAYER_TURN,
        PLAYER_TURN,
        END_PLAYER_TURN,
        END_TURN,
        END
    }

    private GameState gameState;

    public final static float CAMERA_SCALE_FACTOR = 10.f;

    private final String[] areas = {"icwars/Level0", "icwars/Level1", "icwars/Level2", "icwars/Level3"};

    private int areaIndex;

    private List<ICWarsPlayer> playerList;
    private RealPlayer mainPlayer;            //need to defined who is the main player to know wish interface draw

    private List<ICWarsPlayer> playersCurrentRound;
    private List<ICWarsPlayer> playersNextRound;
    private ICWarsPlayer currentPlayer;

    private boolean gameOver;

    private SoundAcoustics mainMusic;


    /**
     * Add all the areas
     */
    private void createAreas(){

        addArea(new Level0());
        addArea(new Level1());
        addArea(new Level2());
        addArea(new Level3());

    }

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            createAreas();
            playerList = new ArrayList<ICWarsPlayer>();
            playersCurrentRound = new ArrayList<ICWarsPlayer>();
            playersNextRound = new ArrayList<ICWarsPlayer>();
            areaIndex = 0;

            mainMusic = new SoundAcoustics("sounds/MainMusic.wav", 0.5f, true, true, true, false);

            initArea(areas[areaIndex]);
            return true;
        }
        return false;
    }



    private void initArea(String areaKey) {

        gameOver = false;
        gameState = GameState.INIT;
        ICWarsArea area = (ICWarsArea)setCurrentArea(areaKey, true);

        area.stopSound();
        area.requestSound(mainMusic);

        /// Allied units
        Unit alliedTank = new Tank(area,area.getAlliedTankSpawnPosition(), ICWarsActor.Faction.ALLIED);
        Unit alliedSoldier = new Soldier(area,area.getAlliedSoldierSpawnPosition(), ICWarsActor.Faction.ALLIED);

        ///Allied player
        RealPlayer ally = new RealPlayer(area, area.getAlliedSpawnPosition(), ICWarsActor.Faction.ALLIED, area.getAlliedUnits());
        ally.enterArea(area, area.getAlliedSpawnPosition());
        ally.enterUnit();
        mainPlayer = ally;
        playerList.add(ally);

        /// Enemy player
        AIPlayer enemy = new AIPlayer(area, area.getEnemySpawnPosition(), ICWarsActor.Faction.ENEMY, area.getEnemyUnits());
        enemy.enterArea(area, area.getEnemySpawnPosition());
        enemy.enterUnit();
        playerList.add(enemy);

    }

    @Override
    public void update(float deltaTime) {

        super.update(deltaTime);

        //keyboard input not linked to a player
        Keyboard keyboard = getWindow().getKeyboard() ;

        if(keyboard.get(Keyboard.N).isPressed() && !gameOver){
            switchArea();
        }else if(keyboard.get(Keyboard.R).isPressed()){
            resetArea();
        }

        switch (gameState){
            case INIT :
                for(ICWarsPlayer player : playerList){
                    if(!player.isDefeated()){
                        playersCurrentRound.add(player);
                    }
                }
                gameState = GameState.CHOOSE_PLAYER;
                break;

            case CHOOSE_PLAYER:
                if(!playersCurrentRound.isEmpty()){
                    currentPlayer = playersCurrentRound.get(0);
                    playersCurrentRound.remove(0);
                    gameState = GameState.START_PLAYER_TURN;
                }else{
                    gameState = GameState.END_TURN;
                }
                break;

            case START_PLAYER_TURN:
                if(currentPlayer.isDefeated()){
                    gameState = GameState.CHOOSE_PLAYER;
                }else{
                    currentPlayer.startTurn();
                    gameState = GameState.PLAYER_TURN;
                }
                break;

            case PLAYER_TURN:
                if(currentPlayer.getPlayerState() == ICWarsPlayer.State.IDLE){
                    gameState = GameState.END_PLAYER_TURN;
                }
                break;
            case END_PLAYER_TURN:
                //TODO: do we need to stop the game if the main player is defeated ?
                if(!currentPlayer.isDefeated()){
                    playersNextRound.add(currentPlayer);
                }
                currentPlayer.readyUnits();
                gameState = GameState.CHOOSE_PLAYER;
                break;
            case END_TURN:
                for(ICWarsPlayer player : playerList){
                    if(player.isDefeated()){
                        playersCurrentRound.remove(player);
                        playersNextRound.remove(player);
                    }
                }
                if(playersNextRound.size()<2){
                    gameState = GameState.END;
                }else{
                    playersCurrentRound.clear();
                    playersCurrentRound.addAll(playersNextRound);
                    playersNextRound.clear();
                    gameState = GameState.CHOOSE_PLAYER;
                }
                break;
            case END:
                //show the menu to the main player
                mainPlayer.setGameFinish(true);

        }
    }

    @Override
    public void end() {
        gameOver = true;
        System.out.println("Game over");
    }

    @Override
    public String getTitle() {
        return "ICWars";
    }

    /**
     * switch to the next area
     */
    private void switchArea() {
        if(areaIndex < areas.length - 1){
            ++areaIndex;
            cleanArea();
            initArea(areas[areaIndex]);
        }else{
            end();
        }
    }

    /**
     * reset the current area
     */
    private void resetArea(){
        cleanArea();
        areaIndex = 0;
        initArea(areas[areaIndex]);
    }

    /**
     * clean every variable of the current area
     */
    private void cleanArea(){
        for(ICWarsPlayer player : playerList){
            if(getCurrentArea().exists(player)){
                player.leaveArea();
            }
        }
        playerList.clear();
        playersCurrentRound.clear();
        playersNextRound.clear();
        currentPlayer = null;
    }

}
