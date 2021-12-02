package telconomics.rdg.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class FileUtils {


    public static Reader resourceStringToReader(String stringResourceLocation){
        InputStream is = FileUtils.class.getClassLoader().getResourceAsStream(stringResourceLocation);
        return new InputStreamReader(is);
    }


    public static Writer writeFileInResources(String fileName){

        Paths.get("src","main","resources",fileName);

        try {
            Path path = Paths.get("src","main","resources",fileName);
            return Files.newBufferedWriter(path);

        } catch ( IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
