package ch.epfl.cs107.play.game.icwars.actor.players;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.List;

public class AIPlayer extends ICWarsPlayer{

    private boolean counting;
    private float counter;

    private int selectedUnitIndex;

    private final Sprite sprite;

    public AIPlayer(Area owner, DiscreteCoordinates coordinates, Faction faction, List<Unit> units) {
        super(owner, coordinates, faction, units);

        String spriteName = (faction == Faction.ALLIED) ? "icwars/allyCursor" : "icwars/enemyCursor";
        sprite = new Sprite(spriteName, 1.f, 1.f,this);
    }

    /**
     * Ensures that value time elapsed before returning true
     * @param dt elapsed time
     * @param value waiting time (in seconds)
     * @return true if value seconds has elapsed , false otherwise
     */
    private boolean waitFor(float value , float dt) {
        if (counting) {
            counter += dt;
            if (counter > value) {
                counting = false;
                return true;
            }
        } else {
            counter = 0f;
            counting = true;
        }
        return false;
    }

    public void update(float deltaTime) {

        switch (playerState){
            case IDLE :
                selectedUnitIndex = -1;
                break;
            case NORMAL:
                if(selectedUnitIndex < units.size()-1){     //select all unit one by one
                    ++selectedUnitIndex;
                    playerState = State.SELECT_CELL;
                }else{
                    playerState = State.IDLE;       //if all unit was selected, return to idle
                }
                break;
            case SELECT_CELL:
                selectedUnit = units.get(selectedUnitIndex);
                List<DiscreteCoordinates> allEnemy = ((ICWarsArea) getOwnerArea()).getAllEnemy(selectedUnit.getFaction());
                if(!attackableEnemy().isEmpty()){       //if an enemy could be attack, attack it
                    playerState = State.ACTION_SELECTION;
                }else if (!allEnemy.isEmpty()) {        //else move toward it
                    DiscreteCoordinates nearestEnemyPosition = nearestEnemy(selectedUnit.getCurrentMainCellCoordinates(), ((ICWarsArea)getOwnerArea()).getAllEnemy(selectedUnit.getFaction()));     //enemy position
                    DiscreteCoordinates destination = bestWay(selectedUnit.getCurrentMainCellCoordinates(), nearestEnemyPosition, selectedUnit.getRadius());                                        //best way to the enemy unit
                    selectedUnit.changePosition(destination);
                    setCurrentPosition(destination.toVector());
                    selectedUnit.moveUnit();
                    playerState = State.MOVE_UNIT;
                }else{
                    playerState = State.NORMAL;
                }
                break;
            case MOVE_UNIT:
                if(waitFor(1,0.1f)){
                    if(selectedUnit.getUnitState() == Unit.UnitState.IDLE){         //wait the end of walk animation
                        playerState = State.ACTION_SELECTION;
                    }else if(selectedUnit.getUnitState() == Unit.UnitState.DEATH){
                        playerState = State.NORMAL;
                    }
                }
                break;
            case ACTION_SELECTION:
                if(selectedUnit.isDead()){
                    playerState = State.NORMAL;
                }else{
                    if(!attackableEnemy().isEmpty()) {
                        Unit targetedUnit = getWeakerEnemy(attackableEnemy());       //attack the weaker enemy if possible
                        targetedUnit.ReceiveDamage(-super.selectedUnit.getDamage() + targetedUnit.getDefense());
                        selectedUnit.attackUnitAnimation(targetedUnit);
                    }
                    playerState = State.ACTION;
                }



                break;
            case ACTION:
                if(waitFor(1,0.1f)){
                    if(selectedUnit.getUnitState() == Unit.UnitState.IDLE){     //wait the end of attack animation
                        playerState = State.NORMAL;
                    }
                }
                break;

        }
        super.update(deltaTime);
    }


    @Override
    public void draw(Canvas canvas) {
        if(playerState != State.IDLE){
            sprite.draw(canvas);
        }
    }

