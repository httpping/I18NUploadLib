package tp.xml.utils;

import java.io.File;
import java.util.HashMap;

public class FileParse {

    /**
     * 解析
     * @param rootPath
     * @return
     */
    public static HashMap<String,String> parse(String rootPath){

        String path ="/app/build/intermediates/incremental";
        File file =new File(rootPath +path);

        File[] files =  file.listFiles();

        File mergeFile = null;
        for (File mege : files){

            if (mege.getName().contains("merge") && mege.getName().contains("Resources")){
                mergeFile = mege;
            }
        }

        String mergeDirStr = mergeFile.getAbsolutePath() +"/merged.dir";

        //找到对应目录了
        File mergeDir = new File(mergeDirStr);

        files = mergeDir.listFiles();

        HashMap<String,String> maps = new HashMap<String, String>();

        for (File valuesI18 : files){
            String pathName = valuesI18.getName();
            if (pathName.equals("values")){
                maps.put("en", valuesI18.listFiles()[0].getAbsolutePath());
            }else {
                String lang =pathName.replace("values-","");
                if (!lang.equals("en")){
                    maps.put(lang, valuesI18.listFiles()[0].getAbsolutePath());
                }
            }
        }
        return maps;
    }
}
