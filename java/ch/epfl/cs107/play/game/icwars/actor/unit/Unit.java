package ch.epfl.cs107.play.game.icwars.actor.unit;

import ch.epfl.cs107.play.game.NameGenerator;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.*;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActor;
import ch.epfl.cs107.play.game.icwars.actor.unit.action.Action;
import ch.epfl.cs107.play.game.icwars.area.ICWarsBehavior;
import ch.epfl.cs107.play.game.icwars.area.ICWarsRange;
import ch.epfl.cs107.play.game.icwars.handler.ICWarsInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Unit extends ICWarsActor implements Interactor {

    /// name of the unit, used by ICWarsPlayerGUI
    private final String name;
    /// current hp, less or equal to MAX_HP
    private int hp;
    private final int MAX_HP;
    private final int attack;
    private final int radius;
    /// defense start of the current cell

    private ICWarsBehavior.ICWarsCell currentCell;      //know the cell instead of just the defense star to use cell effect
    private int damageEffect;                           //additional damage received effect, could be negative

    private final DiscreteCoordinates unitSpawnPosition;

    /// list of actions the unit can do
    protected List<Action> actionsList;

    private boolean alreadyUsed;
    private UnitState unitState;

    private final ICWarsUnitInteractionHandler handler;

    /// a unit must have at least one sprite
    protected Sprite idleSprite;

    private ICWarsRange range;

    /// path to the next position
    private Queue<Orientation> path;

    protected Unit isAttacking;

    /**
     * enum used to describe the animation state of the unit
     */
    public enum UnitState{
        IDLE,
        WALK,
        ATTACK,
        DEATH,
    }

    /**
     * Default Unit constructor
     * @param owner (Area): Owner area. Not null
     * @param coordinates (DiscreteCoordinates): Initial spawn position of the entity in the Area. Not null
     * @param faction (Faction): Faction of the unit, ally or enemy
     * @param maxHp (int): maximum hp of this type of unit
     * @param attack (int): damage per attack
     * @param radius (int): radius of displacement and attack
     */
    public Unit(Area owner, DiscreteCoordinates coordinates, Faction faction,int maxHp,int attack, int radius) {

        super(owner, coordinates, faction);
        this.unitSpawnPosition = coordinates;

        if(faction == Faction.ALLIED){      //select a random name for the unit
            this.name = NameGenerator.RandomPlayer();       //if allied , it's  a student
        }else{
            this.name = NameGenerator.RandomTeacher();      //if enemy, it's a teacher or an assistant
        }

        this.hp = maxHp;
        this.MAX_HP = maxHp;
        this.attack = attack;
        this.radius = radius;
        this.damageEffect = 0;

        this.handler = new ICWarsUnitInteractionHandler();
        this.actionsList = new ArrayList<Action>();

        this.unitState = UnitState.IDLE;
        this.alreadyUsed = false;


    }

    /**
     * Getter for the action list of the unit
     * @return (List<Action>)
     */
    public List<Action> getActionsList() {
        return actionsList;
    }

    /**
     * Setter to change the alpha of the unit and set it used
     * @param used (boolean)
     */
    public void setAlreadyUsed(boolean used){
        if (used){
            setVisibility(0.5f);
        }
        this.alreadyUsed = used;
    }

    /**
     * Getter to know if the unit is used
     * @return (boolean)
     */
    public boolean isAlreadyUsed() {
        return alreadyUsed;
    }

    /**
     * initialise the range of the unit base on its current position and radius
     */
    private void initRange(){

        range  = new ICWarsRange();
        boolean left;
        boolean right;
        boolean up;
        boolean down;

        for(int x = -radius + getCurrentMainCellCoordinates().x; x <= radius + getCurrentMainCellCoordinates().x; ++x){
            for(int y = -radius + getCurrentMainCellCoordinates().y; y <= radius + getCurrentMainCellCoordinates().y; ++y){
                if(getOwnerArea().canEnterAreaCells(this, List.of(new DiscreteCoordinates(x, y))) || (x==getCurrentMainCellCoordinates().x && y==getCurrentMainCellCoordinates().y)){
                    left = true;
                    right = true;
                    up = true;
                    down = true;
                    if (x == -radius + getCurrentMainCellCoordinates().x || (!getOwnerArea().canEnterAreaCells(this, List.of(new DiscreteCoordinates(x-1, y))) && !(x-1==getCurrentMainCellCoordinates().x && y==getCurrentMainCellCoordinates().y))) {
                        left = false;
                    }
                    if (x == radius + getCurrentMainCellCoordinates().x || (!getOwnerArea().canEnterAreaCells(this, List.of(new DiscreteCoordinates(x+1, y))) && !(x+1==getCurrentMainCellCoordinates().x && y==getCurrentMainCellCoordinates().y))) {
                        right = false;
                    }
                    if (y == -radius + getCurrentMainCellCoordinates().y || (!getOwnerArea().canEnterAreaCells(this, List.of(new DiscreteCoordinates(x, y-1))) && !(x==getCurrentMainCellCoordinates().x && y-1==getCurrentMainCellCoordinates().y))) {
                        down = false;
                    }
                    if (y == radius + getCurrentMainCellCoordinates().y || (!getOwnerArea().canEnterAreaCells(this, List.of(new DiscreteCoordinates(x, y+1))) && !(x==getCurrentMainCellCoordinates().x && y+1==getCurrentMainCellCoordinates().y))) {
                        up = false;
                    }
                    range.addNode(new DiscreteCoordinates(x, y), left, up, right, down);
                }
            }
        }
    }

    /**
     * Getter for the unit name
     * @return (String)
     */
    public String getName(){
        return this.name;
    }

    /**
     * Getter for the radius
     * @return (int)
     */
    public int getRadius() {
        return radius;
    }

    /**
     * Getter for the hp
     * @return (int)
     */
    public int getHp() {
        return hp;
    }

    /**
     * Getter for the defense star of the current cell
     * @return (int)
     */
    public int getDefense() {
        return currentCell.getDefenseStar();
    }

    /**
     * Return true if the unit is dead
     * @return (boolean)
     */
    public boolean isDead(){
        return (hp == 0);
    }

    /**
     * applies a bonus damage on the unit
     * @param damageEffect (int) the damage effect could be positive or negative but can't heal
     */
    public void applyDamageEffect(int damageEffect){
        this.damageEffect = damageEffect;
    }

    /**
     * applies damage on the unit
     * @param damage (int) the damage could be positive or negative
     */
    public void ReceiveDamage(int damage){
        damage = Math.max(Math.abs(damage) + damageEffect, 0);
        if(hp > damage){
            hp -= damage;
        }else{
            hp = 0;
        }

    }

    /**
     * Repair the unit of a number of point
     * @param repair (int) the damage could be positive or negative
     */
    public void Repair(int repair){
        if(hp + repair < MAX_HP){
            hp += repair;
        }else{
            hp = MAX_HP;
        }
    }

    /**
     * Getter of the unit damage
     * @return (int)
     */
    public int getDamage(){
        return attack;
    }

    @Override
    public void update(float deltaTime) {

        switch (unitState){
            case IDLE:
                if(isDead()){
                    unitState = UnitState.DEATH;
                }else if(isDisplacementOccurs() || (path != null && !path.isEmpty())){
                    unitState = UnitState.WALK;
                }
                break;
            case WALK:
                if(path != null && !path.isEmpty()){
                    moveUnit();
                }else if(!isDisplacementOccurs()){
                    unitState = UnitState.IDLE;
                }
                break;
            case ATTACK:
                break;
            case DEATH:
                break;
        }

        super.update(deltaTime);

    }

    /**
     * Getter for the unit state
     * @return (UnitState)
     */
    public UnitState getUnitState() {
        return unitState;
    }

    /**
     * methode to draw attack annimation on an enemy
     * @param unit wish unit to attack
     */
    public void attackUnitAnimation(Unit unit){
        unitState = UnitState.ATTACK;
        isAttacking = unit;
    }

    /**
     * when the attack is finish, return to the Idle state
     */
    protected void attackFinishAnimation(){
        unitState = UnitState.IDLE;
        isAttacking = null;
    }

    @Override
    public void draw(Canvas canvas) {
        if(actionsList != null){
            for(Action action : actionsList){
                action.draw(canvas);
            }
        }
    }

    @Override
    public boolean takeCellSpace() {
        return !isDead();       //take cell space only if the unit is alive
    }

    @Override
    public boolean isCellInteractable() {
        return !isDead();       //can interact only if the unit is alive
    }

    @Override
    public boolean isViewInteractable() {
        return false;
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

    @Override
    public DiscreteCoordinates getCurrentMainCellCoordinates(){
        return new DiscreteCoordinates(super.getCurrentMainCellCoordinates().x, super.getCurrentMainCellCoordinates().y);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        ((ICWarsInteractionVisitor)v).interactWith(this);
    }

    /**
     * Getter for the spawn position of the unit
     * @return (DiscreteCoordinates)
     */
    public DiscreteCoordinates getUnitSpawnPosition() {
        return unitSpawnPosition;
    }

    /**
     * Draw the unit's range and a path from the unit position to
     destination
     * @param destination path destination
     * @param canvas canvas
     */
    public void drawRangeAndPathTo(DiscreteCoordinates destination ,Canvas canvas) {
        initRange();
        range.draw(canvas);
        Queue<Orientation> path =
                range.shortestPath(getCurrentMainCellCoordinates(),
                        destination);
        //Draw path only if it exists (destination inside the range)
        if (path != null){
            new Path(getCurrentMainCellCoordinates().toVector(),
                    path).draw(canvas);
        }
    }

    @Override
    public boolean changePosition(DiscreteCoordinates newPosition) {
        initRange();

        if(!range.nodeExists(newPosition)){
            return false;
        }
        path = range.shortestPath(getCurrentMainCellCoordinates(), newPosition);
        return true;
    }

    /**
     * Change the alpha of the idle sprites
     * @param alpha (float)
     */
    public void setVisibility(float alpha){
        if ((alpha > 0) && (alpha <= 1)) {
            idleSprite.setAlpha(alpha);
        }
    }

    /**
     * Move the unit in the next path cell
     */
    public void moveUnit(){
        if(path != null ){
            if(orientate(path.peek()) && move(10)){
                path.poll();
            }
        }
    }

    @Override
    public void onLeaving(List<DiscreteCoordinates> coordinates) {
        damageEffect = 0;                                                //cancel effect of the previous cell
        super.onLeaving(coordinates);
    }

    @Override
    public void onEntering(List<DiscreteCoordinates> coordinates) {
        if(currentCell != null){
            currentCell.getType().effect(this);                     //add effect of the new cell
        }
        super.onEntering(coordinates);
    }

    private class ICWarsUnitInteractionHandler implements ICWarsInteractionVisitor {

        @Override
        public void interactWith(ICWarsBehavior.ICWarsCell cell) {       //unit interact with cell to know the number of defense star
            currentCell = cell;
        }

        @Override
        public void interactWith(Unit unit) {

        }
    }


}
