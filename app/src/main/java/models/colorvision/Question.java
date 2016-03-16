package models.colorvision;

/**
 * Created by Kate on 03/05/2016.
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
