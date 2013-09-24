package cn.edu.nju.software;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;

public class PreLogTest {
	public static void main(String[] args) throws IOException {
		FileInputStream is = new FileInputStream("/Users/abraham/workspace/eclipse/log-alpha-plus-plus-n4.pLog");
		
		PreLog pLog = PreLogParser.parseFromStream(is);
		is.close();
		
		String[] tn = pLog.taskName;

		LogRecord[][] wf = pLog.workflow;
		LogRecord wi = null;
		for(int i=0;i<wf.length;i++) {
			System.out.print("\nwf: ");
			for(int j=0;j<wf[i].length;j++) {
				wi = wf[i][j];
				System.out.print('[');
				for(int k=0;k<wi.preTask.length;k++) {
					System.out.print(tn[wi.preTask[k]]+" ");
				}
				System.out.print(']'+tn[wi.taskId]+',');
			}
		}
		
		System.out.print("\nstart: ");
		for(int i=0;i<pLog.start.length;i++) {
			System.out.print(" "+ tn[pLog.start[i]]);
		}
		System.out.print("\nend: ");
		for(int i=0;i<pLog.end.length;i++) {
			System.out.print(" "+ tn[pLog.end[i]]);
		}
		
		System.out.print("\none cycle: ");
		for(String k : pLog.oneCycleHash.keySet()) {
			ArrayList<Integer> al = pLog.oneCycleHash.get(k);
			for(int i=0;i<al.size();i++) {
				System.out.println("     "+k+" -> " + tn[al.get(i)]);
			}
			
		}
		
		Petrinet pNet = PetrinetFactory.newPetrinet("Output Petrinet");
		PreLogMiner.mine(pLog, pNet);
		
	}
}
