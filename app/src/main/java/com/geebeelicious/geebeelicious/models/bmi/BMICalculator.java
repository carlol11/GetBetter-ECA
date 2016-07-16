package com.geebeelicious.geebeelicious.models.bmi;

/**
 * Created by mgmalana.
 * BMICalculator class is used by the monitoringFragment
 * for calculating BMI.
 * BMI percentile data retrieved from http://www.who.int/growthref/who2007_bmi_for_age/en/
 */
public class BMICalculator {
    /*
        bmi[x][y]
            x: 0 = P3, 1 = P85, 2 = P97 //P3 = 3rd percentile
            y: age - 5
    */
    private final static float[][] boyBMI = {
            {13.1f, 13.2f, 13.3f, 13.4f, 13.6f,
                    14.9f, 14.2f, 14.6f, 15.1f, 15.6f,
                    16.2f, 16.7f, 17.1f, 17.5f, 17.8f},
            {16.7f, 16.8f, 17.1f, 17.5f, 18.0f,
                    18.6f, 19.3f, 20.1f, 20.9f, 21.9f,
                    22.8f, 23.7f, 24.4f, 25.0f, 25.6f},
            {18.1f, 18.3f, 18.8f, 19.4f, 20.1f,
                    21.0f, 22.0f, 23.1f, 24.2f, 25.3f,
                    26.4f, 27.3f, 28.0f, 28.6f, 29.1f}
    };

    private final static float[][] girlBMI = {
            {12.9f, 12.8f, 12.9f, 13.0f, 13.3f,
                    13.6f, 14.0f, 14.6f, 15.1f, 15.6f,
                    16.1f, 16.4f, 16.6f, 16.7f, 16.7f},
            {16.9f, 17.1f, 17.4f, 17.8f, 18.4f,
                    19.1f, 20.0f, 20.9f, 21.9f, 22.9f,
                    23.7f, 24.2f, 24.7f, 24.9f, 25.1f},
            {18.6f, 18.9f, 19.4f, 20.2f, 21.0f,
                    22.1f, 23.2f, 24.4f, 25.6f, 26.7f,
                    27.6f, 28.2f, 28.6f, 28.9f, 29.0f}
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
        int ageIndex = age - 5;


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
        } else if (patientBMI > bmiChart[2][ageIndex]){ //greater or equal to 95P
            return 3;
        } else if (patientBMI > bmiChart[1][ageIndex]){ //85P to less than 95P
            return 2;
        } else {
            return 1;
        }
    }
}
