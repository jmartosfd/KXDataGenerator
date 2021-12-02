package telconomics.rdg.utils;

public class IMSIGenerator {

    private static double IMSILength = 1.0E14;

    public static double generateIMSI(int id){
        return IMSILength+(double)id;
    }

}
