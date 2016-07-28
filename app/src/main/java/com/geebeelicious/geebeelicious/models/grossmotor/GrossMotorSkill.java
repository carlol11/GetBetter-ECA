package com.geebeelicious.geebeelicious.models.grossmotor;

/**
 * Created by Kate on 03/21/2016.
 * The GrossMotorSkill class represents a
 * gross motor skill. It contains the name
 * of the skill, the type of skill,
 * instructions to complete the skill, and
 * the duration for which the skill will
 * be performed.
 */
public class GrossMotorSkill {

    private String skillName;
    private String type;
    private String instruction;
    private int duration; //milliseconds (1s = 1000ms)
    private int skillResImage;
    private String assessment;
    private boolean isTested;

    public GrossMotorSkill(String skillName, String type, String instruction, int duration, int skillResImage) {
        this.skillName = skillName;
        this.type = type;
        this.instruction = instruction;
        this.duration = duration;
        this.skillResImage = skillResImage;
        this.isTested = false;
        this.assessment = "No Results";
    }

    public String getSkillName() {
        return skillName;
    }

    public String getType() {
        return type;
    }

    public String getInstruction() {
        return instruction;
    }

    public int getDuration() {
        return duration;
    }

    public int getSkillResImage(){
        return skillResImage;
    }

    public boolean isTested(){
        return isTested;
    }

    public void setTested(){
        isTested = true;
    }

    public void setSkillPassed(){
        assessment = "Pass";
    }

    public void setSkillFailed(){
        assessment = "Fail";
    }

    public void setSkillSkipped(){
        assessment = "N/A";
    }

    public String getAssessment(){
        return assessment;
    }

}
