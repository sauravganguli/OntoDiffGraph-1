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
import java.util.List;

public class GitHubRequest {

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

    private String searchFileInDir(String request, String searchedFileName){
        String response = getGitHubFilesListRequest(request);
        if(response.contains("Not Found"))
            return "";
        List<GitHubResultList> gitHubResultList = getFileFolderListThroughJson(response);
        for (GitHubResultList entity : gitHubResultList){
            if (entity.isFile() && entity.getFileName().equals(searchedFileName))
                return entity.getDownloadURL();

            if(!entity.isFile()){
                String result = searchFileInDir(request + entity.getFileName() + "/", searchedFileName);
                if (!result.equals(""))
                    return result;
            }
        }
        return "";
    }

    private ArrayList<GitHubResultList> getFileFolderListThroughJson(String response){
        ArrayList<GitHubResultList> result = new ArrayList<>();
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(response);
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        for (int i = 0; i<jsonArray.size(); i++){
            JsonObject objects = jsonArray.get(i).getAsJsonObject();
            result.add(new GitHubResultList(
                    objects.get("name").toString(),
                    objects.get("path").toString(),
                    objects.get("type").toString(),
                    objects.get("download_url").toString()
            ));
        }
        return result;
    }


    private File downloadFile(String ontologyDownloadLink){
        // TODO: Uncomment If you need to store every local copy of the new ontology from github
        // final String localFileName = new SimpleDateFormat("yyyyMMddHHmm'ontology.owl'").format(new Date());

        // TODO: For now, there is only one ontology will be stored (local copy from the GitHub one)
        final String localFilePath = "src"+ File.separatorChar + "main" + File.separatorChar + "resources"+ File.separatorChar +
                "ontologies";
        final String ontologyName = "ontologyFromGitHub.owl";


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
        String downloadLink = searchFileInDir(initialRequest, credentials.getOntoName());
        if (downloadLink.equals(""))
            return null;

        return downloadFile(downloadLink);
    }
}
