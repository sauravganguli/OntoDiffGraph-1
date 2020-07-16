package application.ui;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class ListViewFilter<T>{

	private final List<T> fullList = new ArrayList<>();
	private final ListView<T> listView;
	private final TextField textField;
	private final BiFunction<T,String,Boolean> filterFunction;
	private final ChangeListener<String> listener;

	public ListViewFilter(ListView<T> listView,TextField textField, BiFunction<T,String,Boolean> filterFunction){
		this.listView = listView;
		this.textField = textField;
		this.filterFunction = filterFunction;
		this.listener = (observable,oldValue,newValue) -> {
			listView.getItems().clear();
			listView.getItems().addAll(
					fullList.stream().filter(item -> filterFunction.apply(item,newValue)).collect(Collectors.toList())
			);
		};
	}

	public void startFilter(){
		fullList.addAll(listView.getItems());
		textField.textProperty().addListener(listener);
	}

	public void stopFilter(){
		fullList.clear();
		textField.textProperty().removeListener(listener);
	}
}
