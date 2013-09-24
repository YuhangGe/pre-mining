package cn.edu.nju.software;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

public class PreLogMiner {
	
//	private static int[][] tt = null;
	private static LogRecord[][] wf = null;
	
	private static boolean addIfNotIn(ArrayList<WFPair> pl, WFPair p){
		int _s = pl.size();
		for(int z=0;z<_s;z++){
			WFPair sp = pl.get(z);
			if(sp.leftHash.equals(p.leftHash) && sp.rightHash.equals(p.rightHash)){
				return false;
			}
		}
		pl.add(p);
		return true;
	}
	
	public static void mine(PreLog preLog, Petrinet petriNet) {
//		tt = preLog.taskTable;
		wf = preLog.workflow;
		
		ArrayList<WFPair> pl = new ArrayList<WFPair>();
		
		generatePairs(pl);
		
		pr_pair(preLog.taskName, pl);
		
		ArrayList<WFPair> v_pl = new ArrayList<WFPair>();
		
		getValidatePairs(pl, v_pl);
		
		System.out.print("\n\n-----vp-----\n\n");
		pr_pair(preLog.taskName, v_pl);
		
		ArrayList<WFPair> b_pl = new ArrayList<WFPair>();
		getBigestPairs(v_pl, b_pl);
		
		System.out.print("\n\n----------\n\n");
		pr_pair(preLog.taskName, b_pl);
		
		insertOneCycle(b_pl, preLog.oneCycleHash);
		
		System.out.print("\n\n----final------\n\n");
		pr_pair(preLog.taskName, b_pl);
		
		constructPetrinet(b_pl, preLog, petriNet);
		
		pl.clear();
		v_pl.clear();
		b_pl.clear();
		
//		tt = null;
		wf = null;
		
	}
	
	private static void insertOneCycle(ArrayList<WFPair> pl, HashMap<String, ArrayList<Integer>> oc) {
		for(int i=0;i<pl.size();i++) {
			WFPair _p = pl.get(i);
			String _key = _p.leftHash+"|"+_p.rightHash;
			if(oc.containsKey(_key)) {
				ArrayList<Integer> _v = oc.get(_key);
				_p.addLeft(_v);
				_p.addRight(_v);
			}
		}
	}
	private static void constructPetrinet(ArrayList<WFPair> pl, PreLog pLog, Petrinet pNet) {
		int _size = pl.size();
		Place p_s = pNet.addPlace("start"), p_e = pNet.addPlace("end");
		
		Transition[] ts = new Transition[pLog.taskName.length];
		for(int i=0;i<ts.length;i++) {
			ts[i] = pNet.addTransition(pLog.taskName[i]);
		}
		for(int i=0;i<pLog.start.length;i++) {
			pNet.addArc(p_s, ts[pLog.start[i]]);
		}
		for(int i=0;i<pLog.end.length;i++) {
			pNet.addArc(ts[pLog.end[i]], p_e);
		}
		for(int i=0;i<_size;i++) {
			WFPair pair = pl.get(i);
			Place p_t = pNet.addPlace("p"+i);
			for(int j=0;j<pair.left.size();j++) {
				pNet.addArc(ts[pair.left.get(j)], p_t);
			}
			for(int j=0;j<pair.right.size();j++) {
				pNet.addArc(p_t, ts[pair.right.get(j)]);
			}
		}
	}
	
