package models.colorvision;

/**
 * Created by Kate on 03/05/2016.
 * The Question class represents a question
 * asked during the color vision test. The
 * class contains an Ishihara plate and the
 * multiple choice options that the user
 * will choose an answer from.
 */
public class Question {

    private IshiharaPlate ishiharaPlate;
    private Option[] options;

    public Question(IshiharaPlate ishiharaPlate, Option[] options){
        this.ishiharaPlate = ishiharaPlate;
        this.options = options;
    }

    public IshiharaPlate getIshiharaPlate() {
        return ishiharaPlate;
    }

    public Option[] getOptions() {
        return options;
    }



}
