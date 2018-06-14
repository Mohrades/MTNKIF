package domain.models;

public class ServiceAccess {

	private int id, code;
	private boolean flag;
	private String username, password;

	public ServiceAccess(int id, int code, String username, String password, boolean flag) {
		this.id = id;
		this.code = code;
		this.username = username;
		this.password = password;
		this.flag = flag;
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

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
