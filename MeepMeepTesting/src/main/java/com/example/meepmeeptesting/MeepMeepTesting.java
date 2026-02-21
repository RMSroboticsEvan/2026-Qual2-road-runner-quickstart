package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        // Declare bot with constraints
        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(70, 70, Math.toRadians(180), Math.toRadians(180), 15)
                .build();

        // Simple test path
        myBot.runAction(
                myBot.getDrive().actionBuilder(new Pose2d(62, 16, Math.toRadians(180)))
                        .strafeToLinearHeading(new Vector2d(-10, 6), Math.toRadians(45))
                        .splineToSplineHeading(new Pose2d(22, 25, Math.toRadians(90)), Math.toRadians(95))
                        .splineToSplineHeading(new Pose2d(22, 60, Math.toRadians(90)), Math.toRadians(90))
                        .strafeToLinearHeading(new Vector2d(22, 25), Math.toRadians(90))
                        .strafeToLinearHeading(new Vector2d(-16, 6), Math.toRadians(45))
                        .splineToLinearHeading(new Pose2d(17, 62, Math.toRadians(125)), Math.toRadians(70))
                        .strafeToLinearHeading(new Vector2d(17, 30), Math.toRadians(90))
                        .strafeToLinearHeading(new Vector2d(-22, 6), Math.toRadians(90))
                        .strafeToLinearHeading(new Vector2d(-16, 52), Math.toRadians(90))
                        .strafeToLinearHeading(new Vector2d(-16, 6), Math.toRadians(45))
                        .strafeToLinearHeading(new Vector2d(40,35), Math.toRadians(90))
                        .strafeToLinearHeading(new Vector2d(40, 67), Math.toRadians(90))
                        .strafeToLinearHeading(new Vector2d(-14, 6), Math.toRadians(45))
                        .splineToSplineHeading(new Pose2d(12, 50, Math.toRadians(90)), Math.toRadians(90))

                        .build()
        );

        // Use the DECODE Juice dark field image from the MeepMeep background enum
        // (check MeepMeep.Background in your IDE for the exact constant name)
        meepMeep
                .setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}
