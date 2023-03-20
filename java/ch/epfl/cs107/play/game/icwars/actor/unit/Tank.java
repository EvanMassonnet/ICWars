package ch.epfl.cs107.play.game.icwars.actor.unit;

import ch.epfl.cs107.play.game.actor.SoundAcoustics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.icwars.actor.unit.action.Action;
import ch.epfl.cs107.play.game.icwars.actor.unit.action.Attack;
import ch.epfl.cs107.play.game.icwars.actor.unit.action.Wait;
import ch.epfl.cs107.play.math.*;
import ch.epfl.cs107.play.window.Audio;
import ch.epfl.cs107.play.window.Canvas;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Tank extends Unit {

    /// Animation duration in frame number
    private final static int ANIMATION_DURATION = 4;
    private final static int MOVE_DURATION = 8;

    private final Animation[] idleAnimation, walkAnimation, attackAnimation, deathAnimation;
    //sounds
    private final SoundAcoustics deathSound, walkSound, attackSound;

    private boolean soundRequested;     /// boolean to play sound only once

    private  List<Vector> shotTrajectory;   /// trajectory of a shot

    public Tank(Area owner, DiscreteCoordinates coordinates, Faction faction){
        super(owner, coordinates, faction,10, 7, 2);

        actionsList = new ArrayList<>();

        Action attack = new Wait(owner, this);
        actionsList.add(attack);
        Action wait = new Attack(owner, this);
        actionsList.add(wait);

        soundRequested = false;

        if(faction == Faction.ALLIED){
            idleSprite = new Sprite("icwars/friendlyTank" , 1.5f, 1.5f, this , null , new Vector(-0.25f, -0.25f));
        }else{
            idleSprite = new Sprite("icwars/enemyTank" , 1.5f, 1.5f, this , null , new Vector(-0.25f, -0.25f));
        }

        String spriteName = (faction == Faction.ALLIED)? "Green" : "Red";

        //idle animation

        Sprite[] idleSprite = Sprite.extractSprites("icwars/new/"+  spriteName +"RobotIdle", 4, 1.5f, 2f, this, 105, 160);
        Sprite[] idleSpriteMirror = Sprite.extractSprites("icwars/new/"+  spriteName +"RobotIdle", 4, 1.5f, 2f, this, 105, 160);
        for(int i = 0; i < 4; ++i){
            idleSprite[i].setRelativeTransform(Transform.I.translated(-0.2f,0f));
            idleSpriteMirror[i].setRelativeTransform(idleSprite[i].getRelativeTransform().translated(-1f,0).mirrorY());
        }

        Sprite [][] idles = {idleSprite, idleSpriteMirror, idleSpriteMirror, idleSprite};
        idleAnimation = Animation.createAnimations(ANIMATION_DURATION, idles);

        //walk animation

        Sprite[] walkSprite = Sprite.extractSprites("icwars/new/"+  spriteName +"RobotWalk", 10, 1.5f, 2f, this, 100, 173);
        Sprite[] walkSpriteMirror = Sprite.extractSprites("icwars/new/"+  spriteName +"RobotWalk", 10, 1.5f, 2f, this, 100, 173);
        for(int i = 0; i < 10; ++i){
            walkSpriteMirror[i].setRelativeTransform(walkSprite[i].getRelativeTransform().translated(-1.5f,0).mirrorY());
        }

        Sprite [][] walks = {walkSprite, walkSpriteMirror, walkSpriteMirror, walkSprite};
        walkAnimation = Animation.createAnimations(ANIMATION_DURATION, walks);

        //attack animation

        Sprite[] attackSprite = Sprite.extractSprites("icwars/new/"+  spriteName +"RobotAttack", 26, 2.7f, 1.9f, this, 170, 154);
        Sprite[] attackSpriteMirror = Sprite.extractSprites("icwars/new/"+  spriteName +"RobotAttack", 26, 2.7f, 1.9f, this, 170, 154);
        for(int i = 0; i < 26; ++i){
            attackSprite[i].setRelativeTransform(Transform.I.translated(-1f,0.05f));
            attackSpriteMirror[i].setRelativeTransform(attackSprite[i].getRelativeTransform().translated(-1.5f,0f).mirrorY());
        }

        Sprite [][] attacks = {attackSprite, attackSpriteMirror, attackSpriteMirror, attackSprite};
        attackAnimation = Animation.createAnimations(ANIMATION_DURATION, attacks, false);

        //death animation

        Sprite[] deathSprite = Sprite.extractSprites("icwars/new/"+  spriteName +"RobotDeath", 18, 3f, 3f, this, 200, 280);
        Sprite[] deathSpriteMirror = Sprite.extractSprites("icwars/new/"+  spriteName +"RobotDeath", 18, 3f, 3f, this, 200, 280);
        for(int i = 0; i < 18; ++i){
            deathSprite[i].setRelativeTransform(Transform.I.translated(-0.9f,-0.2f));
            deathSpriteMirror[i].setRelativeTransform(deathSprite[i].getRelativeTransform().translated(-1.6f,-0.2f).mirrorY());
        }

        Sprite [][] deaths = {deathSprite, deathSpriteMirror, deathSpriteMirror, deathSprite};
        deathAnimation = Animation.createAnimations(ANIMATION_DURATION, deaths, false);

        // sounds

        attackSound = new SoundAcoustics("sounds/GreenRobotAttack.wav", 0.5f, false, false, false, false);
        walkSound = new SoundAcoustics("sounds/MainWalk.wav", 0.5f, false, false, false, false);
        deathSound = new SoundAcoustics("sounds/RobotDeath.wav", 0.5f, false, false, false, false);

    }

    @Override
    public void draw(Canvas canvas) {
        if(getHp() < 1){
            if(!soundRequested){
                deathSound.shouldBeStarted();
                soundRequested = true;
            }
            if(!deathAnimation[this.getOrientation().ordinal()].isCompleted()){
                deathAnimation[this.getOrientation().ordinal()].draw(canvas);
            }
        }else if(isDisplacementOccurs()){
            if(!soundRequested){
                walkSound.shouldBeStarted();
                soundRequested = true;
            }
            walkAnimation[this.getOrientation().ordinal()].draw(canvas);
        }else if(getUnitState() == UnitState.ATTACK){
            if(!soundRequested){
                attackSound.shouldBeStarted();
                soundRequested = true;
            }
            drawAttackTo(isAttacking, canvas);
            attackAnimation[this.getOrientation().ordinal()].draw(canvas);
            if(attackAnimation[this.getOrientation().ordinal()].isCompleted()){
                attackFinishAnimation();
                attackAnimation[this.getOrientation().ordinal()].reset();
            }
        }else {
            soundRequested = false;                                             // sound could be play again
            idleAnimation[this.getOrientation().ordinal()].draw(canvas);        // default animation
            shotTrajectory = null;                                              // reset trajectory
        }

        super.draw(canvas);
    }

    public List<Action> getActionsList() {
        return actionsList;
    }

    @Override
    public void setVisibility(float alpha) {
        super.setVisibility(alpha);
        for(Animation animation : idleAnimation){
            animation.setAlpha(alpha);
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        idleAnimation[this.getOrientation().ordinal()].update(deltaTime);
        walkAnimation[this.getOrientation().ordinal()].update(deltaTime);
        if(getHp() ==0){
            deathAnimation[this.getOrientation().ordinal()].update(deltaTime);
        }else if(getUnitState() == UnitState.ATTACK){
            attackAnimation[this.getOrientation().ordinal()].update(deltaTime);
        }
    }

    @Override
    public void bip(Audio audio) {
        super.bip(audio);
        deathSound.bip(audio);
        walkSound.bip(audio);
        attackSound.bip(audio);
    }

    /**
     * Methode to draw the attack of the unit
     * @param enemy (Unit) target unit
     * @param canvas (Canvas)
     */
    public void drawAttackTo(Unit enemy ,Canvas canvas) {
        this.orientate((enemy.getCurrentMainCellCoordinates().toVector().x - getCurrentMainCellCoordinates().toVector().x > 0) ? Orientation.RIGHT: Orientation.LEFT);  //orient the unit towards its target

        if(shotTrajectory == null || shotTrajectory.isEmpty()){     // if the trajectory of the shot doesn't exist, create it
            shotTrajectory = new ArrayList<>();
            Vector start = getCurrentMainCellCoordinates().toVector().add(new Vector(0.6f + getOrientation().toVector().x, 0.7f));
            Vector end = enemy.getCurrentMainCellCoordinates().toVector().add(new Vector(0.5f, 0.5f));

            shotTrajectory.add(start);

            for(int j = 1; j <= 5; ++j){    //subdivide the interval to draw projectile step by step
                shotTrajectory.add(start.add(end.sub(start).div(5).mul(j)));
            }
        }

        if(!shotTrajectory.isEmpty()){
            canvas.drawShape( new Circle(0.1f, shotTrajectory.get(0)), Transform.I, new Color(255, 0, 0), new Color(225, 125, 10), 0.03f, 1f, 10001);       //draw a circle on the first element of the trajectory
            shotTrajectory.remove(0); //remove the first element
        }
    }
}

