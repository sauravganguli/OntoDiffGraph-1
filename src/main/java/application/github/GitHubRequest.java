package application.github;

import application.util.Vars;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GitHubRequest {

    private final List<GitHubFile> downloadLinks = new ArrayList<>();

    private String getGitHubFilesListRequest(String requestString){
        String result = "";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(requestString);
        request.addHeader(Vars.GITHUB_TOKEN_PARAMETER_NAME, Vars.GITHUB_TOKEN);
        try (CloseableHttpResponse response = httpClient.execute(request)) {

            HttpEntity entity = response.getEntity();

            // return it as a String
            result = EntityUtils.toString(entity);

        } catch (IOException e) {
            Logger.getRootLogger().error(e.getMessage());
            e.printStackTrace();
        }
        finally {
            if(httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * If it is needed to search by whole word than @completelyName flag is true
     * If it is needed to search by partial file name, then @completelyName false
     * */
    private void searchFileInDir(String request, String searchedFileName){
        String response = getGitHubFilesListRequest(request);
        if(response.contains("Not Found"))
            return;
        List<GitHubObject> gitHubObject = getFileFolderListThroughJson(response);
        for (GitHubObject entity : gitHubObject){

            if (entity.isFile() && entity.getFileName().contains(searchedFileName)) {
                downloadLinks.add(new GitHubFile(entity.getFileName(), entity.getDownloadURL()));

                // If it is searched by whole name than check is file name completely equals and exit function
                if(entity.getFileName().equals(searchedFileName)) return;
                else continue;
            }

            if(!entity.isFile()){
                searchFileInDir(request + entity.getFileName() + "/", searchedFileName);
//                if (!result.equals(""))
//                    return result;
            }
        }
    }

    private ArrayList<GitHubObject> getFileFolderListThroughJson(String response){
        ArrayList<GitHubObject> result = new ArrayList<>();
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(response);
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        for (int i = 0; i<jsonArray.size(); i++){
            JsonObject objects = jsonArray.get(i).getAsJsonObject();
            result.add(new GitHubObject(
                    objects.get("name").toString(),
                    objects.get("path").toString(),
                    objects.get("type").toString(),
                    objects.get("download_url").toString()
            ));
        }
        return result;
    }


    private File downloadFile(String ontologyDownloadLink, String ontologyName){
        // TODO: Uncomment If you need to store every local copy of the new ontology from github
        // final String localFileName = new SimpleDateFormat("yyyyMMddHHmm'ontology.owl'").format(new Date());

        // TODO: For now, there is only one ontology will be stored (local copy from the GitHub one)
        final String localFilePath = "src"+ File.separatorChar + "main" + File.separatorChar + "resources"+ File.separatorChar +
                "ontologies";
        //final String ontologyName = "ontologyFromGitHub.owl";


        File dir = new File(localFilePath);
        if (!dir.exists()) {
            boolean dirCreated = dir.mkdirs();
            if (dirCreated)
                Logger.getRootLogger().info("New folders was created");
        }
        File localOntologyFilePath = new File(dir.getAbsolutePath() + File.separatorChar + ontologyName);

        int connectionTimeout = 10000;
        int readTimeout = 10000;

        try {
            URL remoteFile = new URL(ontologyDownloadLink);
            FileUtils.copyURLToFile(remoteFile, localOntologyFilePath, connectionTimeout, readTimeout);
        } catch (IOException e) {
            Logger.getRootLogger().error("Error in obtaining the file " + e.getMessage());
            e.printStackTrace();
        }
        return localOntologyFilePath;
    }

    public File getGithubLocalFilePath(Credentials credentials){
        String initialRequest = MessageFormat.format("https://api.github.com/repos/{0}/{1}/contents/",
                credentials.getUserName(), credentials.getUserRepo());
        // call of recursive function
        searchFileInDir(initialRequest, credentials.getOntoName());
        if (downloadLinks.isEmpty())
            return null;

        // return first file with the pointed name
        return downloadFile(downloadLinks.get(0).getDownloadLink(), downloadLinks.get(0).getFileName());
    }

    public List<File> getGithubLocalFolderFileList(Credentials credentials){
        List<File> result = new ArrayList<>();
        String initialRequest = MessageFormat.format("https://api.github.com/repos/{0}/{1}/contents/",
                credentials.getUserName(), credentials.getUserRepo());

        // Default ontology name value is always contains statements with comma, which are separate searched extensions
        List<String> extensionList = Arrays.asList(credentials.getOntoName().split(","));
        extensionList.forEach(v->searchFileInDir(initialRequest, v));
        if (downloadLinks.isEmpty())
            return null;

        // download all files from GitHub repository
        downloadLinks.forEach(v -> result.add(downloadFile(v.getDownloadLink(), v.getFileName())));

        return result;
    }
}
