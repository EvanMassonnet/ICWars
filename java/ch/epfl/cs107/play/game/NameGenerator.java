package ch.epfl.cs107.play.game;
import ch.epfl.cs107.play.math.RandomGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class NameGenerator {

    private static File listInfo;
    private static File listSysCom;
    private static File listTeacher;

    private static int listInfoSize = 473;
    private static int listSysComSize = 98 ;
    private static int listTeacherSize = 23;

    private static Scanner scannerInfo;
    private static Scanner scannerSysCom;
    private static Scanner scannerTeacher;

    static private void init(){
        try {
            listInfo = new File("res/List_Info.txt");
            listSysCom = new File("res/List_SysCom.txt");
            listTeacher = new File("res/List_Teacher.txt");

            scannerInfo = new Scanner(listInfo);
            scannerSysCom = new Scanner(listSysCom);
            scannerTeacher = new Scanner(listTeacher);

        }catch (FileNotFoundException e) {
            System.out.println("The file can't be open or doesn't exist");
            e.printStackTrace();
        }
    }

    static public String RandomInfo(){
        init();
        int randomInt = RandomGenerator.getInstance().nextInt(listInfoSize);
        for(int line = 0; line < randomInt; ++line){
            scannerInfo.nextLine();
        }
        return scannerInfo.nextLine().replaceAll("\t", " ");
    }

    static public String RandomSysCom(){
        init();
        int randomInt = RandomGenerator.getInstance().nextInt(listSysComSize);
        for(int line = 0; line < randomInt; ++line){
            scannerSysCom.nextLine();
        }
        return scannerSysCom.nextLine().replaceAll("\t", " ");
    }

    static public String RandomTeacher(){
        init();
        int randomInt = RandomGenerator.getInstance().nextInt(listTeacherSize);
        for(int line = 0; line < randomInt; ++line){
            scannerTeacher.nextLine();
        }
        return scannerTeacher.nextLine().replaceAll("\t", " ");
    }

    static public String RandomPlayer(){
        init();
        int randomProba = RandomGenerator.getInstance().nextInt(101);
        if(randomProba <= Math.abs(100*listSysComSize/(listInfoSize + listSysComSize))){
            return RandomSysCom();
        }else{
            return RandomInfo();
        }
    }

}
