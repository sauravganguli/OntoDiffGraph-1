package application.ui;

import java.util.Date;

public class FileDiffListData {

	private String fileName;
	private Date fileChangedDateTime;
	private String cssColorClass;

	public FileDiffListData(String fileName, Date fileChangedDateTime, String cssColorClass) {
		this.fileName = fileName;
		this.fileChangedDateTime = fileChangedDateTime;
		this.cssColorClass = cssColorClass;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getFileChangedDateTime() {
		return fileChangedDateTime;
	}

	public void setFileChangedDateTime(Date fileChangedDateTime) {
		this.fileChangedDateTime = fileChangedDateTime;
	}

	public String getCssColorClass() {
		return cssColorClass;
	}

	public void setCssColorClass(String cssColorClass) {
		this.cssColorClass = cssColorClass;
	}
}
