package gmail.henryzhefeng.Leaner;

import gmail.henryzhefeng.Models.FileSinkInfo;

import java.util.List;

/**
 * Created by ем on 3/30/2015.
 */
public class LinearLearner extends Learner {

    // contains the weight for each parameter
    private int[] mParams;
    // the count of sources for each sink in result files.
    private List<FileSinkInfo> mInfos;

    /**
     * init function
     */
    public LinearLearner(List<FileSinkInfo> infos) {
        mInfos = infos;
        mParams = new int[infos.get(0).sinks.length];
    }

    @Override
    public void startLearning() {
        // TODO
    }

    @Override
    protected void calculate() {
        // todo
    }

    // indicating the previous deviation from expect.
    private float mPrevDeviation = Float.MAX_VALUE;

    @Override
    protected boolean feedBack() {
        return false;
    }

    @Override
    protected void ajustParams() {
        // todo
    }
}