	private static void generatePairs(ArrayList<WFPair> pl) {
		
//		for(int i=0;i<tt.length;i++) {
//			int[] pre = tt[i];
//			for(int j=0;j<pre.length;j++) {
//				pl.add(new WFPair(pre[j], i));
//			}
//		}
		
		ArrayList<String> pc = new ArrayList<String>();
		
		for(int i=0;i<wf.length;i++) {
			LogRecord[] lr = wf[i];
			for(int j=0;j<lr.length;j++) {
				LogRecord _l = lr[j];
				int[] pre = _l.preTask;
				
				for(int k=0;k<pre.length;k++) {
					String _key = pre[k]+","+_l.taskId;
					//要防止重复插入
					if(!pc.contains(_key)) {
						pl.add(new WFPair(pre[k], _l.taskId));
						pc.add(_key);
					}
					
				}
				
			}
		}
		pc.clear();
		pc = null;
		
		
		int debug = 0;
		boolean _new = true;
		int _size = 0;
		/**
		 * debug是为了防止死循环
		 */

		out : 
		while(_new && debug < 10000){
			_size = pl.size();
			//System.out.println("cur size:"+_size);
			_new = false;
			for(int i=0;i<_size;i++){
				for(int j=i+1;j<_size;j++){

					WFPair p1 = pl.get(i), p2 = pl.get(j);
					if(p1.leftHash.equals(p2.leftHash) && addIfNotIn(pl,p1.leftMerge(p2))==true){
						_new = true;
					} else if(p1.rightHash.equals(p2.rightHash) && addIfNotIn(pl, p1.rightMerge(p2))==true ){
						_new = true;
					}
					if(_new == true){
//						for(int t=0;t<pl.size();t++){
//							System.out.print(pl.get(t).toString());
//						}
//						System.out.println();
						debug++;
						continue out;
					}
				}
			}

			System.out.print("\nwhile finish...\n");
		}
		//System.out.println("debug:"+debug);
		if(debug>=10000){
			System.err.print("调试计数达到最大值，可能发生死循环...");
		}
		
	}
	
	private static void getValidatePairs(ArrayList<WFPair> pl, ArrayList<WFPair> v_pl) {
		int _size = pl.size();
		for(int i=0;i<_size;i++) {
			if(isPairValidate(pl.get(i))) {
				v_pl.add(pl.get(i));
			}
		}
	}
	
	private static boolean isPairValidate(WFPair pair) {
		if(isSetValidate(pair.left, false) && isSetValidate(pair.right, true)) {
			return true;
		}
		return false;
	}
	
	private static boolean isSetValidate(ArrayList<Integer> set, boolean strick) {
		int _s = set.size();
		
		for(int i=0;i<_s;i++) {
			for(int j=i+1;j<_s;j++) {
				int ta = set.get(i), tb = set.get(j);
				if(ta==2 && tb==6) {
					ta = 2;
				}
				if(_is_follow_(ta, tb)==true 
						|| _is_follow_(tb, ta)==true 
						|| _is_in_one_pre_(ta, tb)==true
						||_is_pars_(ta, tb)==true
						||(strick==true && _is_not_choice_strick_(ta, tb)==true)
						||(strick==false && _is_not_choice_(ta, tb)==true)) {
					return false;
				}
			}
		}
		return true;
	}
	
	private static boolean _is_follow_(int a, int b) {
		for(int i=0;i<wf.length;i++) {
			int wi = bSearch(wf[i], b);
			if(wi>=0 && Arrays.binarySearch(wf[i][wi].preTask, a)>=0) {
					return true;
			}
		}
		return false;
	}
	
