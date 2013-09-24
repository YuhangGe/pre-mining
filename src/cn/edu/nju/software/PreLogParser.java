package cn.edu.nju.software;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;


public class PreLogParser {
	private static int idx;
	private static int end;
	private static char chr;
	
	private static String str;
	
	private static HashMap<String, Integer> taskMap = null;
//	private static ArrayList<ArrayList<Integer>> taskTable = null;
	private static ArrayList<String> taskName = null;
	private static ArrayList<Boolean> isEnd = null;
	private static ArrayList<Boolean> isStart = null;
	
	private static ArrayList<ArrayList<LogRecord>> workflow = null;
	
	private static ArrayList<Integer> oneCycleList = null;
	private static ArrayList<WFPair> oneCyclePair = null;
	
	private static int TASK_ID = 0;
	
	public static PreLog parseFromStream(InputStream inputStream) {
		
		try {
			
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
		    String line = null;
		    StringBuilder sb = new StringBuilder();
		    while((line=br.readLine())!=null) {
		    	sb.append(line).append('\n');
		    }
			str = sb.toString();
			br.close();
			
			taskMap = new HashMap<String, Integer>();
//			taskTable = new ArrayList<ArrayList<Integer>>();
			taskName = new ArrayList<String>();
			workflow = new ArrayList<ArrayList<LogRecord>>();
			oneCycleList = new ArrayList<Integer>();

			oneCyclePair = new ArrayList<WFPair>();
			
			isEnd = new ArrayList<Boolean>();
			isStart = new ArrayList<Boolean>();
			
			PreLog pl = doParse();
			
			str = null;
			taskMap = null;
//			taskTable.clear();
//			taskTable = null;
			workflow.clear();
			workflow = null;
			isEnd.clear();
			isEnd = null;
			isStart.clear();
			isStart = null;
			oneCycleList.clear();
			oneCycleList = null;
			oneCyclePair.clear();
			oneCyclePair = null;
			
			return pl;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	private static char read() {
		chr = '\0';
		if(idx<end) {
			chr = str.charAt(idx);
			idx++;
		}
		return chr;
	}
	private static String readWord() {
		StringBuilder sb = new StringBuilder();
		while(chr!='\0' && ((chr>='A'&&chr<='Z')||(chr>='a' && chr<='z')||(chr>='0' && chr<='9')||chr=='_'||chr=='-')) {
			sb.append(chr);
			read();
		}
		return sb.toString();
	}
	private static void _skip() {
		while(chr!='\0' && !((chr>='A'&&chr<='Z')||(chr>='a' && chr<='z')||(chr>='0' && chr<='9')||chr=='\n'||chr=='_'||chr=='-'||chr==','||chr=='['||chr==']')) {
			read();
		}
	}
	
	private static void _line() {
		ArrayList<LogRecord> wf = new ArrayList<LogRecord>();
		
		while(chr != '\0') {
			_each(wf);
			if(chr=='\n') {
				read();
				_skip();
				break;
			}
		}
		
//		LogRecord[] logs = new LogRecord[wf.size()];
//		wf.toArray(logs);
//		Arrays.sort(logs);
		
		workflow.add(wf);
	}
	
	
	private static void _each(ArrayList<LogRecord> wf) {
		if(chr!='[') {
			return;
		} else {
			read();
			_skip();
		}
		
		ArrayList<Integer> _pre_list = new ArrayList<Integer>();
		String w;
		int task_idx;
		while(chr!='\0' && chr != ']') {
			
			w = readWord();//pre tasks
			task_idx = putTask(w);
			//一但该task出现在了某task的前驱中，说明该task有后继，则不可能是end task
			isEnd.set(task_idx, false);
			
			if(!_pre_list.contains(task_idx)) {
				_pre_list.add(task_idx);
			}
			
			_skip();
			
			if(chr==']') {
				read();
				_skip();
				break;
			} else if(chr==',') {
				read();
			}
			
			_skip();
		}
		
		if(chr==']') {
			read();
			_skip();
		}
		
		w = readWord(); //task
		task_idx = putTask(w);
		LogRecord lr = new LogRecord(task_idx, new int[_pre_list.size()], wf.size());
		//pr("task:" + w+" , "+task_idx);
		
		//pr("wf add: "+w+","+task_idx);
		if(_pre_list.size()>0) {
			//如果前驱不为空，则不可能是start节点
			isStart.set(task_idx, false);
		}
		
//		ArrayList<Integer> pList = taskTable.get(task_idx);
		for(int i=0;i<_pre_list.size();i++) {
//			task_idx = _pre_list.get(i);
//			if(!pList.contains(task_idx)) {
//				pList.add(task_idx);
//			}
			lr.preTask[i] = _pre_list.get(i);
		}
		
		//检测当前是不是单循环
		if(lr.preTask.length==1 && lr.preTask[0]==lr.taskId) {
			if(!oneCycleList.contains(lr.taskId)) {
				oneCycleList.add(lr.taskId);
				oneCyclePair.set(lr.taskId, new WFPair());
			}
		} else {
			Arrays.sort(lr.preTask);
			wf.add(lr);
		}
		
		
		
		_skip();
		if(chr==',') {
			read();
			_skip();
		}
		
		
	}
	private static PreLog doParse() {
		idx = 0;
		end = str.length()-1;
		
		TASK_ID = -1;
		
		read();
		_skip();
		while(chr != '\0') {
			_line();
		}
		
		Collections.sort(oneCycleList);
		
		
		PreLog pLog = new PreLog(new String[taskName.size()], new LogRecord[workflow.size()][], new HashMap<String, ArrayList<Integer>>());
		taskName.toArray(pLog.taskName);
		
		
		ArrayList<Integer> startList = new ArrayList<Integer>();
		ArrayList<Integer> endList = new ArrayList<Integer>();
		
		
		for(int i=0;i<isEnd.size();i++) {
			if(isEnd.get(i)) {
				endList.add(i);
			}
		}
		for(int i=0;i<isStart.size();i++) {
			if(isStart.get(i)) {
				startList.add(i);
			}
		}

		dealOneCycle();
		
		for(int k=0;k<oneCycleList.size();k++) {
			int _o = oneCycleList.get(k);
			WFPair _p = oneCyclePair.get(_o);
			String _key = _p.leftHash+"|"+_p.rightHash;
			if(!pLog.oneCycleHash.containsKey(_key)) {
				pLog.oneCycleHash.put(_key, new ArrayList<Integer>());
			}
			pLog.oneCycleHash.get(_key).add(_o);
		}
		
		for(int i=0;i<workflow.size();i++) {
			ArrayList<LogRecord> wf = workflow.get(i);
			LogRecord[] lr = new LogRecord[wf.size()];
			wf.toArray(lr);
			wf.clear();
			wf = null;
			//排序，方便查找
			Arrays.sort(lr);
			pLog.workflow[i] = lr;
		}
		
		
		pLog.start = new int[startList.size()];
		pLog.end = new int[endList.size()];
		for(int i=0;i<startList.size();i++) {
			pLog.start[i] = startList.get(i);
		}
		for(int i=0;i<endList.size();i++) {
			pLog.end[i] = endList.get(i);
		}
		return pLog;
	}
	
	private static void dealOneCycle() {
		int _s = workflow.size(), _cs = oneCycleList.size();
		for(int i=0;i<_s;i++) {
			ArrayList<LogRecord> lr = workflow.get(i);
			
			for(int k=0;k<_cs;k++) {
				int _o = oneCycleList.get(k);
				WFPair _p = oneCyclePair.get(_o);
				for(int j=0;j<lr.size();j++) {
					LogRecord _l = lr.get(j);
					if(_l.taskId==_o) {
						if( _l.preTask.length!=1){
							System.err.println("something strange occurs at dealOneCycle.");
						}
						if(Collections.binarySearch(oneCycleList, _l.preTask[0])<0) {
							System.out.println(Collections.binarySearch(oneCycleList, _l.preTask[0]));
							_p.addLeft(_l.preTask[0]);
						}
						
						lr.remove(j);
						j--;
					} else if(Arrays.binarySearch(_l.preTask, _o)>=0) {
						if(Collections.binarySearch(oneCycleList, _l.taskId)<0) {
//							System.out.println(Collections.binarySearch(oneCycleList, _l.taskId));
						
							_p.addRight(_l.taskId);
						}
						
						if(_l.preTask.length==1) {
							lr.remove(j);
						} else {
							int[] old_p = _l.preTask, new_p = new int[_l.preTask.length-1];
							int new_idx = 0;
							for(int __p=0;__p<old_p.length;__p++) {
								if(old_p[__p]!=_o) {
									new_p[new_idx] = old_p[__p];
									new_idx++;
								}
							}
							_l.preTask = new_p;
							
						}
						j--;
					}	
				}
				
			}
			
		}
		
		
		
		
	}
	private static int putTask(String task) {
		if(taskMap.containsKey(task)) {
			return taskMap.get(task);
		} else {
			TASK_ID++;
//			taskTable.add(new ArrayList<Integer>());
		    
			taskName.add(task);
			taskMap.put(task, TASK_ID);
			//
			//
			isEnd.add(true);
			isStart.add(true);
			
//			pr("put "+task+":"+TASK_ID);
			
			//把单循环pair容量增加。
			oneCyclePair.add(null);
			
			return TASK_ID;
		}
	}
//	private static void pr(String s) {
//		System.out.println(s);
//	}
	private PreLogParser() {
		//no instance
	}
	
}
