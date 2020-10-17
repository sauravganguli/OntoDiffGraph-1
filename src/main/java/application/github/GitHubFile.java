package application.github;

import java.util.Date;

public class GitHubFile {
    private String fileName;
    private String downloadLink;

    public GitHubFile(String fileName, String downloadLink) {
        this.fileName = fileName;
        this.downloadLink = downloadLink;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }


}