	private static boolean _is_in_one_pre_(int a, int b) {
		
		for(int i=0;i<wf.length;i++) {
			LogRecord[] lr = wf[i];
			for(int j=0;j<lr.length;j++) {
				int[] pre = lr[j].preTask;
				if(Arrays.binarySearch(pre, a)>=0 && Arrays.binarySearch(pre, b)>=0) {
					return true;
				}
			}
			
		}
		return false;
	}
	private static boolean _is_pars_(int a, int b) {
		for(int i=0;i<wf.length;i++) {
			LogRecord[] line = wf[i];
			
			int ax = bSearch(line, a), bx = bSearch(line, b);
			if((ax>=0 && bx>=0) && ((line[ax].taskIndex-line[bx].taskIndex)==1 || (line[ax].taskIndex-line[bx].taskIndex)==-1) && _set_equal_(line[ax].preTask, line[bx].preTask)) {

				return true;

			}
		}
		return false;
	}
	private static boolean _check_choice_(int[] pre, int b) {
		for(int i=0;i<wf.length;i++) {
			int bx = bSearch(wf[i], b);
			if(bx>=0 && _set_joinable_(wf[i][bx].preTask, pre)==true) {
				return true;
			}
		}
		return false;
	}
	private static boolean _is_not_choice_strick_(int a ,int b) {
		
		for(int i=0;i<wf.length;i++) {
			int ax = bSearch(wf[i], a), bx = bSearch(wf[i], b);
			if((ax>=0 && bx<0) && _check_choice_(wf[i][ax].preTask, b)){
				return false;
			}
			if((bx>=0 && ax<0) && _check_choice_(wf[i][bx].preTask, a)){
				return false;
			}
			
		}
		
		return true;
		
	}
	private static boolean _is_not_choice_(int a, int b) {
		/**
		 * 对于是否选择关系的弱检测，只要它们不同时出现在一条log中，则认为可能是选择关系。
		 */
		for(int i=0;i<wf.length;i++) {
			int ax = bSearch(wf[i], a), bx = bSearch(wf[i], b);
			if((ax>=0 && bx<0)||(ax<0 && bx>=0)) {
				return false;
			}
		}
		
		return true;
	}

	private static boolean _set_joinable_(int[] s1, int[] s2) {
		if(s1[0]>s2[s2.length-1] || s1[s1.length-1]<s2[0]) {
			return false;
		}
		for(int i=0;i<s1.length;i++) {
			if(Arrays.binarySearch(s1, s1[i])>=0) {
				return true;
			}
		}
		return false;
	}
	private static void getBigestPairs(ArrayList<WFPair> pl, ArrayList<WFPair> b_pl) {
		int _size = pl.size();

		for(int i=0;i<_size;i++){
			for(int j=i+1;j<_size;j++){
				WFPair p1 = pl.get(i), p2 = pl.get(j);
				if(p1.leftHash.contains(p2.leftHash) && p1.rightHash.contains(p2.rightHash)){
					p2.parent = p1;
				} else if(p2.leftHash.contains(p1.leftHash) && p2.rightHash.contains(p1.rightHash)){
					p1.parent = p2;
				}
			}
		}
		
		for(int i=0;i<_size;i++) {
			if(pl.get(i).parent==null) {
				b_pl.add(pl.get(i));
			}
		}
		
	}
	
	private static void pr_pair(String[] names, ArrayList<WFPair> pl) {
		pr_pair(names, pl, false);
	}
	private static void pr_pair(String[] names, ArrayList<WFPair> pl, boolean no_parent) {
		for(int i=0;i<pl.size();i++) {
			WFPair p = pl.get(i);
			if(no_parent && p.parent!=null) {
				continue;
			}
			System.out.print("\n");
			
			for(int l=0;l<p.left.size();l++) {
				System.out.print(names[p.left.get(l)] + " ");
			}
			System.out.print("|");
			for(int r=0;r<p.right.size();r++) {
				System.out.print(names[p.right.get(r)] + " ");
			}
		
		}
	}

	
	
	private static boolean _set_equal_(int[] s1, int[] s2) {
		if(s1.length!=s2.length) {
			return false;
		}
		if(s1.length==1) {
			return s1[0] == s2[0];
		}
		for(int i=0;i<s1.length;i++) {
			if(s1[i]!=s2[i]) {
				return false;
			}
		}
		return true;
	}
	
	
	public static int bSearch(LogRecord[] lr, int a) {
		int low = 0;
        int high = lr.length - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;

			int cmp = lr[mid].taskId - a;
            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
	}
}

class Pos{
	public int row;
	public int col;
	public Pos() {
		row = 0;
		col = 0;
	}
	public Pos(int r, int c){
		row = r;
		col = c;
	}
}
