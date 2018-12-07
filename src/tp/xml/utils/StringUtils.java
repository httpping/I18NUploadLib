package tp.xml.utils;

public class StringUtils {

    public static boolean isEmpty(String string){
        if (string == null || string.equals("")){
            return true;
        }
        return false;
    }
}
