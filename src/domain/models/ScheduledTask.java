package domain.models;

public class ScheduledTask {

	private int id, code;
	private boolean flag;
	private String stepExecution;
	private int hour, minute;

	public ScheduledTask(int id, int code, String stepExecution, int hour, int minute, boolean flag) {
		this.id = id;
		this.code = code;
		this.stepExecution = stepExecution;
		this.hour = hour;
		this.minute = minute;
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

	public String getStepExecution() {
		return stepExecution;
	}

	public void setStepExecution(String stepExecution) {
		this.stepExecution = stepExecution;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

}
