package domain.models;

public class MSISDNRedirection {

	private int id, code;
	private String type, expression;
	private String redirection_url;
	
	public MSISDNRedirection() {

	}
	
	public MSISDNRedirection(int id, int code, String type, String expression, String redirection_url) {
		this.id = id;
		this.code = code;
		this.type = type;
		this.expression = expression;
		this.redirection_url = redirection_url;
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

	public String getRedirection_url() {
		return redirection_url;
	}

	public void setRedirection_url(String redirection_url) {
		this.redirection_url = redirection_url;
	}

}
