package ch.epfl.cs107.play.game.icwars.area;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.icwars.ICWars;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActor;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

import java.util.ArrayList;
import java.util.List;

public abstract class ICWarsArea extends Area {

    private ICWarsBehavior behavior;

    protected abstract void createArea();

    private List<Unit> unitList = new ArrayList<>();

    @Override
    public final float getCameraScaleFactor() {
        return ICWars.CAMERA_SCALE_FACTOR;
    }

    public abstract List<Unit> getAlliedUnits();
    public abstract List<Unit> getEnemyUnits();

    //Spawn position of the players
    public abstract DiscreteCoordinates getAlliedSpawnPosition();
    public abstract DiscreteCoordinates getEnemySpawnPosition();

    //Spawn position of the units
    public abstract DiscreteCoordinates getAlliedTankSpawnPosition();
    public abstract DiscreteCoordinates getAlliedSoldierSpawnPosition();
    public abstract DiscreteCoordinates getEnemyTankSpawnPosition();
    public abstract DiscreteCoordinates getEnemySoldierSpawnPosition();



    /**
     * Add unit to the area list
     * @param unit (Unit)
     */
    public void addUnit(Unit unit){
        unitList.add(unit);
    }

    /**
     * Remove unit from the area list
     * @param unit (Unit)
     */
    public void removeUnit(Unit unit){
        unitList.remove(unit);
    }


    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            // Set the behavior map
            behavior = new ICWarsBehavior(window, getTitle());
            setBehavior(behavior);
            createArea();
            return true;
        }
        return false;
    }

    /**
     * Return a list of all attackable enemey of a given unit
     * @param position (DiscreteCoordinates) position of the unit
     * @param range (int) range of the unit
     * @param faction (Faction) faction of the unit
     * @return (List<DiscreteCoordinates>) position of all attackable enemy
     */
    public List<DiscreteCoordinates> getAttackableEnemy(DiscreteCoordinates position, int range, ICWarsActor.Faction faction){
        List<DiscreteCoordinates> attackableEnemy = new ArrayList<>();
        for(Unit unit : unitList){
            if(unit.getFaction() != faction && !unit.isDead()){
                if(unit.getPosition().x <= position.x + range && unit.getPosition().x >= position.x - range && unit.getPosition().y <= position.y + range && unit.getPosition().y >= position.y - range ){
                    attackableEnemy.add(unit.getCurrentMainCellCoordinates());
                }
            }
        }
        return attackableEnemy;
    }

    /**
     * Return a list of position of all unit of a faction
     * @param faction (Faction)
     * @return (List<DiscreteCoordinates>)
     */
    public List<DiscreteCoordinates> getAllEnemy(ICWarsActor.Faction faction){
        List<DiscreteCoordinates> allEnemy = new ArrayList<>();
        for(Unit unit : unitList){
            if(unit.getFaction() != faction && !unit.isDead()){
                allEnemy.add(unit.getCurrentMainCellCoordinates());
            }
        }
        return allEnemy;
    }

    /**
     * Convert a position in a Unit, return null if there is no unit
     * @param position (DiscreteCoordinates)
     * @return (Unit)
     */
    public Unit positionToUnit(DiscreteCoordinates position){
        for(Unit unit : unitList){
            if(unit.getCurrentMainCellCoordinates().equals(position)){
                return unit;
            }
        }
        return null;
    }

}
