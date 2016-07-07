package com.geebeelicious.geebeelicious.models.grossmotor;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.geebeelicious.geebeelicious.fragments.GrossMotorFragment;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kate on 03/21/2016.
 * The GrossMotorTest class represents the
 * gross motor test that the child will
 * take. It generates a short list of skills
 * from the general list of skills, as well as
 * generates music for the test. The class
 * allows the test to be generated for each
 * user and allows the user to perform
 * the skill.
 */
public class GrossMotorTest {

    private GrossMotorSkill[] grossMotorSkills;
    private GrossMotorSkill[] testSkills;
    private MusicPlayer musicPlayer;
    private int currentSkill;

    private CountDownTimer countDownTimer;

    public GrossMotorTest(Context context){
        musicPlayer = new MusicPlayer(context);
        grossMotorSkills = new GrossMotorSkill[8];
        testSkills = new GrossMotorSkill[3];
        currentSkill = 0;
        grossMotorSkills[0] = new GrossMotorSkill("Jumping Jacks", "Jumping", "Do jumping jacks.", 30000);
        grossMotorSkills[1] = new GrossMotorSkill("Jump in Place", "Jumping", "Jump in place.", 30000);
        grossMotorSkills[2] = new GrossMotorSkill("Run in Place", "Running", "Run in place.", 40000);
        grossMotorSkills[3] = new GrossMotorSkill("Hop in Place", "Hopping", "Hop on one foot.", 20000);
        grossMotorSkills[4] = new GrossMotorSkill("One Foot Balance", "Balance", "Stand and balance on one foot", 15000);
        grossMotorSkills[5] = new GrossMotorSkill("Walk in Place", "Walking","Walk in place", 60000);
        grossMotorSkills[6] = new GrossMotorSkill("March in Place", "Marching", "March in place", 60000);
        grossMotorSkills[7] = new GrossMotorSkill("Jog in Place", "Jogging", "Jog in place", 40000);
    }

    private GrossMotorSkill getRandomSkill(int testSkillCounter){
        Random random = new Random((int)System.nanoTime());
        boolean isFound = false;
        GrossMotorSkill temp = null;

        while(!isFound){
            temp = grossMotorSkills[random.nextInt(grossMotorSkills.length - 1)];
            if(!checkSkillDuplicates(testSkills, temp)){
                isFound = true;
                break;
            }
        }
        return temp;
    }

    private boolean checkSkillDuplicates(GrossMotorSkill[] array, GrossMotorSkill key){
        for(GrossMotorSkill gms : array){
            if(key == gms){
                return true;
            }
        }
        return false;
    }

    public void makeTest() {
        for (int i = 0; i < 3; i++) {
            testSkills[i] = getRandomSkill(i);
        }
    }

    public void setCurrentSkill(int skillNumber){
        currentSkill = skillNumber;
    }

    public void performSkill(int skillNumber, final TextView timerView, final LinearLayout answers, final GrossMotorFragment.OnFragmentInteractionListener grossMotorInteraction){
        GrossMotorSkill skill = testSkills[skillNumber];
        musicPlayer.setRandomSong(skill.getDuration());
        countDownTimer = new CountDownTimer(skill.getDuration(), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String timerString = "" + String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished));
                timerView.setText(timerString);
            }

            @Override
            public void onFinish() {
                timerView.setText("");
                answers.setVisibility(View.VISIBLE);
                for (int j = 0; j<answers.getChildCount(); j++){
                    View view = answers.getChildAt(j);
                    view.setEnabled(true);
                    view.setVisibility(View.VISIBLE);
                }
                musicPlayer.stopMusic();
                grossMotorInteraction.onHideNAButton();
            }
        };

        countDownTimer.start();
        musicPlayer.playMusic();
    }

    public GrossMotorSkill getCurrentSkill(){
        return testSkills[currentSkill];
    }

    public int getCurrentSkillNumber(){
        return currentSkill;
    }

    public String getAllResults(){
        String result = "";
        for(GrossMotorSkill gms : testSkills){
            result += gms.getSkillName() + " : " + gms.getAssessment() + "\n";
        }
        return result;
    }

    public String getFinalResult(){
        int pass = 0;
        int fail = 0;
        int na = 0;
        String assessment;
        for(GrossMotorSkill gms : testSkills){
            assessment = gms.getAssessment();
            if(assessment.equals("Pass")){
                pass++;
            }else if(assessment.equals("Fail")){
                fail++;
            }else if(assessment.equals("N/A")){
                na++;
            }
        }
        if(pass>=2){
            return "Pass";
        } else if(na >=2){
            return "N/A";
        } else{
            return "Fail";
        }
    }

    public void endTest(){
        musicPlayer.stopMusic();
    }


    public void skipTest(TextView timerView, GrossMotorFragment.OnFragmentInteractionListener grossMotorInteraction) {
        countDownTimer.cancel();
        timerView.setText("");
        musicPlayer.stopMusic();
    }
}
