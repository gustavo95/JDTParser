package Parse;

public class MethodInformations {
	
	private int id;
	private String name;
	private String methodClass;
	private int startLine;
	private int endLine;
	private int length;
	
	public MethodInformations(String name, String methodClass, int startLine, int endLine) {
		super();
		this.name = name;
		this.methodClass = getClassNameByLocation(methodClass);
		this.startLine = startLine;
		this.endLine = endLine;
		this.length = endLine - startLine + 1;
	}
	
	public String getClassNameByLocation(String location){
		String aux = location.replace("\\", "");
		int i = location.length() - aux.length();
		return location.split("\\\\")[i].replace(".java", "");
	}

	public String getName() {
		return name;
	}

	public String getMethodClass() {
		return methodClass;
	}

	public int getStartLine() {
		return startLine;
	}

	public int getEndLine() {
		return endLine;
	}

	public int getLength() {
		return length;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