    /**
     * return the list of the coordinate of all attackable enemy
     * @return (List<DiscreteCoordinates>)
     */
    private List<DiscreteCoordinates> attackableEnemy(){
        return ((ICWarsArea)getOwnerArea()).getAttackableEnemy(selectedUnit.getCurrentMainCellCoordinates(), selectedUnit.getRadius(), selectedUnit.getFaction());
    }

    /**
     * return the nearest enemy from the unit
     * @param unitPosition (DiscreteCoordinates)
     * @param enemyPositions (List<DiscreteCoordinates>)
     * @return (DiscreteCoordinates)
     */
    private DiscreteCoordinates nearestEnemy(DiscreteCoordinates unitPosition, List<DiscreteCoordinates> enemyPositions){
        DiscreteCoordinates nearest = enemyPositions.get(0);
        for(DiscreteCoordinates enemyPosition : enemyPositions){
            if(DiscreteCoordinates.distanceBetween(enemyPosition, unitPosition) < DiscreteCoordinates.distanceBetween(nearest, unitPosition)){
                nearest = enemyPosition;
            }
        }
        return nearest;
    }

    /**
     * return the weaker enemy from the enemy list
     * @param enemyPositions (List<DiscreteCoordinates>)
     * @return (Unit)
     */
    private Unit getWeakerEnemy(List<DiscreteCoordinates> enemyPositions){
        Unit weaker = ((ICWarsArea)getOwnerArea()).positionToUnit(enemyPositions.get(0));
        for(DiscreteCoordinates enemyPosition : enemyPositions){
            if(((ICWarsArea)getOwnerArea()).positionToUnit(enemyPosition).getHp() < weaker.getHp()){
                weaker = ((ICWarsArea)getOwnerArea()).positionToUnit(enemyPosition);
            }
        }
        return weaker;

    }

    /**
     * Compute the best position to get close to destination position
     * @param start (DiscreteCoordinates) position of the unit
     * @param destination (DiscreteCoordinates) position to go
     * @param radius (int) mouvement radius of the unit
     * @return (DiscreteCoordinates) a cell where the unit could go
     */
    private DiscreteCoordinates bestWay(DiscreteCoordinates start, DiscreteCoordinates destination, int radius) {

        DiscreteCoordinates result;

        int minX = Math.min(Math.abs(destination.x - start.x), radius);
        int minY = Math.min(Math.abs(destination.y - start.y), radius);

        //compute the best cell, may not be accessible

        if (destination.x - start.x >= 0 && destination.y - start.y <= 0) {
            result = new DiscreteCoordinates(start.x + minX, start.y - minY);
        } else if (destination.x - start.x <= 0 && destination.y - start.y >= 0) {
            result = new DiscreteCoordinates(start.x - minX, start.y + minY);
        } else if (destination.x - start.x <= 0 && destination.y - start.y <= 0) {
            result = new DiscreteCoordinates(start.x - minX, start.y - minY);
        } else {
            result = new DiscreteCoordinates(start.x + minX, start.y + minY);
        }

        if (!selectedUnit.changePosition(result)) {                                             //if the cell is not accessible
            List<DiscreteCoordinates> oldPosition = new ArrayList<>();
            do {                                                                //see if the cells beside are accessible
                oldPosition.add(result);
                DiscreteCoordinates best = start;
                for (DiscreteCoordinates neighbor : result.getNeighbours()) {
                    if (DiscreteCoordinates.distanceBetween(neighbor, start) <= selectedUnit.getRadius()) {
                        if (!oldPosition.contains(neighbor)) {
                            oldPosition.add(neighbor);
                            if (DiscreteCoordinates.distanceBetween(neighbor, destination) < DiscreteCoordinates.distanceBetween(best, destination)) {
                                best = neighbor;
                            }
                        }
                    }
                }
                result = best;
            } while (!selectedUnit.changePosition(result));     //while the cell is not accessible, keep searching
        }
        return result;
    }

}