package cn.edu.nju.software;

import java.io.InputStream;

import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;

//插件定义
	@Plugin(name = "Pre-Log Import Plugin", 
	parameterLabels = { "File Name" }, 
	returnLabels = { "Pre-Log imported" }, 
	returnTypes = { PreLog.class })
//导入插件定义
	@UIImportPlugin(
			//表明传入的文件会被转化为Post类型在ProM中进行传输
			description = "Pre Log",
			//表明识别的自定义文件后缀名为plog
			extensions = { "plog" })
public class PreLogImportPlugin extends AbstractImportPlugin {
	
	
	@UITopiaVariant(affiliation = "cn.edu.nju.software", author = "xiaoge.me", email = "abraham1@163.com")
	@Override
	protected PreLog importFromStream(PluginContext context, InputStream input, String filename,
			long fileSizeInBytes) throws Exception {
		
		context.getFutureResult(0).setLabel("Pre-Log imported from " + filename);
		return PreLogParser.parseFromStream(input);
		
	}

}
