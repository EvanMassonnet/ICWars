package ch.epfl.cs107.play.game.icwars.gui;

import ch.epfl.cs107.play.game.actor.Graphics;
import ch.epfl.cs107.play.game.icwars.actor.players.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.area.ICWarsBehavior;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

public class ICWarsPlayerGUI implements Graphics {

    public static float FONT_SIZE = 20.f;
    private ICWarsPlayer player;
    private ICWarsActionsPanel actionPanel;
    private ICWarsInfoPanel infoPanel;

    public ICWarsPlayerGUI(float cameraScaleFactor , ICWarsPlayer player){
        this.player = player;
        this.actionPanel = new ICWarsActionsPanel(cameraScaleFactor);
        this.infoPanel = new ICWarsInfoPanel(cameraScaleFactor);
    }

    @Override
    public void draw(Canvas canvas) {

    }

    public void draw(Canvas canvas, Unit selectedUnit, Unit unitOver, ICWarsBehavior.ICWarsCellType cellTypeOver){

        if(player.getPlayerState() == ICWarsPlayer.State.MOVE_UNIT) {

            selectedUnit.drawRangeAndPathTo(player.getCurrentMainCellCoordinates(), canvas);

        }else if (player.getPlayerState() == ICWarsPlayer.State.ACTION_SELECTION){
            if(selectedUnit.getActionsList() != null){
                actionPanel.setActions(selectedUnit.getActionsList());
                actionPanel.draw(canvas);
            }

        }else if (player.getPlayerState() == ICWarsPlayer.State.NORMAL || player.getPlayerState() == ICWarsPlayer.State.SELECT_CELL){
            infoPanel.setUnit(unitOver);
            infoPanel.setCurrentCell(cellTypeOver);
            infoPanel.draw(canvas);
        }
    }

}
