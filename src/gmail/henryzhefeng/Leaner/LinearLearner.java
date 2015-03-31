package gmail.henryzhefeng.Leaner;

import gmail.henryzhefeng.Models.FileSinkInfo;

import java.util.List;

/**
 * Created by ем on 3/30/2015.
 */
public class LinearLearner extends Learner {


    private static double HIGH_THRESHOLD = 0.8;
    private static double LOW_THRESHOLD = 0.2;
    private static double HIGH_PROPORTION = 0.8;
    private static double LOW_PROPORTION = 0.2;

    // contains the weight for each parameter
    private double[] mParams;
    // the count of sources for each sink in result files.
    private List<FileSinkInfo> mInfos;

    /**
     * init function
     */
    public LinearLearner(List<FileSinkInfo> infos) {
        mInfos = infos;
        mParams = new double[infos.get(0).sinks.length];
    }

    @Override
    public void startLearning() {
        // TODO
    }

    // the current scores for each result file
    private double[] mScores;

    @Override
    protected void calculate() {
        int len = mInfos.size();
        mScores = new double[len];
        for (int i = 0; i < len; i++) {
            int[] sinks = mInfos.get(i).sinks;
            double score = 0;
            // we use linear algorithm
            for (int j = 0; j < sinks.length; j++) {
                score += mParams[j] * sinks[j];
            }
            mScores[i] = score;
        }
    }

    // indicating the previous deviation from expect.
    private double mPrevDeviation = Double.MAX_VALUE;

    @Override
    protected boolean feedBack() {
        double highDeviation = Math.pow(countHighPart() / (double) mScores.length - HIGH_PROPORTION, 2);
        double lowDeviation = Math.pow(countLowPart() / (double) mScores.length - LOW_PROPORTION, 2);
        int middle = mScores.length - countHighPart() - countLowPart();
        double middleDeviation = Math.pow((double) middle / (double) mScores.length - HIGH_PROPORTION, 2);
        double currDeviation = highDeviation + lowDeviation + middleDeviation;
        // if the deviation is less, we believe the param is better.
        boolean rst = currDeviation <= mPrevDeviation;
        mPrevDeviation = currDeviation;
        return rst;
    }

    /**
     * @return the numbers of value in high part
     */
    private int countHighPart() {
        double max = getMaxScore();
        double min = getMinScore();
        double threshold = (max - min) * HIGH_THRESHOLD + min;
        int count = 0;
        for (double val : mScores) {
            if (val >= threshold) {
                count++;
            }
        }
        return count;
    }

    /**
     * @return the numbers of value in low part
     */
    private int countLowPart() {
        double max = getMaxScore();
        double min = getMinScore();
        double threshold = (max - min) * LOW_THRESHOLD + min;
        int count = 0;
        for (double val : mScores) {
            if (val <= threshold) {
                count++;
            }
        }
        return count;
    }


    private double getMaxScore() {
        double max = -1;
        for (double val : mScores) {
            if (val > max) {
                max = val;
            }
        }
        return max;
    }

    private double getMinScore() {
        double min = Double.MAX_VALUE;
        for (double val : mScores) {
            if (val < min) {
                min = val;
            }
        }
        return min;
    }

    @Override
    protected void ajustParams() {
        // todo
    }
}
