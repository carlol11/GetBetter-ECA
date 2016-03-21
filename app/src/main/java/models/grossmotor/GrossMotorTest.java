package models.grossmotor;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.R;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kate on 03/21/2016.
 *
 */
public class GrossMotorTest {

    private GrossMotorSkill[] grossMotorSkills;
    private GrossMotorSkill[] testSkills;
    private MusicPlayer musicPlayer;
    private int currentSkill;


//skill, type, instruction, duration
    public GrossMotorTest(Context context){
        musicPlayer = new MusicPlayer(context);
        grossMotorSkills = new GrossMotorSkill[8];
        testSkills = new GrossMotorSkill[3];
        currentSkill = 0;
        grossMotorSkills[0] = new GrossMotorSkill("Balance Walk", "Balance", "Walk along narrorw beam or line marked on the ground - forwards, backwards, and sideways.", 30000);
        grossMotorSkills[1] = new GrossMotorSkill("Jump On Spot", "Jumping", "Jump on spot, forwards from a line with two feet together.", 30000);
        grossMotorSkills[2] = new GrossMotorSkill("Run in Place", "Running", "Run in place for one minute.", 60000);
        grossMotorSkills[3] = new GrossMotorSkill("Hop in Place", "Hopping", "Hop on one foot for 20 seconds.", 20000);
        grossMotorSkills[4] = new GrossMotorSkill("Flamingo Stand", "Balance", "Stand and balance on one foot for 20 seconds.", 20000);
        grossMotorSkills[5] = new GrossMotorSkill("Walk in Place", "Walking","Walk in place for one minute", 60000);
        grossMotorSkills[6] = new GrossMotorSkill("March in Place", "Marching", "March in place", 60000);
        grossMotorSkills[7] = new GrossMotorSkill("Jog in Place", "Jogging", "Jog in place", 60000);
    }
    //TODO: Add more skills; fix current skills (current ones are just for testing)

    private GrossMotorSkill getRandomSkill(){
        Random random = new Random();
        boolean isFound = false;
        GrossMotorSkill temp = null;
        while(!isFound){
            temp = grossMotorSkills[random.nextInt(grossMotorSkills.length - 1)];
            for(int i = 0; i<testSkills.length; i++){
                if(i == 0 || (i > 0 && !temp.getSkillName().equals(testSkills[i].getSkillName()) && !temp.getType().equals(testSkills[i].getType()))){
                    isFound = true;
                    break;
                }
            }
        }
        return temp;
    }

    public void makeTest() {
        for (int i = 0; i < 3; i++) {
            testSkills[i] = getRandomSkill();
        }
    }

    public void setCurrentSkill(int skillNumber){
        currentSkill = skillNumber;
    }

    public void performSkill(int skillNumber, final TextView timerView, final LinearLayout answers){
        GrossMotorSkill skill = testSkills[skillNumber];
        musicPlayer.setRandomSong(skill.getDuration());
        CountDownTimer countDownTimer = new CountDownTimer(skill.getDuration(), 1000) {
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
                System.out.println("FINISHING");
                answers.setVisibility(View.VISIBLE);
                for (int j = 0; j<answers.getChildCount(); j++){
                    View view = answers.getChildAt(j);
                    view.setVisibility(View.VISIBLE);
                }
                musicPlayer.stopMusic();


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






}
