# InterviewCustomView
想省事儿，用别人的代码就是找坑跳。

1. TextView外层嵌套ScrollView，多一层View，多一层View，界面要卡喽。

2. 将控件做成一个接口，只有想使用此功能的View，只需实现这个接口就好。

       public interface CustomTextView {
    
           String getSelectedText();

           void onTextSelected();
           void onTextUnselected();

        }    
    
3. ActionMode--版本不同，形式不同。对于低版本，menu.clear()并无用处。


![image](https://github.com/qiuuu/InterviewCustomView/blob/master/app/c.gif)


