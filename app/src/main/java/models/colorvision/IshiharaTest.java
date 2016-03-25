package models.colorvision;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by Kate on 03/04/2016.
 * The IshiharaTest class is generated and
 * will contain the generated test items
 * composed of plates and options. The class
 * also keeps track of the score of the user.
 */
public class IshiharaTest {

    private HashMap<Integer, Question> generatedTest;
    private Random randomGenerator;
    private IshiharaPlate[] ishiharaPlates;
    private Option[] options;
    private int score;

    public IshiharaTest(IshiharaPlate[] ishiharaPlates, Option[] options){
        Random random = new Random((int)System.nanoTime());
        this.ishiharaPlates = ishiharaPlates;
        this.options = options;
        this.score = 0;
    }

    public HashMap<Integer, Question> generateTest(){
        generatedTest = new HashMap<Integer, Question>();
        IshiharaPlate plate;
        Option[] generatedOptions;
        for(int i = 0; i<11; i++){
            plate = makeQuestion(i);
            generatedOptions = makeAnswers(plate);
            generatedTest.put(i, new Question(plate, generatedOptions));
        }
        return generatedTest;
    }

    private IshiharaPlate makeQuestion(int i){
        IshiharaPlate plate;
        if(i == 0){
            plate = getPlateWithStyle(6);
        } else if(i%2 == 0){
            plate = getPlateWithStyle(3);
        } else{
            plate = getPlateWithStyle(1);
        }
        return plate;
    }

    private IshiharaPlate getPlateWithStyle(int i){
        boolean isFound = false;
        int index;
        while(!isFound) {
            index = randomGenerator.nextInt(36);
            if ((!ishiharaPlates[index].isAdded()) && (ishiharaPlates[index].getStyle() == i)) {
                isFound = true;
                ishiharaPlates[index].setAdded();
                return ishiharaPlates[index];
            }
        }
        return null;
    }

    private Option getOptionWithShape(String shape){
        for(Option o : options){
            if(o.getShape().equals(shape)){
                o.setAdded();
                return o;
            }
        }
        return null;
    }

    private Option getRandomOption(){
        boolean isFound = false;
        int index;
        while(!isFound){
            index = randomGenerator.nextInt(11);
            if(!options[index].isAdded()){
                isFound = true;
                options[index].setAdded();
                return options[index];
            }
        }
        return null;
    }

    private Option[] makeAnswers(IshiharaPlate plate){
        Option[] generatedOptions = new Option[5];
        int correctAnswer = randomGenerator.nextInt(5);
        generatedOptions[correctAnswer] = getOptionWithShape(plate.getShape());
        for(int i = 0; i<5; i++){
            if (i != correctAnswer) {
                generatedOptions[i] = getRandomOption();
            }
        }
        for(Option o : generatedOptions){
            o.resetAdded();
        }
        return generatedOptions;
    }

    public IshiharaPlate getPlate(int i){
        return generatedTest.get(i).getIshiharaPlate();
    }

    public Option[] getOptions(int i){
        return generatedTest.get(i).getOptions();
    }

    private Option getOption(int questionNumber, int optionNumber){
        return getOptions(questionNumber)[optionNumber];
    }

    private Option getCorrectAnswer(int i){
        for(Option o : getOptions(i)){
            if(o.getShape().equals(getPlate(i).getShape())){
                return o;
            }
        }
        return null;
    }

    private boolean isCorrect(Option a, Option b){
        if(a.getShape().equals(b.getShape())){
            return true;
        }
        else {
            return false;
        }
    }

    private void addScore(){
        score++;
    }

    public int getScore(){
        return score;
    }

    public void checkAnswer(int questionNumber, int answer){
        Option userAnswer = getOption(questionNumber, answer);
        Option correctAnswer = getCorrectAnswer(questionNumber);

        if(isCorrect(userAnswer, correctAnswer)){
            addScore();
        }
    }



}
