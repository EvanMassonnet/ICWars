package ch.epfl.cs107.play.game.icwars.actor.unit.action;

import ch.epfl.cs107.play.game.actor.Graphics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.icwars.actor.players.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.window.Keyboard;

public abstract class Action implements Graphics {

    protected final Area area;
    protected final Unit unit;
    private final String name;
    private final int key;

    protected boolean actionFinished;

    /**
     * Default action constructor
     * @param area (Area)
     * @param unit (Unit) unit on whom the action is attached
     * @param name (String) name of the action
     * @param key  (int) keyboard key to trigger the action
     */
    public Action(Area area, Unit unit, String name, int key){
        this.area = area;
        this.unit = unit;
        this.name = name;
        this.key = key;
        resetAction();
    }

    /**
     * main function of the action
     * @param dt (float) time step
     * @param player (ICWarsPlayer) player on whom the action is attached
     * @param keyboard (Keyboard) input to read
     */
    public abstract void doAction(float dt, ICWarsPlayer player , Keyboard keyboard);

    /**
     * Getter for the action name
     * @return (String)
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the action key
     * @return (int)
     */
    public int getKey() {
        return key;
    }

    /**
     * return true if the action is finished
     * @return (Boolean)
     */
    public boolean isFinished(){
        return actionFinished;
    }

    /**
     * Reset the action state
     */
    public void resetAction(){
        actionFinished = false;
    }
}
