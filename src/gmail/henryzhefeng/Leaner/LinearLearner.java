package gmail.henryzhefeng.Leaner;

import gmail.henryzhefeng.Models.FileSinkInfo;

import java.util.List;

/**
 * Created by ем on 3/30/2015.
 */
public class LinearLearner extends Learner {


    private static double HIGH_THRESHOLD_PROPORTION = 0.8;
    private static double LOW_THRESHOLD_PROPORTION = 0.2;
    private static double EXPECTED_HIGH_PROPORTION = 0.8;
    private static double EXPECTED_LOW_PROPORTION = 0.2;
    private static double PARAMS_INIT_VALUE = 0.5;
    private static int MAX_ADJUST_TIMES = 20;

    // contains the weight for each parameter
    private double[] mParams;
    // indicate the index of the parameter being adjusted
    private int mAdjustIndex = 0;
    // the count of sources for each sink in result files.
    private List<FileSinkInfo> mInfos;
    // indicating the deviation.
    private double mDeviation = Double.MAX_VALUE;
    // the current adjust times for one param
    private int mAdjustTimes = 0;

    /**
     * Constructor
     */
    public LinearLearner(List<FileSinkInfo> infos) {
        mInfos = infos;
        mParams = new double[infos.get(0).sinks.length];
        for (int i = 0; i < mParams.length; ++i) {
            mParams[i] = PARAMS_INIT_VALUE;
        }
    }

    @Override
    public void startLearning() {
        for (int i = 0; i < mParams.length; i++) { // each loop adjust one parameter
            mAdjustIndex = i;
            // try increaseAdjuster
            mAdjustTimes = 0;
            Adjuster incAdjuster = new IncreaseAdjuster(mParams);
            mAdjustTimes++;
            while (mAdjustTimes <= MAX_ADJUST_TIMES) {
                incAdjuster.startAdjust();
                incAdjuster = incAdjuster.getNextAdjuster();
                mAdjustTimes++;
            }
            // try decreaseAdjuster
            mAdjustTimes = 0;
            Adjuster decAdjuster = new DecreaseAdjuster(mParams);
            mAdjustTimes++;
            while (mAdjustTimes <= MAX_ADJUST_TIMES) {
                decAdjuster.startAdjust();
                decAdjuster = decAdjuster.getNextAdjuster();
                mAdjustTimes++;
            }
            // compare
            Adjuster betterAdjuster = decAdjuster;
            if (incAdjuster.getDeviation() < decAdjuster.getDeviation()) {
                betterAdjuster = incAdjuster;
            }
            mParams[mAdjustIndex] = betterAdjuster.getAdjustedParams()[mAdjustIndex];
        }
    }


    /**
     * The Adjuster won't change the any value in learner.
     */
    private abstract class Adjuster {

        // the max retry times
        protected final static int MAX_RETRIES = 20;
        // the initial step without retries
        protected final static double MAX_STEP = 0.1;
        // the adjusted params
        protected double[] mOriginParams;
        protected double[] mAdjustedParams;
        protected Adjuster mNextAdjuster;
        protected double mOriginDeviation;
        protected double mAdjustedDeviation;
        // current reties
        protected int mRetries = 0;

        public Adjuster(double[] params) {
            mOriginParams = params;
            mAdjustedParams = new double[params.length];
            // copy the parameters
            for (int i = 0; i < params.length; i++) {
                mAdjustedParams[i] = params[i];
            }
            mOriginDeviation = calculateDeviation();
        }

        public void startAdjust() {
            while (mRetries < MAX_RETRIES) {
                adjustParams();
                double deviation = calculateDeviation();
                // adjust successfully
                if (deviation < mOriginDeviation) { // succeed
                    mAdjustedDeviation = deviation;
                    mNextAdjuster = getSuccAdjuster();
                    return;
                }
                restoreParams();
                mRetries++;
            }
            // adjust failed
            mAdjustedDeviation = mOriginDeviation;
            mAdjustedParams = mOriginParams;
            mNextAdjuster = getFailAdjuster();
        }

        public Adjuster getNextAdjuster() {
            return mNextAdjuster;
        }

