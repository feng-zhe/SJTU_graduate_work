package gmail.henryzhefeng.Leaner;

import gmail.henryzhefeng.Models.FileSinkInfo;

import java.util.List;

/**
 * Created by �� on 3/30/2015.
 */
public class LinearLearner extends Learner {

    // ����ÿ��sink�Ķ�ӦȨ��ֵ
    private int[] mParams;
    // sink��Ӧ��source����
    private List<FileSinkInfo> mInfos;

    /**
     * ��ʼ������
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
