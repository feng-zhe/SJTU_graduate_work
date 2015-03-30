package gmail.henryzhefeng.Leaner;

/**
 * Created by ем on 3/30/2015.
 */
public abstract class Learner {

    /**
     * start to learn
     */
    public abstract void startLearning();

    /**
     * calculate values
     */
    protected abstract void calculate();

    /**
     * get the feedback
     *
     * @return true means params of this time is better
     */
    protected abstract boolean feedBack();

    /**
     * ajust the params for next calculation.
     */
    protected abstract void ajustParams();
}
