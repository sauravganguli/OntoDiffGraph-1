package application.github;

import java.io.File;

public class Credentials {

    private String userName;
    private String userRepo;
    private String ontoName;
    private File localFile;
    private String errorMessage;

    public Credentials() {
        this.errorMessage = "";
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


    public File getLocalFile() {
        return localFile;
    }

    public void setLocalFile(File localFile) {
        this.localFile = localFile;
    }

    public Credentials(String userName, String userRepo, String ontoName, File localFile) {
        this.userName = userName;
        this.userRepo = userRepo;
        this.ontoName = ontoName;
        this.localFile = localFile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRepo() {
        return userRepo;
    }

    public void setUserRepo(String userRepo) {
        this.userRepo = userRepo;
    }

    public String getOntoName() {
        return ontoName;
    }

    public void setOntoName(String ontoName) {
        this.ontoName = ontoName;
    }

    public boolean isValid(){
        return !userName.equals("") && !userRepo.equals("") && !ontoName.equals("") && localFile != null;
    }


}
