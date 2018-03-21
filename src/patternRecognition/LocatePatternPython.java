package patternRecognition;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class LocatePatternPython {

    public static int[] LocatePattern(int[] zoneToPerformLocalisation, String orientation){
        String data;
        MakeLocalization();
        try {
            data = new String(Files.readAllBytes(Paths.get("/tmp/LocalizationInfo.txt")));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("LocatePatternPython > IOException /tmp/LocalizationInfo.info");
            data="-1 -1 10000 10000";
        }
        String[] infos = data.split(" ");
        int[] coords=new int[4];
        for (int i=0; i<4; i++){
            coords[i]=Integer.parseInt(infos[i]);
        }
        if (coords[0]==-1 && coords[1]==-1 && coords[2]==10000 && coords[3]==10000){
            return new int[]{0,0,0,0};
        }
        else {
            return coords;
        }
    }

    private static void MakeLocalization(){
        List<String> command = new ArrayList<>();
        command.add("python");
        command.add("./src/patternRecognition/LocatePatternPython.py");
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.inheritIO();
        Process p = null;
        try {
            p = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("LocatePatternPython > Erreur processBuilder");
        }
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("LocatePatternPython > Erreur waitfor");
        }
        File f = new File("/tmp/LocalizationDone.lock");
        while(!f.exists()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("LocatePatternPython > Erreur interruptedException");
            }
        }
        return;
    }
}
