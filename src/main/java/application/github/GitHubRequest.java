package application.github;

import application.util.Vars;
import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
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

public class GitHubRequest {

    private Credentials credentials;
    private String initialRequest;

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
        initialRequest = MessageFormat.format("https://api.github.com/repos/{0}/{1}/contents/",
                credentials.getUserName(), credentials.getUserRepo());
    }


    public GitHubRequest() {

    }

    private String sendRequest(String fileName, String requestString){

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(requestString);
        request.addHeader(Vars.GITHUB_TOKEN_PARAMETER_NAME, Vars.GITHUB_TOKEN);
        try (CloseableHttpResponse response = httpClient.execute(request)) {

            // Get HttpResponse Status
//            System.out.println(response.getStatusLine().toString());

            HttpEntity entity = response.getEntity();
            Header headers = entity.getContentType();
            System.out.println(headers);

            // return it as a String
            String result = EntityUtils.toString(entity);
            GitHubJsonParser.getJsonObjects(result);

//            System.out.println(result);

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

        String localFileName = GitHubJsonParser.getDownloadUrlByFileName(fileName);
        if (!localFileName.equals("")){
            return localFileName;
        }

        String localFolderPath = GitHubJsonParser.getFolderPath();
        if(!localFolderPath.equals("")){
            requestString = initialRequest + localFolderPath+ "/";
        }
        else requestString = initialRequest;
//        requestString = !localFolderPath.equals("") ? initialRequest + localFolderPath + "/" : initialRequest;

        return sendRequest(fileName, requestString);
    }

    private File downloadFile(String fileName){
        // TODO: Uncomment If you need to store every local copy of the new ontology from github
        // final String localFileName = new SimpleDateFormat("yyyyMMddHHmm'ontology.owl'").format(new Date());

        // TODO: For now, there is only one ontology will be stored (local copy from the GitHub one)
        final String localFileName = "resources/ontologies/localontology.owl";

        URL remoteFile;
        File localFile = new File(localFileName);
        int connectionTimeout = 10000;
        int readTimeout = 10000;

        try {
            remoteFile = new URL(sendRequest(fileName,initialRequest));
            FileUtils.copyURLToFile(remoteFile, localFile, connectionTimeout, readTimeout);
        } catch (IOException e) {
            Logger.getRootLogger().error("Error in obtaining the file " + e.getMessage());
            e.printStackTrace();
        }
        return localFile;
    }

    public File getGithubLocalFilePath(){

        return downloadFile(credentials.getOntoName());
    }
}
