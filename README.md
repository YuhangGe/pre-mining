pre-mining
==========
process mining [ProM](http://www.promtools.org/prom6/) plugin witch use a new workflow mining algorithm

how to use
---------------
1. download source code of ProM with svn :   `svn co https://svn.win.tue.nl/repos/prom/Framework/trunk dir_of_prom`
2. download pre-mining plugin with git :   `git clone https://github.com/YuhangGe/pre-mining.git`
3. copy all directories and files under `pre-mining/src/` to `dir_of_prom/src_Plugins/`
4. open eclipse, import existing project from `dir_of_prom/`
5. configure build path, add external jar, choose `pre-mining/PetriNets.jar`
6. see [How To Run ProM](https://svn.win.tue.nl/trac/prom/wiki/setup/RunningProM)，and run ProM, the first time you must wait for it downloading other packages it depends on.
7. import a log file from `pre-mining/example-logs/`
8. choose the plugin `Pre-Log Miner Plugin` from the plugins list, and run.
9. enjoy it! 

About
------------
@author: `Yuhang Ge (Software Insititute, Nanjing University)`  
@email:   `abraham1@163.com`  
@twitter: [@xiaoge_me](https://twitter.com/xiaoge_me)
@website: [http://xiaoge.me](http://xiaoge.me)

pre-mining
==========
使用前驱日志挖掘算法进行过程挖掘的ProM插件

如何使用
---------------
1. 从svn上下载ProM的项目源代码：  `svn co https://svn.win.tue.nl/repos/prom/Framework/trunk dir_of_prom`
2. 使用git下载本插件源代码：  `git clone https://github.com/YuhangGe/pre-mining.git`
3. 将`pre-mining/src/`目录下的所有文件（夹）复制到`dir_of_prom`目录下。
4. 打开eclipse，从`dir_of_prom`文件夹引入已经存在的项目（import existing projects）
5. 右键单击项目，build path->configure build path->add externals jar，选择`pre-mining/PetriNets.jar`文件
6. 查看ProM官方说明：[How To Run ProM](https://svn.win.tue.nl/trac/prom/wiki/setup/RunningProM)，运行ProM，第一次运行时请耐心等待它下载好全部依赖的插件。
7. ProM运行后，从文件夹`pre-mining/example-logs/`导入(import)一个日志文件导入。
8. 选择插件`Pre-Log Miner Plugin`，执行。
9. ProM将会以图的形式显示挖掘的结果。

关于
------------
作者：`白羊座小葛（南京大学软件学院）`  
邮箱：abraham1@163.com  
主页：[http://xiaoge.me](http://xiaoge.me)  
微博：[@白羊座小葛](http://weibo.com/abeyuhang)

