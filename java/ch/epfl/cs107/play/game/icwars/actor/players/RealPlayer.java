package ch.epfl.cs107.play.game.icwars.actor.players;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.actor.unit.action.Action;
import ch.epfl.cs107.play.game.icwars.gui.ICWarsEndGamePanel;
import ch.epfl.cs107.play.game.icwars.gui.ICWarsPlayerGUI;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.List;

public class RealPlayer extends ICWarsPlayer {

    private final Sprite sprite;
    private final static int MOVE_DURATION = 8;

    /// gui
    private final ICWarsPlayerGUI gui;
    private final ICWarsEndGamePanel endGameScreen;

    private Action selectedAction;

    private boolean gameFinish;

    /**
     * Default real player constructor
     * @param owner (Area)
     * @param coordinates (DiscreteCoordinates)
     * @param faction (Faction)
     * @param units (Unit) ellipse
     */
    public RealPlayer(Area owner, DiscreteCoordinates coordinates, Faction faction, List<Unit> units) {
        super(owner, coordinates, faction, units);

        gui = new ICWarsPlayerGUI(owner.getCameraScaleFactor(), this);
        endGameScreen = new ICWarsEndGamePanel(owner.getCameraScaleFactor(), this, getOwnerArea());

        String spriteName = (faction == Faction.ALLIED) ? "icwars/allyCursor" : "icwars/enemyCursor";
        sprite = new Sprite(spriteName, 1.f, 1.f,this);

        gameFinish = false;
    }

    @Override
    public void update(float deltaTime) {

        Keyboard keyboard= getOwnerArea().getKeyboard();
        if(playerState == State.NORMAL || playerState == State.SELECT_CELL || playerState == State.MOVE_UNIT ){
            moveIfPressed(Orientation.LEFT, keyboard.get(Keyboard.LEFT));
            moveIfPressed(Orientation.UP, keyboard.get(Keyboard.UP));
            moveIfPressed(Orientation.RIGHT, keyboard.get(Keyboard.RIGHT));
            moveIfPressed(Orientation.DOWN, keyboard.get(Keyboard.DOWN));
        }

        switch (playerState){
            case IDLE :
                break;
            case NORMAL:
                if(keyboard.get(Keyboard.ENTER).isReleased()){
                    playerState = State.SELECT_CELL;
                }else if(keyboard.get(Keyboard.TAB).isPressed() || usedUnits.size() == units.size()){
                    playerState = State.IDLE;
                }
                break;
            case SELECT_CELL:
                if(selectedUnit != null && !selectedUnit.isAlreadyUsed()) {
                    playerState = State.MOVE_UNIT;
                }else{
                    playerState = State.NORMAL;
                }
                break;
            case MOVE_UNIT:
                if(keyboard.get(Keyboard.ENTER).isReleased()){
                    if(selectedUnit.changePosition(getCurrentMainCellCoordinates())){
                        usedUnits.add(selectedUnit);
                        if(selectedUnit.getActionsList().isEmpty()){
                            selectedUnit.setAlreadyUsed(true);
                            selectedUnit = null;
                            playerState = State.NORMAL;
                        }else{
                            playerState = State.ACTION_SELECTION;
                        }
                    }
                }
                break;
            case ACTION_SELECTION:
                for(Action action : selectedUnit.getActionsList()){
                    if(keyboard.get(action.getKey()).isPressed()){
                        selectedAction = action;
                        playerState = State.ACTION;
                    }
                }
                if (keyboard.get(Keyboard.TAB).isPressed()){
                    selectedUnit.setAlreadyUsed(true);
                    selectedUnit = null;
                    playerState = State.NORMAL;
                }
                break;
            case ACTION:
                if(selectedAction == null || selectedAction.isFinished()){
                    if(selectedUnit.getUnitState() == Unit.UnitState.IDLE){
                        selectedUnit.setAlreadyUsed(true);
                        selectedAction.resetAction();
                        selectedUnit = null;
                        selectedAction = null;
                        playerState = State.NORMAL;
                    }
                }else{
                    selectedAction.doAction(0, this, keyboard);
                }
                break;
        }
        super.update(deltaTime);
    }

    @Override
    public void draw(Canvas canvas) {
        if(gameFinish){
            endGameScreen.draw(canvas);     // draw the end game panel
        }else{
            gui.draw(canvas, selectedUnit, unitOver, cellTypeOver);
            if(playerState != State.IDLE){
                sprite.draw(canvas);
            }
        }
    }


    /**
     * Orientate and Move this player in the given orientation if the given button is down
     * @param orientation (Orientation): given orientation, not null
     * @param b (Button): button corresponding to the given orientation, not null
     */
    private void moveIfPressed(Orientation orientation, Button b){
        if(b.isDown()) {
            if (!isDisplacementOccurs()) {
                orientate(orientation);
                move(MOVE_DURATION);
            }
        }
    }

    /**
     * Setter to draw the end game panel
     * @param gameFinish (boolean)
     */
    public void setGameFinish(boolean gameFinish) {
        this.gameFinish = gameFinish;
    }
}
