# eventbus3-intellij-plugin

引导 [EventBus](https://github.com/greenrobot/EventBus) 的 `post | postSticky` 和 `event`

### 从`post | postSticky` 到 `@Subscribe` ，从 `@Subscribe` 到 `post`

![](https://raw.githubusercontent.com/likfe/eventbus3-intellij-plugin/master/art/cap.gif)


## 安装
两种方法：
- Android Studio -> Preference -> Plugins -> Browse Repositories : `EventBus` -> 选择 `EventBus3-IDEA` -> 点击 `Install` 按钮.
- [下载](https://github.com/likfe/eventbus3-intellij-plugin/releases)最新版本的 zip 文件， 然后在插件界面选择"Install Plugin From Disk" 进行安装

----

## 更新日志

### 2020-10-15
- 使用 2020.1.4 构建
- 升级到version 2020.0.0(201~)
- 适配 Android Studio 4.1 (IntelliJ 201)

### 2020-09-22
- 修改插件名称`EventBus3 Intellij Plugin` 为 `EventBus3-IDEA`

### 2020-09-19
- 把项目从 maven 转换为 gradle
- 替换了 标记为 deprecated 的 API
    - `ScrollingUtil` 代替 `TableScrollingUtil`
    - `UsageNode(Node parent, Usage usage)` 代替 `UsageNode(Usage usage, UsageViewTreeModelBuilder model)`
- 适配 Android Studio 4.0.1 (IntelliJ 193)

### 2018-10-17

- 更新 icon 图标，在高清屏上具有更好的效果

### 2018-08-13

- 修复 bug
- 支持 `postSticky` 到 `@Subscribe`
- 尝试支持 kotlin

### 2018-08-10

代码变化在 `dev` 分支，尝试支持 kotlin 中

- 支持从 `postSticky` 到 `@Subscribe`

### 2016-09-07
- 增加了 `build version` 值
- 修改了 `plugin id`，否则不能在 https://plugins.jetbrains.com 插件官网发布新版本
- 添加 `plugin.xml` 的一个配置，使此插件支持在 Android Studio 中在线安装

### 2016-09-05
这是个比较实用的插件，然而对于最新版的 `EventBus 3.0.0` 无效，原作者久久没有回应 `issue`，我尝试让这个插件可以工作

主要Bug修复工作：
- 修改包名和方法名以适应 `EventBus 3.X`
- 替换一个在新版的 `intellij plugin SDK` 已经不存在的类
- 增加若干 `try-catch` ，防止插件崩溃

## Issues

- [Unsupported major.minor version 52.0](https://github.com/likfe/eventbus3-intellij-plugin/issues/1)

## License

```
Copyright 2016

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
