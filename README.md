
## stt-local

#### 云派助手是基于OpenCV框架，运行在`Android`环境的一款工具软件，用于识别可编辑的图片，执行Android本身的触摸事件。最终实现图片识别自动执行脚本。技术上整合了包括

* 屏幕捕捉
* 截屏
* 图片裁剪
* 编辑图片
* 可测试运行的快速识图工具。


识图准确率高达95%以上；
主要运行环境为可root的Android系统。
其中包含了大部分的商用模拟器和少数自定义设备；
操作便捷性便捷性满足开发者和普通用户的应用需求，欢迎大家使用交流！


## 安装和调试
#### 当前可以：

* 下载或者clone源代码，导入到Android-Studio/IDE 中，直接编译运行Demo工程
* 直接下载[最近发布的Release](https://github.com/padyun-cn/stt-local/releases)中的Apk

> ***请确保当前的设备为可Root环境。***

## Demo演示

#### 1. 默认列表加载当前设备的用户安装App列表，启动后流程如下：
![视频录制 120213121745552](https://user-images.githubusercontent.com/80308909/110923004-6124db00-835b-11eb-9682-7bfcd69b270e.gif)

#### 2. 打开浮窗，开始新建一个任务：
![视频录制 12021312175763](https://user-images.githubusercontent.com/80308909/110924029-8534ec00-835c-11eb-98b9-63be5226b3dc.gif)

#### 3. 编辑新增一个“条件”，如下演示：
```
条件是根据我们之后截屏后裁剪图进行识别匹配，如果满足识别条件，之后会进行相关的事件操作。默认实现中,包含3中基本触摸事件：
1. 点击
2. 偏移点击（图片识别后位置的相对位置）
3. 滑动，起始点到终点设置
```
![视频录制 12021312181104](https://user-images.githubusercontent.com/80308909/110925649-6d5e6780-835e-11eb-971c-1ec6d25bf746.gif)

>返回后会新增一个条件条目，可以进行一些预设置和调整：
![视频录制 120213121813215](https://user-images.githubusercontent.com/80308909/110926012-d5ad4900-835e-11eb-8c1f-e5aeee9ac276.gif)
>包括使用opencv对图片的一些调整处理设置包括二值化，hls等
![视频录制 120213121816136](https://user-images.githubusercontent.com/80308909/110926289-291f9700-835f-11eb-983c-5fed770593d4.gif)

#### 4. 最后看一下运行效果：
![视频录制 120213121820207](https://user-images.githubusercontent.com/80308909/110926734-b9f67280-835f-11eb-8d74-56b6c3c5873c.gif)



