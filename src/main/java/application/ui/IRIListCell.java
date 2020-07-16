package application.ui;

import application.util.Vars;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;

public class IRIListCell extends ListCell<IRIListCellData>{

	@Override
	protected void updateItem(IRIListCellData item, boolean empty){
		super.updateItem(item,empty);

		//Colors get mixed up without this, don't know why
		getStyleClass().removeAll(Vars.LISTCELL_ADD_CSS_CLASS,Vars.LISTCELL_EDIT_CSS_CLASS,Vars.LISTCELL_REMOVE_CSS_CLASS);

		if(!empty && item != null){
			setText(item.getValue().getIRI().getShortForm());
			setTooltip(new Tooltip(item.getValue().getIRI().getIRIString()));

			if(item.getCssColorClass() != null){
				getStyleClass().add(item.getCssColorClass());
			}
		}
		else{
			setText(null);
			setTooltip(null);
		}
	}
}
