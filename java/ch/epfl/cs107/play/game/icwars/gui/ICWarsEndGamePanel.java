package ch.epfl.cs107.play.game.icwars.gui;

import ch.epfl.cs107.play.game.actor.Graphics;
import ch.epfl.cs107.play.game.actor.ShapeGraphics;
import ch.epfl.cs107.play.game.actor.SoundAcoustics;
import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.icwars.actor.players.ICWarsPlayer;
import ch.epfl.cs107.play.math.*;
import ch.epfl.cs107.play.math.Polygon;
import ch.epfl.cs107.play.math.Shape;
import ch.epfl.cs107.play.window.Canvas;

import java.awt.*;

public class ICWarsEndGamePanel implements Graphics {


    private final float fontSize;

    /// Sprite and text graphics line
    private final ShapeGraphics background, textBackground;
    private final TextGraphics gameStateText;

    private final ICWarsPlayer player;
    private final Area area;

    private final SoundAcoustics deathSound, victorySound;

    /// boolean to play sound only once
    private boolean soundPlayed;
    private boolean soundStop;

    /**
     * Default Dialog Constructor
     */
    public ICWarsEndGamePanel(float cameraScaleFactor, ICWarsPlayer player, Area owner) {

        final float height = cameraScaleFactor;
        final float width = cameraScaleFactor;

        this.area = owner;
        this.player = player;

        this.soundPlayed = false;
        this.soundStop = false;

        //fontSize = cameraScaleFactor/ICWarsPlayerGUI.FONT_SIZE;
        fontSize = 2;

        Shape rect = new Polygon(0,0, 0,height, width,height, width,0);
        background = new ShapeGraphics(rect, Color.DARK_GRAY, Color.BLACK, 0f, 0f, 2999f);
        Shape textRect = new Polygon(0,0, 0,height/4, width,height/4, width,0);
        textBackground = new ShapeGraphics(textRect, Color.BLACK, Color.BLACK, 0f, 0f, 3000f);

        gameStateText = new TextGraphics("", fontSize, Color.RED, null, 0.0f,
                false, false, new Vector(0, 0),
                TextAlign.Horizontal.CENTER, TextAlign.Vertical.MIDDLE, 0f, 3001f);

        deathSound = new SoundAcoustics("sounds/YouDied.wav", 0.1f, false, false, false, false);
        victorySound = new SoundAcoustics("sounds/YouDied.wav", 0.1f, false, false, false, false);      //TODO sound for victory

    }

    @Override
    public void draw(Canvas canvas) {
        float width = canvas.getXScale();
        float height = canvas.getYScale();

        if(!soundStop){                 /// stop other sound
            area.stopSound();
            soundStop = true;
        }

        if(background.getAlpha()<0.7f){                             /// background fade in
            background.setAlpha(background.getAlpha() + 0.015f);
        }else{
            if(gameStateText.getAlpha() < 1){
                if(!soundPlayed){
                    if(player.isDefeated()){
                        area.requestSound(deathSound);
                        gameStateText.setText("YOU DIED");
                        gameStateText.setFillColor(Color.RED);
                    }else{
                        //area.requestSound(victorySound);
                        gameStateText.setText("VICTORY");
                        gameStateText.setFillColor(Color.GREEN);
                    }

                    soundPlayed = true;
                }

                textBackground.setAlpha(textBackground.getAlpha() + 0.01f);
                gameStateText.setAlpha(gameStateText.getAlpha() + 0.02f);
            }
        }

        background.setRelativeTransform(Transform.I.translated(canvas.getPosition().add(-width/2,-height/2)));
        background.draw(canvas);

        textBackground.setRelativeTransform(Transform.I.translated(canvas.getPosition().add(-width/2,-height/8)));
        textBackground.draw(canvas);

        gameStateText.setRelativeTransform(Transform.I.translated(canvas.getPosition().add(0.3f,0.5f)));
        gameStateText.draw(canvas);

    }

}
