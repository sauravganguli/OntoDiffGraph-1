package application.diff;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileDiff {
    private List<File> localFileList;
    private List<File> gitHubFileList;

    public FileDiff(List<File> localFileList, List<File> gitHubFileList) {
        this.localFileList = localFileList;
        this.gitHubFileList = gitHubFileList;
    }

//    public List<File> getCorrectElementOntology(GroupDiffType diffType){
//        switch(diffType){
//            case ADD: return localFileList;
//            case REMOVE: return gitHubFileList;
//            case UNCHANGED: return gitHubFileList;
//            default: return gitHubFileList;
//        }
//    }

    public List<File> getLocalFileList() {
        return localFileList;
    }

    public void setLocalFileList(List<File> localFileList) {
        this.localFileList = localFileList;
    }

    public List<File> getGitHubFileList() {
        return gitHubFileList;
    }

    public void setGitHubFileList(List<File> gitHubFileList) {
        this.gitHubFileList = gitHubFileList;
    }

    public DiffGroup<File> compareFilesList(){
        return new DiffGroup<>(
                new ArrayList<>(localFileList),
                new ArrayList<>(gitHubFileList),
                File::equals);
    }
}
