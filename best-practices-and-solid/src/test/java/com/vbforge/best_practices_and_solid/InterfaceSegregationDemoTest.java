package com.vbforge.best_practices_and_solid;

import com.vbforge.best_practices_and_solid.InterfaceSegregationDemo.AllInOneDevice;
import com.vbforge.best_practices_and_solid.InterfaceSegregationDemo.BasicPrinter;
import com.vbforge.best_practices_and_solid.InterfaceSegregationDemo.DocumentScanner;
import com.vbforge.best_practices_and_solid.InterfaceSegregationDemo.Eatable;
import com.vbforge.best_practices_and_solid.InterfaceSegregationDemo.FatHuman;
import com.vbforge.best_practices_and_solid.InterfaceSegregationDemo.FatRobot;
import com.vbforge.best_practices_and_solid.InterfaceSegregationDemo.FatWorker;
import com.vbforge.best_practices_and_solid.InterfaceSegregationDemo.Human;
import com.vbforge.best_practices_and_solid.InterfaceSegregationDemo.Robot;
import com.vbforge.best_practices_and_solid.InterfaceSegregationDemo.Sleepable;
import com.vbforge.best_practices_and_solid.InterfaceSegregationDemo.Workable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("InterfaceSegregationDemo — API contracts")
class InterfaceSegregationDemoTest {

    @Nested
    @DisplayName("1. The fat interface")
    class FatInterfaceSection {

        @Test
        @DisplayName("a human implements every method of the fat interface")
        void humanImplementsEverything() {
            FatWorker human = new FatHuman();

            assertThat(human.work()).isEqualTo(8);
            assertThat(human.eat()).isEqualTo("human eats lunch");
            assertThat(human.sleep()).isEqualTo("human sleeps");
        }

        @Test
        @DisplayName("a robot has to fake the methods it cannot support")
        void robotFakesMethods() {
            FatWorker robot = new FatRobot();

            assertThatThrownBy(robot::eat).isInstanceOf(UnsupportedOperationException.class);
            assertThatThrownBy(robot::sleep).isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("code that compiles for robots still crashes at runtime")
        void lunchBreakCrashesForRobots() {
            assertThat(InterfaceSegregationDemo.lunchBreakBad(Collections.<FatWorker>singletonList(new FatHuman())))
                    .isEqualTo("human eats lunch;");
            assertThatThrownBy(() -> InterfaceSegregationDemo.lunchBreakBad(
                    Arrays.<FatWorker>asList(new FatHuman(), new FatRobot())))
                    .isInstanceOf(UnsupportedOperationException.class)
                    .hasMessage("Robots do not eat");
        }
    }


    @Nested
    @DisplayName("2. Segregated role interfaces")
    class RolesSection {

        @Test
        @DisplayName("a human plays all three roles")
        void humanPlaysAllRoles() {
            Human human = new Human("Ann", 8, "soup");

            assertThat(human.work()).isEqualTo(8);
            assertThat(human.eat()).isEqualTo("Ann eats soup");
            assertThat(human.sleep()).isEqualTo("Ann sleeps");
        }

        @Test
        @DisplayName("a robot only plays the Workable role — no fake methods")
        void robotOnlyWorks() {
            Robot robot = new Robot(24);

            assertThat(robot.work()).isEqualTo(24);
            assertThat(robot).isNotInstanceOf(Eatable.class).isNotInstanceOf(Sleepable.class);
        }

        @Test
        @DisplayName("single-method roles can be written as lambdas")
        void rolesAreFunctionalInterfaces() {
            Workable worker = () -> 5;
            Eatable snacker = () -> "snack";

            assertThat(worker.work()).isEqualTo(5);
            assertThat(snacker.eat()).isEqualTo("snack");
        }
    }


    @Nested
    @DisplayName("3. Clients depend on the smallest interface they need")
    class SmallestInterfaceSection {

        @Test
        @DisplayName("a basic printer is enough for printReport")
        void basicPrinterIsAccepted() {
            assertThat(InterfaceSegregationDemo.printReport(new BasicPrinter(), "Q1"))
                    .isEqualTo("printed: REPORT: Q1");
        }

        @Test
        @DisplayName("an all-in-one device is accepted too")
        void allInOneIsAccepted() {
            assertThat(InterfaceSegregationDemo.printReport(new AllInOneDevice(), "Q2"))
                    .isEqualTo("printed: REPORT: Q2");
        }

        @Test
        @DisplayName("the all-in-one device adds scanning and faxing, the basic printer does not")
        void extraRolesAreOptional() {
            AllInOneDevice device = new AllInOneDevice();

            assertThat(device.scan("doc")).isEqualTo("scanned: doc");
            assertThat(device.fax("doc", "123")).isEqualTo("faxed doc to 123");
            assertThat(new BasicPrinter()).isNotInstanceOf(DocumentScanner.class);
        }
    }
}
