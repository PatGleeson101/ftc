package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="BrobotManualOp")
public class BrobotManualOp extends LinearOpMode {
    //Initialise robot devices
    private DcMotor lDrive;
    private DcMotor rDrive;
    private DcMotor sWind;
    private Servo clawExtend;
    private Servo clawExtend2;
    private Servo clawGrab;
    private Servo fndGrab;

    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {
        //Get & associate hardware devices
        lDrive = hardwareMap.get(DcMotor.class, "lDrive");
        rDrive = hardwareMap.get(DcMotor.class, "rDrive");
        sWind = hardwareMap.get(DcMotor.class, "sWind");
        clawExtend = hardwareMap.get(Servo.class, "clawExtend");
        clawExtend2 = hardwareMap.get(Servo.class, "clawExtend2");
        clawGrab = hardwareMap.get(Servo.class, "clawGrab");
        fndGrab = hardwareMap.get(Servo.class, "fndGrab");

        //Initialise variables
        //Function enabled/disabled
        boolean driveEnabled = true;
        boolean sliderEnabled = true;
        boolean clawEnabled = true;
        boolean fndEnabled = true;

        //Motor power
        double lDrivePower = 0;
        double rDrivePower = 0;
        double sWindPower = 0;

        //Time
        double time = 0;
        double elapsed = 0;

        //Claw positions
        double clawIn = 0.0005;
        double clawOut = 0.8;
        double gripPos = 0.1;
        double releasePos = 0.6;
        double grabCurrent = releasePos;
        double clawCurrent = clawIn;

        //Foundation grabber positions
        double fndUp = 0.96;
        double fndDown = 0.5;
        boolean fndBelowInitial = false;

        //Set initial servo states
        fndGrab.setPosition(fndUp);
        clawExtend.setPosition(clawCurrent);
        clawExtend2.setPosition(1-clawCurrent);
        clawGrab.setPosition(grabCurrent);

        //Display Initialised status
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        //Get initial time
        time = runtime.seconds();

        // Run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            //Display telemetry data
            telemetry.addData("Status", "Running");
            telemetry.addData("fndServoPos", fndGrab.getPosition());
            telemetry.addData("clawExtServoPos", clawExtend.getPosition());
            telemetry.addData("grabServoPos", clawGrab.getPosition());
            telemetry.update();

            //Update time data (CURRENTLY OBSOLETE)
            elapsed = runtime.seconds() - time;
            time += elapsed;

            /*Gamepad cheatsheet:
                gamepad1:
                Left Stick: Driving
                    left_stick_y: top (-1) -> bottom (+1) => drive
                    left & right shoulder buttons -> turning

                gamepad2:
                Left Stick: Slider
                    left_stick_y: top (-1) -> bottom (+1) => slider up/down speed
            */


            //Drive
            if (driveEnabled) {
                if (this.gamepad1.left_bumper) {
                    lDrivePower = 0.6;
                    rDrivePower = 0.6;
                } else if (this.gamepad1.right_bumper) {
                    lDrivePower = -0.6;
                    rDrivePower = -0.6;
                } else {
                    lDrivePower = this.gamepad1.left_stick_y;
                    rDrivePower = -this.gamepad1.left_stick_y;
                }

                //Apply power
                lDrive.setPower(lDrivePower);
                rDrive.setPower(rDrivePower);
            }

            //Slider
            if (sliderEnabled) {
                //Get motor power from gamepad
                sWindPower = -this.gamepad2.left_stick_y/2;

                //Set power
                sWind.setPower(sWindPower);
            }

            //Claw
            if (clawEnabled) {
                //Claw extension
                if ((this.gamepad2.right_stick_y < 0)&&(clawCurrent < clawOut)) {
                    clawCurrent += 0.003;
                    clawExtend.setPosition(clawCurrent);
                    clawExtend2.setPosition(1-clawCurrent);
                } else if ((this.gamepad2.right_stick_y > 0)&&(clawCurrent > clawIn)) {
                    clawCurrent -= 0.003;
                    clawExtend.setPosition(clawCurrent);
                    clawExtend2.setPosition(1-clawCurrent);
                }

                //Claw grabbing
                if ((this.gamepad2.right_trigger > 0)&&(grabCurrent > gripPos)) {
                    grabCurrent -= 0.003;
                    clawGrab.setPosition(grabCurrent);
                } else if ((this.gamepad2.right_bumper)&&(grabCurrent < releasePos)) {
                    grabCurrent += 0.003;
                    clawGrab.setPosition(grabCurrent);
                }
            }

            //Foundation
            if (fndEnabled) {
                if (this.gamepad1.left_trigger != 0) {
                    if (!fndBelowInitial) {
                        fndBelowInitial = true;
                    }
                    fndGrab.setPosition(fndUp + this.gamepad1.left_trigger*(fndDown-fndUp));
                } else if (fndBelowInitial) {
                    fndGrab.setPosition(fndUp);
                    fndBelowInitial = false;
                }
            }
        }
    }

}
