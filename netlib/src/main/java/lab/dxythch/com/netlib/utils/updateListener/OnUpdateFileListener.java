package lab.dxythch.com.netlib.utils.updateListener;

/**
 * 项目名称：MyProject
 * 类描述：
 * 创建人：oden
 * 创建时间：2018/4/19
 */
public interface OnUpdateFileListener {

    void start();

    void end();

    void progress(int progress, long current, long total);

    void error(Throwable e);

}
