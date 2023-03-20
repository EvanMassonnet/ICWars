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

public class Soldier extends Unit {

    /// Animation duration in frame number
    private final static int ANIMATION_DURATION = 4;
    private final static int MOVE_DURATION = 8;

    private final Animation[] idleAnimation, walkAnimation, attackAnimation, deathAnimation;
    /// sounds
    private final SoundAcoustics deathSound, walkSound, attackSound;

    private boolean soundRequested;      /// boolean to play sound only once

    private  List<Vector> shotTrajectory;   /// trajectory of a shot


    public Soldier(Area owner, DiscreteCoordinates coordinates, Faction faction){
        super(owner, coordinates, faction,5, 2, 5);

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

        String spriteName = (faction == Faction.ALLIED)? "" : "Enemy";

        //idle animation

        Sprite[] idleSprite = Sprite.extractSprites("icwars/new/Gun"+ spriteName +"Idle", 4, 1.2f, 1.6f, this, 70, 60);
        Sprite[] idleSpriteMirror = Sprite.extractSprites("icwars/new/Gun"+ spriteName +"Idle", 4, 1.2f, 1.6f, this, 70, 60);
        for(int i = 0; i < 4; ++i){
            idleSpriteMirror[i].setRelativeTransform(idleSprite[i].getRelativeTransform().translated(-1f,0).mirrorY());
        }

        Sprite [][] idles = {idleSpriteMirror, idleSprite, idleSprite, idleSpriteMirror};
        idleAnimation = Animation.createAnimations(ANIMATION_DURATION, idles);


        //walk animation

        Sprite[] walkSprite = Sprite.extractSprites("icwars/new/Gun"+ spriteName +"Walk", 6, 1.2f, 1.6f, this, 80, 60);
        Sprite[] walkSpriteMirror = Sprite.extractSprites("icwars/new/Gun"+ spriteName +"Walk", 6, 1.2f, 1.6f, this, 80, 60);
        for(int i = 0; i < 6; ++i){
            walkSpriteMirror[i].setRelativeTransform(walkSprite[i].getRelativeTransform().translated(-1f,0).mirrorY());
        }

        Sprite [][] walks = {walkSpriteMirror, walkSprite, walkSprite, walkSpriteMirror};
        walkAnimation = Animation.createAnimations(ANIMATION_DURATION, walks);

        // attack animation

        Sprite[] attackSprite = Sprite.extractSprites("icwars/new/Gun"+ spriteName +"Attack", 13, 1.8f, 1.5f, this, 80, 60);
        Sprite[] attackSpriteMirror = Sprite.extractSprites("icwars/new/Gun"+ spriteName +"Attack", 13, 1.8f, 1.5f, this, 80, 60);
        for(int i = 0; i < 13; ++i){
            attackSpriteMirror[i].setRelativeTransform(attackSprite[i].getRelativeTransform().translated(-1.5f,0).mirrorY());
        }

        Sprite [][] attacks = {attackSpriteMirror, attackSprite, attackSprite, attackSpriteMirror};
        attackAnimation = Animation.createAnimations(ANIMATION_DURATION, attacks, false);


        // death animation

        Sprite[] deathSprite = Sprite.extractSprites("icwars/new/"+ spriteName +"Death", 6, 1.8f, 1.5f, this, 80, 60);
        Sprite[] deathSpriteMirror = Sprite.extractSprites("icwars/new/"+ spriteName +"Death", 6, 1.8f, 1.5f, this, 80, 60);
        for(int i = 0; i < 6; ++i){
            deathSprite[i].setRelativeTransform(Transform.I.translated(-0.6f,0));
            deathSpriteMirror[i].setRelativeTransform(deathSprite[i].getRelativeTransform().translated(-1.5f,0).mirrorY());
        }

        Sprite [][] deaths = {deathSpriteMirror, deathSprite, deathSprite, deathSpriteMirror};
        deathAnimation = Animation.createAnimations(ANIMATION_DURATION, deaths, false);


        // sounds

        attackSound = new SoundAcoustics("sounds/LaserAttack.wav", 0.5f, false, false, false, false);
        walkSound = new SoundAcoustics("sounds/MainWalk.wav", 0.5f, false, false, false, false);
        deathSound = new SoundAcoustics("sounds/MainDeath" + (RandomGenerator.getInstance().nextInt(4) + 1)+".wav", 0.5f, false, false, false, false);



    }


    @Override
    public void draw(Canvas canvas) {
        if(getHp() == 0){
            if(!soundRequested){
                deathSound.shouldBeStarted();
                soundRequested = true;
            }
            deathAnimation[this.getOrientation().ordinal()].draw(canvas);
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
        }else{
            soundRequested = false;                                         // sound could be play again
            idleAnimation[this.getOrientation().ordinal()].draw(canvas);    // default animation
            shotTrajectory = null;                                          // reset trajectory
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
        this.orientate((enemy.getCurrentMainCellCoordinates().toVector().x - getCurrentMainCellCoordinates().toVector().x > 0) ? Orientation.RIGHT: Orientation.LEFT);      //orient the unit towards its target

        if(shotTrajectory == null || shotTrajectory.isEmpty()){     // if the trajectory of the shot doesn't exist, create it
            shotTrajectory = new ArrayList<>();
            Vector start = getCurrentMainCellCoordinates().toVector().add(new Vector(0.6f + getOrientation().toVector().x, 0.7f));
            Vector end = enemy.getCurrentMainCellCoordinates().toVector().add(new Vector(0.5f, 0.5f));

            shotTrajectory.add(start);

            for(int j = 1; j <= 10; ++j){   //subdivide the interval to draw projectile step by step
                shotTrajectory.add(start.add(end.sub(start).div(10).mul(j)));
            }
        }

        canvas.drawShape( new Polyline(shotTrajectory.get(0), shotTrajectory.get(shotTrajectory.size()-1)), Transform.I, null, new Color(255,253,154), 0.08f, 0.8f, 10001);     //main beam

        for(int i =0; i < shotTrajectory.size() - 1; ++i ){     //add random effect on each subdivide of the beam
            Polyline pathLine = new Polyline(shotTrajectory.subList(i, i+2));
            if(RandomGenerator.getInstance().nextBoolean()){
                canvas.drawShape(pathLine, Transform.I, null, new Color(0,247,255), 0.2f, 1f, 10000);
            }else{
                canvas.drawShape(pathLine, Transform.I, null, new Color(30, 163, 250), 0.2f, 1f, 10000);
            }
        }
    }
}