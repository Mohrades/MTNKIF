package domain.models;

public class MSISDNRedirection {

	private int id, code;
	private String type, expression;
	
	public MSISDNRedirection() {

	}
	
	public MSISDNRedirection(int id, int code, String type, String expression) {
		this.id = id;
		this.code = code;
		this.type = type;
		this.expression = expression;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

}
