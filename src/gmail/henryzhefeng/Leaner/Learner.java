package gmail.henryzhefeng.Leaner;

/**
 * Created by 哲 on 3/30/2015.
 */
public abstract class Learner {

    /**
     * 开始学习
     */
    public abstract void startLearning();

    /**
     * 计算值
     */
    protected abstract void calculate();

    /**
     * 获取反馈情况
     *
     * @return true表示本次计算结果比上次好
     */
    protected abstract boolean feedBack();

    /**
     * 调整下一次计算的参数
     */
    protected abstract void ajustParams();
}
