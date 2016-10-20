package Model;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Junior on 19-10-16.
 */
public class Bucket extends ArrayList<Stacktrace> {

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bucket() {
    }

    public void fill(File[] directoryStacktrace, int id) {
        this.id = id;

        // Pour chaque dossier de Stacktrace
        for(File directory : directoryStacktrace)
        {
            System.out.println(Integer.parseInt(directory.getName()));
            System.out.println();
            Stacktrace stackTrace = new Stacktrace();
            stackTrace.fill(directory.listFiles()[0], Integer.parseInt(directory.getName()));
            this.add(stackTrace);
        }
    }
}
