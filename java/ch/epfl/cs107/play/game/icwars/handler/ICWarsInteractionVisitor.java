package ch.epfl.cs107.play.game.icwars.handler;

import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.area.ICWarsBehavior;

public interface ICWarsInteractionVisitor extends AreaInteractionVisitor {

    void interactWith(Unit unit);                           //player can interact with unit
    void interactWith(ICWarsBehavior.ICWarsCell cell);      //player and unit can interact with cell
}
