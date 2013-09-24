package cn.edu.nju.software;

import java.util.ArrayList;
import java.util.HashMap;



public class PreLog {
	public String[] taskName;
//	public int[][] taskTable = null ;
	public LogRecord[][] workflow = null;
//	public int[][] preList = null;
	public int[] start;
	public int[] end;
	public HashMap<String, ArrayList<Integer>> oneCycleHash = null;
	public PreLog(String[] tn, LogRecord[][] wf, HashMap<String, ArrayList<Integer>> och) {
		taskName = tn;
//		taskTable = tl;
		workflow = wf;
		oneCycleHash = och;
	}
	
}
