package ch.epfl.cs107.play.game.icwars.actor.unit.action;

import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.icwars.actor.players.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.List;

public class Attack extends Action{

    private final ImageGraphics cursor;
    private Unit targetedUnit;
    private int targetedUnitIndex;
    private List<DiscreteCoordinates> attackableEnemy;


    public Attack(Area area, Unit unit){

        super(area, unit, "(A)ttack", Keyboard.A);

        cursor = new ImageGraphics(ResourcePath.getSprite("icwars/UIpackSheet"), 1f, 1f, new RegionOfInterest(4*18, 26*18,16,16));
        cursor.setDepth(3001f);

    }

    @Override
    public void doAction(float dt, ICWarsPlayer player, Keyboard keyboard) {

        if(attackableEnemy == null){        //if attackableEnemy is null, find the enemy
            InitAttack();
        }

        if(!(attackableEnemy.isEmpty())){

            if(keyboard.get(Keyboard.RIGHT).isPressed()){

                targetedUnitIndex = (targetedUnitIndex < attackableEnemy.size() -1) ?  ++targetedUnitIndex : 0;
                targetedUnit = ((ICWarsArea)area).positionToUnit(attackableEnemy.get(targetedUnitIndex));

            }else if(keyboard.get(Keyboard.LEFT).isPressed()){

                targetedUnitIndex = (targetedUnitIndex > 0) ? --targetedUnitIndex : attackableEnemy.size() -1;
                targetedUnit = ((ICWarsArea)area).positionToUnit(attackableEnemy.get(targetedUnitIndex));

            }else if(keyboard.get(Keyboard.ENTER).isPressed()){
                unit.attackUnitAnimation(targetedUnit);                                                 //lunch attack animation
                targetedUnit.ReceiveDamage(-super.unit.getDamage() + targetedUnit.getDefense());        //deal damage
                unit.setAlreadyUsed(true);
                finishAction();
                player.centerCamera();
            }else if(keyboard.get(Keyboard.TAB).isPressed()){   //if tab is pressed, cancel the attack and set the unit already used
                unit.setAlreadyUsed(true);
                finishAction();
                player.centerCamera();
            }
        }else{                      //if attackableEnemy is empty, cancel the attack
            finishAction();
            player.centerCamera();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if(targetedUnit != null){
            area.setViewCandidate(targetedUnit);
            cursor.setAnchor(canvas.getPosition().add(1,0));
            cursor.draw(canvas);
        }
    }

    /**
     * if the action is finish, reset it
     */
    private void finishAction(){
        targetedUnit = null;
        attackableEnemy = null;
        actionFinished = true;
    }

    /**
     * find if some enemy could be attack
     */
    private void InitAttack(){
        attackableEnemy = ((ICWarsArea)area).getAttackableEnemy(unit.getCurrentMainCellCoordinates(), unit.getRadius()+1, unit.getFaction());
        if(attackableEnemy != null && !attackableEnemy.isEmpty()){
            targetedUnitIndex = 0;
            targetedUnit = ((ICWarsArea)area).positionToUnit(attackableEnemy.get(targetedUnitIndex));
        }
    }

}
