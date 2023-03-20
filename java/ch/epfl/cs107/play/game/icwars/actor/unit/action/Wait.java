package ch.epfl.cs107.play.game.icwars.actor.unit.action;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.icwars.actor.players.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

public class Wait extends Action{

    public Wait(Area area, Unit unit){
        super(area, unit, "(W)ait", Keyboard.W);
    }

    @Override
    public void doAction(float dt, ICWarsPlayer player, Keyboard keyboard) {
        unit.setAlreadyUsed(true);
        actionFinished = true;
    }

    @Override
    public void draw(Canvas canvas) {
    }

}
