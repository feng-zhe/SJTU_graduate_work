package gmail.henryzhefeng.Leaner;

/**
 * Created by �� on 3/30/2015.
 */
public abstract class Learner {

    /**
     * ��ʼѧϰ
     */
    public abstract void startLearning();

    /**
     * ����ֵ
     */
    protected abstract void calculate();

    /**
     * ��ȡ�������
     *
     * @return true��ʾ���μ��������ϴκ�
     */
    protected abstract boolean feedBack();

    /**
     * ������һ�μ���Ĳ���
     */
    protected abstract void ajustParams();
}
