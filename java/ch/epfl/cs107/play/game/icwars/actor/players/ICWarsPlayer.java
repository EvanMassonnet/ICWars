package ch.epfl.cs107.play.game.icwars.actor.players;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActor;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.game.icwars.area.ICWarsBehavior;
import ch.epfl.cs107.play.game.icwars.handler.ICWarsInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class ICWarsPlayer extends ICWarsActor implements Interactor {

    protected State playerState;
    protected List<Unit> units;
    protected List<Unit> usedUnits;
    protected List<Unit> deadUnits;

    //gui info
    protected Unit selectedUnit;
    protected Unit unitOver;
    protected ICWarsBehavior.ICWarsCellType cellTypeOver;

    //interaction handler
    private final ICWarsPlayerInteractionHandler handler;

    public enum State{
        IDLE,
        NORMAL,
        SELECT_CELL,
        MOVE_UNIT,
        ACTION_SELECTION,
        ACTION
    }

    /**
     * Default constructor for unit
     * @param owner (Area)
     * @param coordinates (DiscreteCoordinates)
     * @param faction (Faction)
     * @param units (Unit) ellipse
     */
    public ICWarsPlayer(Area owner, DiscreteCoordinates coordinates, Faction faction, List<Unit> units){
        super(owner, coordinates, faction);
        this.units = units;
        this.usedUnits = new ArrayList<>();
        this.deadUnits = new ArrayList<>();
        this.handler = new ICWarsPlayerInteractionHandler();

        playerState = State.IDLE;
    }

    /**
     * return true if the player is defeated
     * @return (boolean)
     */
    public boolean isDefeated(){
        return units.isEmpty();
    }

    /**
     * enter all units of the player in the area
     */
    public void enterUnit(){
        for(Unit unit : units){
            unit.enterArea(getOwnerArea(), unit.getUnitSpawnPosition());
            ((ICWarsArea)getOwnerArea()).addUnit(unit);
        }
    }

    /**
     * leave all units of the player in the area
     */
    public void leaveUnit(){
        for(Unit unit : units){
            ((ICWarsArea)getOwnerArea()).removeUnit(unit);
            unit.leaveArea();
        }
        for(Unit unit : deadUnits){
            unit.leaveArea();
        }
    }

    @Override
    public void update(float deltaTime) {
        unitOver = null;                               //used to solve the interaction problem
        super.update(deltaTime);
        int i = 0;
        while(i < units.size()){
            units.get(i).update(deltaTime);
            if(units.get(i).isDead()){
                ((ICWarsArea)getOwnerArea()).removeUnit(units.get(i));
                deadUnits.add(units.get(i));                                    //if a unit is dead, add it to the dead unit list but don't remove it from the game
                units.remove(i);
                --i;
            }
            ++i;
        }
    }

    /**
     * Leave an area by unregister this player and all its units
     */
    public void leaveArea(){
        leaveUnit();
        units.clear();
        deadUnits.clear();
        selectedUnit = null;
        getOwnerArea().unregisterActor(this);
    }

    /**
     * Center the camera on the player
     */
    public void centerCamera() {
        getOwnerArea().setViewCandidate(this);
    }

    @Override
    public boolean takeCellSpace() {
        return false;
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }

    @Override
    public void draw(Canvas canvas) {
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
    }

    /**
     * Initialise the turn of the player
     */
    public void startTurn(){
        playerState = State.NORMAL;
        readyUnits();
        centerCamera();
        if(usedUnits != null){
            usedUnits.clear();
        }
    }

    /**
     * Reset all the unit of the player for the next round
     */
    public void readyUnits() {
        for (Unit u : units){
            u.setAlreadyUsed(false);
            u.setVisibility(1.f);
        }
    }

    /**
     * Getter for the player state
     * @return (State)
     */
    public State getPlayerState(){
        return playerState;
    }

    @Override
    public void onLeaving(List<DiscreteCoordinates> coordinates) {
        super.onLeaving(coordinates);
        cellTypeOver = null;
    }

    @Override
    public void onEntering(List<DiscreteCoordinates> coordinates) {
        super.onEntering(coordinates);
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return null;
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        return false;
    }

    @Override
    public void interactWith(Interactable other) {
        other.acceptInteraction(handler);
    }


    private class ICWarsPlayerInteractionHandler implements ICWarsInteractionVisitor {
        @Override
        public void interactWith(Unit unit) {
            //TODO solve problem when leave a cell
            //Problem from AreaEntity : use math.round for discrete coordinates, leave and enter are run at the same time and player keeps interacting with the previous cell
            unitOver = unit;
            if(unit.getFaction() == getFaction() && playerState == State.SELECT_CELL){
                if(!usedUnits.contains(unit)){
                    selectedUnit = unit;
                }
            }
        }

        @Override
        public void interactWith(ICWarsBehavior.ICWarsCell cell) {
            cellTypeOver = cell.getType();

        }
    }
}

