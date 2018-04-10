package patternRecognition;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class LocatePatternAutomated {

    private static String pythonCommand="python3";

    public static int[] LocatePattern(int[] zoneToPerformLocalisation){
        String data;
        File file = new File("/tmp/Localization.done");
        if (file.exists()) {
            file.delete();
        }
        MakeLocalization();
        try{
            data = new String(Files.readAllBytes(Paths.get("/tmp/LocalizationInfo.txt")));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("LocatePatternAutomated > IOException /tmp/LocalizationInfo.txt");
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
        command.add(pythonCommand);
        command.add("./src/patternRecognition/LocatePatternAutomated.py");
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.inheritIO();
        Process p = null;
        try {
            p = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("LocatePatternAutomated > Erreur processBuilder");
        }
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("LocatePatternAutomated > Erreur waitfor");
        }
        File f = new File("/tmp/Localization.done");
        while(!f.exists()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("LocatePatternAutomated > Erreur interruptedException");
            }
        }
        return;
    }
}
