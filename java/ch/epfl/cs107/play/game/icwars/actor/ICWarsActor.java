package ch.epfl.cs107.play.game.icwars.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;


import java.util.Collections;
import java.util.List;

public abstract class ICWarsActor extends MovableAreaEntity {

    private Faction faction;

    /**
     * faction enum
     */
    public enum Faction{
        ALLIED,
        ENEMY
    }

    public ICWarsActor(Area owner, DiscreteCoordinates coordinates, Faction faction) {
        super(owner,(faction == Faction.ALLIED) ? Orientation.RIGHT : Orientation.LEFT, coordinates);
        this.faction = faction;
    }

    /**
     * Getter for the faction of the actor
     * @return (Faction)
     */
    public Faction getFaction(){
        return faction;
    }

    /**
     * Leave an area by unregister this player
     */
    public void leaveArea(){
        getOwnerArea().unregisterActor(this);
    }

    /**
     *
     * @param area (Area): initial area, not null
     * @param position (DiscreteCoordinates): initial position, not null
     */
    public void enterArea(Area area, DiscreteCoordinates position){
        area.registerActor(this);
        setOwnerArea(area);
        setCurrentPosition(position.toVector());
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }
}
