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
        randomGenerator = new Random((int)System.nanoTime());
        this.ishiharaPlates = ishiharaPlates;
        this.options = options;
        this.score = 0;
    }

    //Returns generated IshiharaTest by choosing random plates and generating choices for each chosen plate
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

    //Returns IshiharaPlate based on question index on which the necessary plate style is based
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

    //Returns IshiharaPlate based on selected style that has not yet been added in the generated test
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

    //Returns an Option based on given shape
    private Option getOptionWithShape(String shape){
        for(Option o : options){
            if(o.getShape().equals(shape)){
                o.setAdded();
                return o;
            }
        }
        return null;
    }

    //Returns a random Option
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

    //Returns Option[5] for a given plate, 1 correct option and 4 random options
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

    //Return IshiharaPlate from generated test based on index
    public IshiharaPlate getPlate(int i){
        return generatedTest.get(i).getIshiharaPlate();
    }

    //Return Option[] from generated test based on index
    public Option[] getOptions(int i){
        return generatedTest.get(i).getOptions();
    }

    //Return an Option from given questionNumber and optionNumber
    private Option getOption(int questionNumber, int optionNumber){
        return getOptions(questionNumber)[optionNumber];
    }

    //Return correct Option based on index of generated test
    private Option getCorrectAnswer(int i){
        for(Option o : getOptions(i)){
            if(o.getShape().equals(getPlate(i).getShape())){
                return o;
            }
        }
        return null;
    }

    //Return boolean based on comparison of user-selected Option and correct Option
    private boolean isCorrect(Option a, Option b){
        if(a.getShape().equals(b.getShape())){
            return true;
        }
        else {
            return false;
        }
    }

    //Increments score by 1
    private void addScore(){
        score++;
    }

    public int getScore(){
        return score;
    }

    //Checks answer of given question given the questionNumber and the user-selected answer
    public void checkAnswer(int questionNumber, int answer){
        Option userAnswer = getOption(questionNumber, answer);
        Option correctAnswer = getCorrectAnswer(questionNumber);

        if(isCorrect(userAnswer, correctAnswer)){
            addScore();
        }
    }



}
