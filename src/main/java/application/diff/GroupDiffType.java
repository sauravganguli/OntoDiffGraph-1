package application.diff;

public enum GroupDiffType{
	ADD,REMOVE,UNCHANGED;

	public boolean diffOccurred(){
		switch(this){
			case ADD: return true;
			case REMOVE: return true;
			case UNCHANGED: return false;
		}
		return false;
	}
}
