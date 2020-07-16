package application.diff;

import application.util.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class DiffGroup<V>{

	private final List<V> added = new ArrayList<>();
	private final List<V> removed = new ArrayList<>();
	private final List<V> kept = new ArrayList<>();
	private final BiFunction<V,V,Boolean> equalityFunction;

	public DiffGroup(List<V> initial, List<V> current, BiFunction<V,V,Boolean> equalityFunction){
		this.equalityFunction = equalityFunction;

		initial.forEach(v -> {
			boolean wasKept = current.stream().anyMatch(v2 -> equalityFunction.apply(v,v2));
			if(wasKept){
				kept.add(v);
			}
			else{
				removed.add(v);
			}
		});
		current.forEach(v -> {
			boolean wasAdded = initial.stream().noneMatch(v2 -> equalityFunction.apply(v,v2));
			if(wasAdded){
				added.add(v);
			}
		});
	}

	public List<V> getAdded(){
		return added;
	}

	public List<V> getRemoved(){
		return removed;
	}

	public List<V> getKept(){
		return kept;
	}

	public int getSize(){
		return added.size()+removed.size()+kept.size();
	}

	public List<V> getAllValues(){
		List<V> values = new ArrayList<>();
		values.addAll(added);
		values.addAll(removed);
		values.addAll(kept);
		return values;
	}

	public List<Tuple<V,GroupDiffType>> getAllValuesWithDiff(){
		List<Tuple<V,GroupDiffType>> values = new ArrayList<>();
		added.forEach(v -> values.add(new Tuple<>(v,GroupDiffType.ADD)));
		removed.forEach(v -> values.add(new Tuple<>(v,GroupDiffType.REMOVE)));
		kept.forEach(v -> values.add(new Tuple<>(v,GroupDiffType.UNCHANGED)));
		return values;
	}

	public GroupDiffType getDiffType(V value){
		boolean wasAdded = added.stream().anyMatch(v -> equalityFunction.apply(v,value));
		if(wasAdded){
			return GroupDiffType.ADD;
		}

		boolean wasRemoved = removed.stream().anyMatch(v -> equalityFunction.apply(v,value));
		if(wasRemoved){
			return GroupDiffType.REMOVE;
		}

		boolean wasKept = kept.stream().anyMatch(v -> equalityFunction.apply(v,value));
		if(wasKept){
			return GroupDiffType.UNCHANGED;
		}

		return null;
	}

	public boolean startsWithNoElements(){
		return (removed.size()+kept.size() == 0);
	}
	public boolean endsWithNoElements(){
		return (added.size()+kept.size() == 0);
	}

	public GroupDiffType getGeneralDiff(){
		if(startsWithNoElements()) return GroupDiffType.ADD;
		else if(endsWithNoElements()) return GroupDiffType.REMOVE;
		else return GroupDiffType.UNCHANGED;
	}

	@Override public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}

		DiffGroup<?> diffGroup = (DiffGroup<?>) o;

		if(added.size() != diffGroup.getAdded().size() || removed.size() != diffGroup.getRemoved().size() || kept.size() != diffGroup.getKept().size()){
			return false;
		}

		if(!added.containsAll(diffGroup.getAdded()) || !removed.containsAll(diffGroup.getRemoved()) || !kept.containsAll(diffGroup.getKept())){
			return false;
		}

		return true;
	}

	@Override public int hashCode(){
		return 1;//TODO
	}
}
