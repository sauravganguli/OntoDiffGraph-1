package application.ui;

import application.util.Vars;
import javafx.scene.control.ListCell;

public class FileDiffList extends ListCell<FileDiffListData>{

	public FileDiffList(){
		super();
	}

	@Override
	protected void updateItem(FileDiffListData item, boolean empty){
		super.updateItem(item,empty);

		//Colors get mixed up without this, don't know why
		getStyleClass().removeAll(Vars.LISTCELL_ADD_CSS_CLASS,Vars.LISTCELL_EDIT_CSS_CLASS,Vars.LISTCELL_REMOVE_CSS_CLASS);

		if(!empty && item != null){
			setText(item.getFileName());
			if(item.getCssColorClass() != null){
				getStyleClass().add(item.getCssColorClass());
			}
		}
		else{
			setText(null);
		}
	}

}
