package application.github;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class GitHubJsonParser {

    private final static List<GitHubJsonObject> gitHubJsonObjectArrayList = new ArrayList<>();
    private final static ArrayList<String> pathPassed = new ArrayList<>();

    public GitHubJsonParser() {

    }

    private static boolean isPathPassed(String path){
        return pathPassed.contains(path);
    }

    public static void getJsonObjects(String response){
        gitHubJsonObjectArrayList.clear();
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(response);
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        for (int i = 0; i<jsonArray.size(); i++){
            JsonObject objects = jsonArray.get(i).getAsJsonObject();
            gitHubJsonObjectArrayList.add(new GitHubJsonObject(
                    objects.get("name").toString(),
                    objects.get("path").toString(),
                    objects.get("type").toString(),
                    objects.get("download_url").toString()
            ));
        }
    }

    public static String getDownloadUrlByFileName(String fileName){
        String result = "";
        if (gitHubJsonObjectArrayList.isEmpty()) return result;
        for (GitHubJsonObject object : gitHubJsonObjectArrayList){
            if (object.getFileName().equals(fileName)) {
                result = object.getDownloadURL();
                return result;
            }
        }

        return result;
    }

    public static String getFolderPath(){
        String result = "";
        if (gitHubJsonObjectArrayList.isEmpty()) return result;
        for (GitHubJsonObject object : gitHubJsonObjectArrayList){

            if(isPathPassed(object.getFilePath())){
                continue;
            }
            if (object.getFileType().equals("dir")) {
                result = object.getFilePath();
                pathPassed.add(result);
                return result;
            }
        }
        return result;
    }

}
