package application.diff;

import application.ui.FileDiffListData;

import java.util.List;
import java.util.stream.Collectors;

public class FileDiff {
    private List<FileDiffListData> localFileList;
    private List<FileDiffListData> gitHubFileList;

    public FileDiff(List<FileDiffListData> localFileList, List<FileDiffListData> gitHubFileList) {
        this.localFileList = localFileList;
        this.gitHubFileList = gitHubFileList;
    }

    public List<FileDiffListData> getLocalFileList() {
        return localFileList;
    }

    public void setLocalFileList(List<FileDiffListData> localFileList) {
        this.localFileList = localFileList;
    }

    public List<FileDiffListData> getGitHubFileList() {
        return gitHubFileList;
    }

    public void setGitHubFileList(List<FileDiffListData> gitHubFileList) {
        this.gitHubFileList = gitHubFileList;
    }

    public List<FileDiffListData> compareFilesList(){
        return new DiffGroup<>(
                localFileList.stream().collect(Collectors.toList()),
                gitHubFileList.stream().collect(Collectors.toList()),
                (v1, v2) -> v1.getFileName().equals(v2.getFileName()));
    }
}
