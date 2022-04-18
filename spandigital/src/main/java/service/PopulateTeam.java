package service;

import constants.Constants;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PopulateTeam {

    private static Map<String, Integer> rankeableTeams = new HashMap<>();

    /**
     * method to validate the correct insert format
     * @param vsTeams
     * @return
     */
    private boolean validateInsert(String vsTeams) {
        if (vsTeams.length() > 6 && vsTeams.matches(Constants.VALIDATEINSERTION)) {
            return true;
        }
        return false;
    }

    /**
     * method to validate a correct input and
     * getting strings
     * @param teams
     */
    private void addTeamsToRanking(String teams) {
        String teamName = "(\\w\\w\\D+)";
        String teamResult = "([0-9]*)";
        Pattern PATTERN_SPORT_AND_HOME_TEAM_RESULT_AWAY_TEAM = Pattern.compile(teamName + "\\s" + teamResult + ",\\s" + teamName + "\\s" + teamResult, Pattern.UNICODE_CHARACTER_CLASS);

        Matcher matcher = PATTERN_SPORT_AND_HOME_TEAM_RESULT_AWAY_TEAM.matcher(teams);
        if (matcher.matches()) {
            setNewRankeables(matcher.group(1), Integer.parseInt(matcher.group(2)), matcher.group(3), Integer.parseInt(matcher.group(4)));
        }
    }

    /**
     * method to set into the map and adding
     * pointing
     * @param teamA
     * @param pointsA
     * @param teamB
     * @param pointsB
     */
    private void setNewRankeables(String teamA, int pointsA, String teamB, int pointsB) {
        int actualRankA = rankeableTeams.containsKey(teamA) ? rankeableTeams.get(teamA) : 0;
        int actualRankB = rankeableTeams.containsKey(teamB) ? rankeableTeams.get(teamB) : 0;
        int winnerFlag = pointsA - pointsB;

        if (winnerFlag > 0) {
            rankeableTeams.put(teamA, actualRankA + 3);
            rankeableTeams.put(teamB, actualRankB);
        }
        if (winnerFlag < 0) {
            rankeableTeams.put(teamB, actualRankB + 3);
            rankeableTeams.put(teamA, actualRankA);
        }
        if (winnerFlag == 0) {
            rankeableTeams.put(teamA, actualRankA + 1);
            rankeableTeams.put(teamB, actualRankB + 1);
        }
    }

    /**
     * method to order our map
     */
    private void orderRankeable() {
        rankeableTeams = rankeableTeams.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    /**
     * method to printe expected result
     */
    public void printResult() {
        orderRankeable();
        if (!rankeableTeams.isEmpty()) {
            AtomicInteger index = new AtomicInteger(1);
            rankeableTeams.forEach((s, i) -> {
                System.out.println(index + ". " + s + ", " + i + " pts");
                index.getAndIncrement();
            });
        } else {
            System.out.println("no entries founded");
        }

    }

    /**
     * method to read fie
     * @param setRank
     * @throws IOException
     */
    private void readFile(String setRank) throws IOException {
        File file = new File(setRank);
        String extension = setRank.substring(setRank.lastIndexOf("."));

        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        while ((st = br.readLine()) != null) {
            if(extension.equals(".csv")){
                int first = st.indexOf(",");
                int last = st.lastIndexOf(",");
                st = replaceChar(st, first, last);
            }
            insertNewRegister(st);
        }
    }

    /**
     * method to read a new set
     * @param st
     * @throws IOException
     */
    public void newSet(String st) throws IOException {
        if(isPathFile(st)){
            readFile(st);
        }else{
            insertNewRegister(st);
        }
    }

    /**
     * method to add new register to our map
     * @param st
     */
    private void insertNewRegister(String st){
        boolean isValid = validateInsert(st);
        if (isValid) {
            System.out.println("adding to ranking...");
            addTeamsToRanking(st);
        }
    }


    /**
     * this method is to know if new insert is
     * a path file
     * @param st
     * @return
     */
    private boolean isPathFile(String st){
        if (st.matches(Constants.VALIDATEISPATH)){
            return true;
        }
        return false;
    }


    /**
     * this methos is used to put string in a valid format
     * when is a CSV file
     * str = String
     * indaxI = first position of separation char for colums
     * indaxF = last position of separation char for colums
     * */
    private String replaceChar(String str, int indexI, int indexF) {
        return (str.substring(0, indexI) + " " + str.substring(indexI + 1, indexF) + " " + str.substring(indexF + 1)).replace(",", ", ");
    }
}
