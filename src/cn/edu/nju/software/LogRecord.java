package cn.edu.nju.software;


public class LogRecord implements Comparable<LogRecord> {
	public int taskId;
	public int[] preTask;
	public int taskIndex;
	
	public LogRecord(int id, int[] pre, int index) {
		taskId = id;
		preTask = pre;
		taskIndex = index;
	}
	
	public int compareTo(LogRecord o) {
		// TODO Auto-generated method stub
		return this.taskId-o.taskId;
	}
	public String toString() {
		return Integer.toString(taskId);
	}
}