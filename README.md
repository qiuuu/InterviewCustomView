# InterviewCustomView
想省事儿，用别人的代码就是找坑跳。

1. TextView外层嵌套ScrollView，多一层View，多一层View，界面要卡喽。

将控件做成一个接口，只有想使用此功能的View，只需实现这个接口就好。

public interface CustomTextView {
    //Context getContext();
   // CharSequence getText();
    String getSelectedText();

    void onTextSelected();
    void onTextUnselected();

}
