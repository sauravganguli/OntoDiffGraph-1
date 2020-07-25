package application.github;

public class GitHubJsonObject {
    private String fileName;
    private String filePath;
    private String fileType;
    private String downloadURL;

    public GitHubJsonObject() {
    }

    public GitHubJsonObject(String fileName, String filePath, String fileType, String downloadURL) {
        this.fileName = removeQuotes(fileName);
        this.filePath = removeQuotes(filePath);
        this.fileType = removeQuotes(fileType);
        this.downloadURL = removeQuotes(downloadURL);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    private String removeQuotes(String str){
        return str.replace("\"", "");
    }
}
