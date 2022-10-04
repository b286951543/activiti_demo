1. idea 的版本是：ideaIU-2020.3.4。https://www.jetbrains.com/idea/download/other.html

2. bpmn 插件使用的是 Activiti BPMN visualizer（按住shift+鼠标左键能移动幕布）。其他用法参考：https://blog.csdn.net/weixin_43025151/article/details/125967784

3. bpmn 也可以使用 Camunda Modeler 来画：https://camunda.com/download/modeler/#

4. 遇到分支时，是使用代码来判断还是在图片中使用分支条件来判断，我的理解是看前端，前端需要选择的（比如前端选择发给财务），那么使用代码来判断。前端不需要选择的（比如财务拒绝，拒绝后，后端需要根据拒绝次数选择是发回给客服，还是直接结束），但是又需要经过分支，那么使用图片中的分支条件来判断。可参考：ReimburseDemo