        public double getDeviation() {
            return mAdjustedDeviation;
        }

        public double[] getAdjustedParams() {
            return mAdjustedParams;
        }

        protected void restoreParams() {
            mAdjustedParams[LinearLearner.this.mAdjustIndex] =
                    LinearLearner.this.mParams[LinearLearner.this.mAdjustIndex];
        }

        // returns the next adjuster when adjust successfully this time
        protected abstract Adjuster getSuccAdjuster();

        // returns the next adjuster when adjusting failed this time
        protected abstract Adjuster getFailAdjuster();

        protected abstract void adjustParams();

        private double[] calculate() {
            double[] scores = new double[mInfos.size()];
            for (int i = 0; i < mInfos.size(); i++) {
                int[] sinks = mInfos.get(i).sinks;
                double score = 0;
                // we use linear algorithm
                for (int j = 0; j < sinks.length; j++) {
                    score += mAdjustedParams[j] * sinks[j];
                }
                scores[i] = score;
            }
            return scores;
        }

        /**
         * Calculate the deviation of the adjusted params
         *
         * @return
         */
        protected double calculateDeviation() {
            double[] scores = calculate();
            double highDeviation = Math.pow(countHighPart(scores) / (double) scores.length - EXPECTED_HIGH_PROPORTION, 2);
            double lowDeviation = Math.pow(countLowPart(scores) / (double) scores.length - EXPECTED_LOW_PROPORTION, 2);
            int middle = scores.length - countHighPart(scores) - countLowPart(scores);
            double middleDeviation = Math.pow((double) middle / (double) scores.length - EXPECTED_HIGH_PROPORTION, 2);
            return highDeviation + lowDeviation + middleDeviation;
        }

        /**
         * @return the numbers of value in high part
         */
        private int countHighPart(double[] scores) {
            double max = getMaxScore(scores);
            double min = getMinScore(scores);
            double threshold = (max - min) * HIGH_THRESHOLD_PROPORTION + min;
            int count = 0;
            for (double val : scores) {
                if (val >= threshold) {
                    count++;
                }
            }
            return count;
        }

        /**
         * @return the numbers of value in low part
         */
        private int countLowPart(double[] scores) {
            double max = getMaxScore(scores);
            double min = getMinScore(scores);
            double threshold = (max - min) * LOW_THRESHOLD_PROPORTION + min;
            int count = 0;
            for (double val : scores) {
                if (val <= threshold) {
                    count++;
                }
            }
            return count;
        }


        private double getMaxScore(double[] scores) {
            double max = -1;
            for (double val : scores) {
                if (val > max) {
                    max = val;
                }
            }
            return max;
        }

        private double getMinScore(double[] scores) {
            double min = Double.MAX_VALUE;
            for (double val : scores) {
                if (val < min) {
                    min = val;
                }
            }
            return min;
        }
    }

    private class IncreaseAdjuster extends Adjuster {

        public IncreaseAdjuster(double[] params) {
            super(params);
        }

        @Override
        protected Adjuster getSuccAdjuster() {
            return new IncreaseAdjuster(mAdjustedParams);
        }

        @Override
        protected Adjuster getFailAdjuster() {
            return new DecreaseAdjuster(mOriginParams);
        }

        @Override
        protected void adjustParams() {
            int index = LinearLearner.this.mAdjustIndex;
            mAdjustedParams[index] =
                    LinearLearner.this.mParams[index] + MAX_STEP / (mRetries + 1);
        }
    }

    private class DecreaseAdjuster extends Adjuster {

        public DecreaseAdjuster(double[] params) {
            super(params);
        }

        @Override
        protected Adjuster getSuccAdjuster() {
            return new DecreaseAdjuster(mAdjustedParams);
        }

        @Override
        protected Adjuster getFailAdjuster() {
            return new IncreaseAdjuster(mOriginParams);
        }

        @Override
        protected void adjustParams() {
            int index = LinearLearner.this.mAdjustIndex;
            mAdjustedParams[index] =
                    LinearLearner.this.mParams[index] - MAX_STEP / (mRetries + 1);
        }
    }
}
