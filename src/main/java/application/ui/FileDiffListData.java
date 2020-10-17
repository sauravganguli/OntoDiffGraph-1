package application.ui;

import java.io.File;
import java.util.Date;

public class FileDiffListData {

	private File file;
	private String cssColorClass;

	public FileDiffListData(File file, String cssColorClass) {
		this.file = file;
		this.cssColorClass = cssColorClass;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getCssColorClass() {
		return cssColorClass;
	}

	public void setCssColorClass(String cssColorClass) {
		this.cssColorClass = cssColorClass;
	}
}
