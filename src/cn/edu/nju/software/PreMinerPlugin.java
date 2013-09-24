package cn.edu.nju.software;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;

public class PreMinerPlugin {
		//标签定义
		@Plugin(name = "Pre-Log Miner Plugin", parameterLabels = { "Pre-Log imported" }, returnLabels = { "Result By PreMinier" }, returnTypes = { Petrinet.class }, userAccessible = true, help = "use PreLog Import Plugin to import a Pre-Log as the source")
		@UITopiaVariant(affiliation = "cn.edu.nju.software", author = "xiaoge.me", email = "abraham1@163.com")
		public Petrinet processMultiset(UIPluginContext context, PreLog pLog) {
			
			Petrinet pNet = PetrinetFactory.newPetrinet("Output Petrinet");

			
			PreLogMiner.mine(pLog, pNet);
			
			return pNet;
		}
}
