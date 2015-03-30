package gmail.henryzhefeng.Leaner;

import gmail.henryzhefeng.Models.FileSinkInfo;

import java.util.List;

/**
 * Created by 哲 on 3/30/2015.
 */
public class LinearLearner extends Learner {

    // 包含每个sink的对应权重值
    private int[] mParams;
    // sink对应的source数量
    private List<FileSinkInfo> mInfos;

    /**
     * 初始化函数
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

    @Override
    protected boolean feedBack() {
        return false;
    }

    @Override
    protected void ajustParams() {
        // todo
    }
}
