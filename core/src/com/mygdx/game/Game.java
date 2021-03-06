package com.mygdx.game;

import controllerView.ControllerView;
import model.persons.MainCharacter;
import model.Model;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;

public class Game{

    private Model model;
    private ControllerView vc;
    private File mapsFile;
    private FileWriter fw;
    private FileReader fr;
    private boolean[] bossWon;

    public Game() throws IOException {
        this.fw = null;
        this.fr = null;
        this.model = new Model();
        mapsFile = new File("maps2.txt");
        if(!mapsFile.exists()){
            throw new IOException("Game File Missing, Game Corrupted");
        }
        this.model.loadMaps(new FileReader(mapsFile));
        this.model.generateMaps();
        this.bossWon = new boolean[3];
        model.activatePersonFactory();
        model.setUpMainParty();
    }

    public void setUpControllerView(ControllerView vc){
        this.vc = vc;
        model.setUpViewController(vc);
    }

    public Model getModel(){
        return model;
    }

    public void saveGame() throws IOException{
        ObjectOutputStream file = null;
        try{
            file = new ObjectOutputStream(
                    new BufferedOutputStream(new FileOutputStream(
                            "./savedGame.txt")));
            MainCharacter mc = (MainCharacter) this.model.getPersonByClass
                    (MainCharacter.class.toString(),0);
            file.writeObject(mc);
        }catch(IOException e){
            e.getMessage(); 
        }finally {
            if (file != null) {
                file.close();
            }
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException{
        out.defaultWriteObject();
        this.bossWon = this.model.getBossWon();
        String currentMap =
                this.model.getMapHandler().getCurrentMap().getName();
        out.writeObject(bossWon[0]);
        out.writeObject(bossWon[1]);
        out.writeObject(bossWon[2]);
    }

    public static void loadGame()throws IOException, ClassNotFoundException{
        ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(
                        "savedGame.txt")));
        boolean[] bossWon = new boolean[3];
        String currentMap = null;
        MainCharacter mc = null;
        try{
            Game aux = new Game();
            mc = aux.readData(in, currentMap);
        }catch(IOException e){
            e.getMessage();
        }finally {
            in.close();
        }
    }

    private MainCharacter readData(ObjectInputStream in, String currentMap)
            throws IOException, ClassNotFoundException{
        MainCharacter mc = (MainCharacter) in.readObject();
        this.bossWon[0] = in.readBoolean();
        this.bossWon[1] = in.readBoolean();
        this.bossWon[2] = in.readBoolean();
        currentMap = (String) in.readObject();
        return mc;
    }
}
