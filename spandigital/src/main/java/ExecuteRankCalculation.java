import service.PopulateTeam;

import java.io.IOException;
import java.util.Scanner;

public class ExecuteRankCalculation {

    private static PopulateTeam populateTeam = new PopulateTeam();


    public static void main(String[] args) throws IOException {

        Scanner stdin = new Scanner(System.in);
        System.out.println("Hello This is may Ranking application\nto can use it follow steps");
        System.out.println("type a phat file or set a new value");
        System.out.println("pres x to out");

        String input = stdin.nextLine();


        while (!input.equals("x")) {
                populateTeam.newSet(input);
                input = stdin.nextLine();
            }

        populateTeam.printResult();

    }
}
