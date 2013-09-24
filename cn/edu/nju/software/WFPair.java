package cn.edu.nju.software;

import java.util.ArrayList;

public class WFPair {
	public ArrayList<Integer> left = new ArrayList<Integer>();
	public ArrayList<Integer> right = new ArrayList<Integer>();
	public String leftHash = "";
	public String rightHash = "";
	public WFPair parent = null;
	
	public WFPair(ArrayList<Integer> left, ArrayList<Integer> right){
		this.left.addAll(left);
		this.right.addAll(right);
		leftHash = calcHash(left);
		rightHash = calcHash(right);
	}
	private String calcHash(ArrayList<Integer> list){
		StringBuilder sb = new StringBuilder();
		sb.append(',');
		for(int i=0;i<list.size();i++){
			sb.append(list.get(i)).append(',');
		}
		return sb.toString();
	}
	public WFPair() {
		
	}
	public WFPair(int n1, int n2){
		left.add(n1);
		right.add(n2);
		leftHash = calcHash(left);
		rightHash = calcHash(right);
	}
	public WFPair leftMerge(WFPair p){
		WFPair r = new WFPair(left, WFSet.union(right, p.right));
		return r;	
	}

	public WFPair rightMerge(WFPair p){
		WFPair r = new WFPair(WFSet.union(left, p.left), right);
		return r;	
	}
	private void add(int n, ArrayList<Integer> list) {
		for(int i=0;i<list.size();i++) {
			int v = list.get(i);
			if(v==n) {
				return;
			} else if(v<n) {
				continue;
			} else {
				list.add(i, n);
				return;
			}
		}
		//如果n比所有元素大，插在末尾
		list.add(n);
	}
	public void addLeft(int n) {
		add(n, left);
		leftHash = calcHash(left);
	}
	public void addRight(int n) {
		add(n, right);
		rightHash = calcHash(right);
	}
	public void addLeft(ArrayList<Integer> nl) {
		for(int i=0;i<nl.size();i++) {
			add(nl.get(i), left);
		}
		leftHash = calcHash(left);
	}
	public void addRight(ArrayList<Integer> nl) {
		for(int i=0;i<nl.size();i++) {
			add(nl.get(i), right);
		}
		rightHash = calcHash(right);
	}
	public ArrayList<WFPair> merge(WFPair p2){
		ArrayList<WFPair> rtn = new ArrayList<WFPair>();
		if(leftHash == p2.leftHash){
			rtn.add(leftMerge(p2));
		} else if(rightHash == p2.rightHash){
			rtn.add(rightMerge(p2));
		}
		return rtn;
	}

    @Override public String toString(){
		return '('+leftHash + '|' + rightHash+')';
	}
}