package apt.ToDoList;

public class ToDoItem {

	private String title = "";
	private String description = "";
	private int id = - 1;
	private int parent_id = -1;
	private ToDoItemHelper helper = null;
	
	public ToDoItem(int id) {
		this.id = id;		
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
