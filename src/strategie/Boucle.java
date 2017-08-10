package strategie;

import java.util.ArrayList;

/**
 * Created by shininisan on 14/12/16.
 */
public class Boucle implements Runnable{
    private int indice;
    private int indiceEnd;
    private int pas;
    private ArrayList<Runnable> instr;
public Boucle(int indiceIni, int indiceFin, ArrayList<Runnable> instr,int pas)
{
    indice=indiceIni;
    indiceEnd=indiceFin;
    this.instr=instr;


}
    @Override
    public void run() {
        try {

            for (; indice < indiceEnd; indice+=pas) {


            }
        }
        catch (Exception e)
        {
            //appeller l'exception manager?
        }

    }
}
