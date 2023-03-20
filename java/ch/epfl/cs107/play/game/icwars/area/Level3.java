package ch.epfl.cs107.play.game.icwars.area;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActor;
import ch.epfl.cs107.play.game.icwars.actor.unit.Soldier;
import ch.epfl.cs107.play.game.icwars.actor.unit.Tank;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.ArrayList;
import java.util.List;

public class Level3 extends ICWarsArea{

    @Override
    public String getTitle() {
        return "icwars/Level3";
    }

    @Override
    public List<Unit> getAlliedUnits(){
        List<Unit> units = new ArrayList<>();

        Unit tank = new Tank(this, new DiscreteCoordinates(2,7), ICWarsActor.Faction.ALLIED);
        units.add(tank);

        Unit soldier1 = new Soldier(this, new DiscreteCoordinates(3,7), ICWarsActor.Faction.ALLIED);
        units.add(soldier1);

        Unit soldier2 = new Soldier(this, new DiscreteCoordinates(2,5), ICWarsActor.Faction.ALLIED);
        units.add(soldier2);

        Unit soldier3 = new Soldier(this, new DiscreteCoordinates(4,8), ICWarsActor.Faction.ALLIED);
        units.add(soldier3);

        return units;
    }

    @Override
    public List<Unit> getEnemyUnits() {
        List<Unit> units = new ArrayList<>();

        Unit tank1 = new Tank(this, new DiscreteCoordinates(18,2), ICWarsActor.Faction.ENEMY);
        units.add(tank1);

        Unit tank2 = new Tank(this, new DiscreteCoordinates(18,6), ICWarsActor.Faction.ENEMY);
        units.add(tank2);

        Unit tank3 = new Tank(this, new DiscreteCoordinates(17,8), ICWarsActor.Faction.ENEMY);
        units.add(tank3);

        Unit soldier = new Soldier(this, new DiscreteCoordinates(17,4), ICWarsActor.Faction.ENEMY);
        units.add(soldier);

        return units;
    }

    @Override
    public DiscreteCoordinates getAlliedSpawnPosition() {
        return new DiscreteCoordinates(5,7);
    }

    @Override
    public DiscreteCoordinates getEnemySpawnPosition() {
        return new DiscreteCoordinates(17,5);
    }

    @Override
    public DiscreteCoordinates getAlliedTankSpawnPosition() {
        return new DiscreteCoordinates(2,7);
    }

    @Override
    public DiscreteCoordinates getAlliedSoldierSpawnPosition() {
        return new DiscreteCoordinates(3,7);
    }

    @Override
    public DiscreteCoordinates getEnemyTankSpawnPosition() {
        return new DiscreteCoordinates(17,5);
    }

    @Override
    public DiscreteCoordinates getEnemySoldierSpawnPosition() {
        return new DiscreteCoordinates(18,5);
    }

    protected void createArea() {
        registerActor(new Background(this));

    }
}