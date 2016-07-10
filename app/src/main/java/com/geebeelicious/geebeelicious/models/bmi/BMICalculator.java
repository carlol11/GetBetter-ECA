package com.geebeelicious.geebeelicious.models.bmi;

/**
 * Created by mgmalana.
 * BMICalculator class is used by the monitoringFragment
 * for calculating BMI.
 * BMI percentile data retrieved from http://www.cdc.gov/growthcharts/percentile_data_files.htm
 */
public class BMICalculator {
    /*
        bmi[x][y]
            x: 0 = P5, 1 = P85, 2 = P95 //P5 = 5th percentile
            y: age - 2
    */
    private final static float[][] boyBMI = {
            {14.71929257f, 14.32851119f, 14.03295533f, 13.83854804f,
                    13.73623987f, 13.72112886f, 13.79574779f, 13.9621189f,
                    14.21865508f, 14.56000917f, 14.97744768f, 15.4591825f,
                    15.99065148f, 16.55481067f, 17.13249925f, 17.70284417f,
                    18.24348662f, 18.73019025f, 19.13539969f},
            {18.11954923f, 17.32627356f, 16.92501028f, 16.84075838f,
                    17.01417786f, 17.40121891f, 17.95575444f, 18.63222223f,
                    19.3904113f, 20.19667437f, 21.02385969f, 21.85104367f,
                    22.66325092f, 23.45117065f, 24.21087038f, 24.94361805f,
                    25.65601244f, 26.36053932f, 27.07644874f},
            {19.27889813f, 18.23841666f, 17.8361423f, 17.93892524f,
                    18.41421242f, 19.15236073f, 20.06792538f, 21.08892746f,
                    22.15409238f, 23.21358351f, 24.22984982f, 25.17811163f,
                    26.04662217f, 26.83688026f, 27.56392793f, 28.25675709f,
                    28.9586172f, 29.72674204f, 30.63106054f}
    };

    private final static float[][] girlBMI = {
            {14.3801866f, 14.00209476f, 13.71472021f, 13.52091027f,
                    13.42587042f, 13.43276016f, 13.54050176f, 13.74412656f,
                    14.03535405f, 14.40290497f, 14.83262428f, 15.30748565f,
                    15.80752854f, 16.30973602f, 16.78787442f, 17.21234472f,
                    17.55015468f, 17.76515018f, 17.81856356f},
            {17.97371413f, 17.16633643f, 16.80057671f, 16.80197195f,
                    17.09974172f, 17.6255667f, 18.31717594f, 19.11937316f,
                    19.98399779f, 20.86984014f, 21.74262677f, 22.57506113f,
                    23.34689045f, 24.04502533f, 24.66372475f, 25.20482218f,
                    25.67786376f, 26.09993465f, 26.49501679f},
            {19.05823845f, 18.25475381f,18.02851473f, 18.25738105f,
                    18.83777954f, 19.67793583f, 20.69525186f, 21.8172543f,
                    22.98257733f, 24.14141407f,25.25564304f, 26.298799f,
                    27.25597392f, 28.12369169f, 28.90980533f, 29.63350259f,
                    30.32554216f, 31.02880054f, 31.79902964f}
    };

    public static float computeBMIMetric(int height, int weight){
        if (height == 0){
            return 0;
        }
        else{
            return ((float)weight / height / height) * 10000;
        }
    }

    public static int getBMIResult(boolean isGirl, int age, float patientBMI){
        int ageIndex = age - 2;


        if(ageIndex < 0 || ageIndex > boyBMI[0].length - 1) {
            return 4;
        } else if (isGirl){
            return getBMIResult(ageIndex, patientBMI, girlBMI);
        } else {
            return getBMIResult(ageIndex, patientBMI, boyBMI);
        }
    }

    public static String getBMIResultString(boolean isGirl, int age, float patientBMI){
        int result = getBMIResult(isGirl, age, patientBMI);

        switch (result){
            case 0:
                return "Underweight";
            case 1:
                return "Normal";
            case 2:
                return "Overweight";
            case 3:
                return "Obese";
        }

        return "N/A";
    }

    private static int getBMIResult(int ageIndex, float patientBMI, float bmiChart[][]){
        if (patientBMI < bmiChart[0][ageIndex]){ //less than 5P
            return 0;
        } else if (patientBMI >= bmiChart[2][ageIndex]){ //greater or equal to 95P
            return 3;
        } else if (patientBMI >= bmiChart[1][ageIndex]){ //85P to less than 95P
            return 2;
        } else {
            return 1;
        }
    }
}
