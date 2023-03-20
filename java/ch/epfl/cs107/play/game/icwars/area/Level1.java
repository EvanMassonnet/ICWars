package ch.epfl.cs107.play.game.icwars.area;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActor;
import ch.epfl.cs107.play.game.icwars.actor.unit.Soldier;
import ch.epfl.cs107.play.game.icwars.actor.unit.Tank;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.ArrayList;
import java.util.List;

public class Level1 extends ICWarsArea{

    @Override
    public String getTitle() {
        return "icwars/Level1";
    }

    @Override
    public List<Unit> getAlliedUnits(){
        List<Unit> units = new ArrayList<>();

        Unit tank = new Tank(this, new DiscreteCoordinates(2,5), ICWarsActor.Faction.ALLIED);
        units.add(tank);

        Unit soldier = new Soldier(this, new DiscreteCoordinates(3,5), ICWarsActor.Faction.ALLIED);
        units.add(soldier);

        return units;
    }

    @Override
    public List<Unit> getEnemyUnits() {
        List<Unit> units = new ArrayList<>();

        Unit tank = new Tank(this, new DiscreteCoordinates(8,5), ICWarsActor.Faction.ENEMY);
        units.add(tank);

        Unit soldier1 = new Soldier(this, new DiscreteCoordinates(9,4), ICWarsActor.Faction.ENEMY);
        units.add(soldier1);

        Unit soldier2 = new Soldier(this, new DiscreteCoordinates(9,6), ICWarsActor.Faction.ENEMY);
        units.add(soldier2);

        return units;
    }


    @Override
    public DiscreteCoordinates getAlliedSpawnPosition() {
        return new DiscreteCoordinates(4,5);
    }

    @Override
    public DiscreteCoordinates getEnemySpawnPosition() {
        return new DiscreteCoordinates(17,5);
    }

    @Override
    public DiscreteCoordinates getAlliedTankSpawnPosition() {
        return new DiscreteCoordinates(2,5);
    }

    @Override
    public DiscreteCoordinates getAlliedSoldierSpawnPosition() {
        return new DiscreteCoordinates(3,5);
    }

    @Override
    public DiscreteCoordinates getEnemyTankSpawnPosition() {
        return new DiscreteCoordinates(8,5);
    }

    @Override
    public DiscreteCoordinates getEnemySoldierSpawnPosition() {
        return new DiscreteCoordinates(9,5);
    }

    protected void createArea() {
        registerActor(new Background(this));

    }
}
