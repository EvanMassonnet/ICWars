package ch.epfl.cs107.play.game.icwars.area;

import ch.epfl.cs107.play.game.areagame.AreaBehavior;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.actor.unit.Unit;
import ch.epfl.cs107.play.game.icwars.handler.ICWarsInteractionVisitor;
import ch.epfl.cs107.play.window.Window;

public class ICWarsBehavior extends AreaBehavior{

    public enum ICWarsCellType{

        NONE(0, 0),
        ROAD(-16777216, 0),
        PLAIN(-14112955, 1),
        WOOD(-65536, 3),
        RIVER(-16776961, 0),
        MOUNTAIN(-256, 4),
        CITY(-1,2),
        LAVA(-26624,0),
        ICE(-1249295,0);

        final int type;
        final int defense;

        ICWarsCellType(int type, int defense){
            this.type = type;
            this.defense = defense;
        }

        public static ICWarsBehavior.ICWarsCellType toType(int type){
            for(ICWarsBehavior.ICWarsCellType ict : ICWarsBehavior.ICWarsCellType.values()){
                if(ict.type == type)
                    return ict;
            }
            // When you add a new color, you can print the int value here before assign it to a type
            System.out.println(type);
            return NONE;
        }

        /**
         * Effect of each cell
         * @param unit (Unit) on wish unit add effect
         */
        public void effect(Unit unit){
            if(this == ICWarsCellType.LAVA){        //Lava cell : 1 damage each time the unit enter on the cell
                unit.ReceiveDamage(1);

            }else if(this == ICWarsCellType.ICE){   //Ice cell : increase  damage receive by 2
                unit.applyDamageEffect(2);

            }else if(this == ICWarsCellType.CITY){  //City cell : decrease damage receive by 1 and reaper the unit by 1
                unit.applyDamageEffect(-1);
                unit.Repair(1);
            }
        }

        /**
         * Getter for the defense star of the cell
         * @return (int)
         */
        public int getDefenseStar(){
            return this.defense;
        }

        public String typeToString(){
            return toType(this.type).toString();
        }

    }

    public ICWarsBehavior(Window window, String name){
        super(window, name);
        int height = getHeight();
        int width = getWidth();
        for(int y = 0; y < height; y++) {
            for (int x = 0; x < width ; x++) {
                ICWarsBehavior.ICWarsCellType color = ICWarsBehavior.ICWarsCellType.toType(getRGB(height-1-y, x));
                setCell(x,y, new ICWarsBehavior.ICWarsCell(x,y,color));
            }
        }
    }

    public class ICWarsCell extends AreaBehavior.Cell {

        private final ICWarsBehavior.ICWarsCellType type;

        public  ICWarsCell(int x, int y, ICWarsBehavior.ICWarsCellType type){
            super(x, y);
            this.type = type;
        }

        @Override
        protected boolean canLeave(Interactable entity) {
            return true;
        }

        @Override
        protected boolean canEnter(Interactable entity) {
            if(entity.takeCellSpace()){
                for(Interactable interactable : entities){
                    if(interactable.takeCellSpace()){
                        return false;
                    }
                }
            }
            return true;
        }

        /**
         * Getter for the defense star of the cell
         * @return (int)
         */
        public int getDefenseStar(){
            return type.defense;
        }

        /**
         * Getter type of cell
         * @return (ICWarsCellType)
         */
        public ICWarsCellType getType() {
            return type;
        }

        @Override
        public boolean isCellInteractable() {
            return true;
        }

        @Override
        public boolean isViewInteractable() {
            return false;
        }

        @Override
        public void acceptInteraction(AreaInteractionVisitor v) {
            ((ICWarsInteractionVisitor)v).interactWith(this);
        }

    }
}
