package apt.ToDoList;

import java.util.ArrayList;

public class ToDoItem {

	private String title = "";
	private String description = "";
	private int id = - 1;
	private int parent_id = -1;
	private ToDoItem parent = null;
	private ArrayList< ToDoItem > subToDos = null;
	
	public ToDoItem(int id, int parent_id) {
		this.id = id;
		this.parent_id = parent_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle( String title ) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription( String description ) {
		this.description = description;
	}

	public ArrayList< ToDoItem > getSubToDos() {
		return subToDos;
	}

	public void setSubToDos( ArrayList< ToDoItem > items ) {
		subToDos = items;
	}

	public void addSubItem( ToDoItem item ) {
		subToDos.add( item );
	}

	public ToDoItem getParent() {
		return parent;
	}

	public void setParent( ToDoItem parent ) {
		this.parent = parent;
	}

	public int getId() {
		return id;
	}

	public void setId( int id ) {
		this.id = id;
	}
	
	public int getParentId(){
		return parent_id;
	}
	
	public void setParentId(int parent_id){
		this.parent_id = parent_id;
	}

}
