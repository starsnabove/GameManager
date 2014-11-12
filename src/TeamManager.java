/*
 * Copyright (C) 2014 Vincent Kruger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package gamemanager;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;

/** Class for managing many teams results (known as a division)
 *
 * @author Vincent Kruger
 */
public class TeamManager {
    
    private final ArrayList<Team> teams;
    static final String HEADER = "Team,WIN,T W,SCORED DRAW,T S D,SCORELESS" +
            "DRAW,T SL D,LOSS,G scored, G scored Against,G D,TOTAL,RANK";
    public int length;
    private boolean modified = false;
    private Stack<Object[][]> past;
    
    /** creates a new blank TeamManager
     * 
     */
    public TeamManager() {
        this.teams = new ArrayList<>();
        this.past = new Stack<Object[][]>();
    }
    
    /** creates a new TeamManager with teams of the given names but no
     * statistics
     * 
     * @param teams list of team names
     */
    public TeamManager(String[] teams) {
        this.teams = new ArrayList<>();
        this.past = new Stack<Object[][]>();
        
        for (int i = 0; i < teams.length; ++i) {
            this.teams.add(new Team(teams[i]));
        }
        this.length = this.teams.size();
    }
    
    /** Adds the statistic less teams to the TeamManager
     * 
     * @param teams list of teams names
     */
    public void addTeams(String[] teams) {
        for (String team : teams) {
            this.teams.add(new Team(team));
        }
        this.length = this.teams.size();
        modified = true;
    }
    
    /** gets the team at the given index
     * 
     * @param index the index of the desired team
     * @return the Team at the given index
     */
    public Team getTeam(int index) {
        return this.teams.get(index);
    }
    
    /** adds match results to the team at the given index
     * 
     * @param index the index of the team to add the statistics to
     * @param goalsScored the number of goals scored this match
     * @param goalsAgainst the number of goals scored against the team this
     * match
     */
    public void addMatchResult(int index, int goalsScored, int goalsAgainst) {
        teams.get(index).addMatchResults(goalsScored, goalsAgainst);
        
    }
    
    /** Adds match results to the team with the given name
     * 
     * @param teamName the name of the team to add the results to
     * @param goalsScored The goals scored by the team
     * @param goalsAgainst the goals scored against the team
     */
    public void addMatchResult(String teamName, int goalsScored, int goalsAgainst) {
        Team team = findTeam(teamName);
        team.addMatchResults(goalsScored, goalsAgainst);
        
    }
    
    /** Undoes match results to the team with the given name
     * 
     * @param teamName the name of the team to add the results to
     * @param goalsScored The goals scored by the team
     * @param goalsAgainst the goals scored against the team
     */
    public void undoMatchResult(String teamName, int goalsScored,
            int goalsAgainst) {
        Team team = findTeam(teamName);
        team.undoMatchResults(goalsScored, goalsAgainst);
    }
    
    /** Adds the results of the match to the statistics
     * 
     * @param results list of the results
     */
    public void addMatchResults(Object[][] results) {
        
        for (int i = 0; i < results.length; ++i) {
            String name = (String) results[i][0];
            int gScored = (int) results[i][1];
            int gAgainst = (int) results[i][2];
            boolean dnp = (boolean) results[i][3];
            if (!dnp) addMatchResult(name, gScored, gAgainst);
        }
        past.push(results);
        modified = true;
    }
    
    public Object[][] undoMatchResults() {
        if (this.past.isEmpty()) return null; //nothing to undo
        
        Object [][] results = past.pop();
        for (int i = 0; i < results.length; ++i) {
            String name = (String) results[i][0];
            int gScored = (int) results[i][1];
            int gAgainst = (int) results[i][2];
            boolean dnp = (boolean) results[i][3];
            if (!dnp) undoMatchResult(name, gScored, gAgainst);
        }
        return results;        
    }
    
    
    /** Finds the Team object with the given name
     * 
     * @param teamName the name of the team to find
     * @return the Team object 
     */
    public Team findTeam(String teamName) {
        for (Team team: teams) {
            if (team.getName().equals(teamName)) return team;
        }
        throw new IllegalArgumentException();
    }
    
    /** sorts the teams into order for display
     * 
     */
    public void sort() {
        Collections.sort(teams);
        Collections.reverse(teams);
    }    
    
    /** saves the teams statistics to the given filename
     * 
     * @param filename the filename to save
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException 
     */
    public void saveTeamsFile(String filename) throws FileNotFoundException,
            UnsupportedEncodingException {

        PrintWriter writer;
        writer = new PrintWriter(filename, "UTF-8");
        for (Team team: teams) {
            writer.write(team.toString() + '\n');
        }
        writer.close();
        modified = false;

    }
    
    
    /** saves a csv representation of all statistics to the given filename
     * 
     * @param filename the filename to save to
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException 
     */
    public void exportToCSV(String filename) throws FileNotFoundException,
            UnsupportedEncodingException {
        this.sort();
        PrintWriter writer;
        writer = new PrintWriter(filename, "UTF-8");
        writer.write( HEADER + '\n');
        for (int i = 0; i < length; ++i) {
            Team team = teams.get(i);
            writer.write(team.csvString() + (i + 1) + '\n');
            }
        writer.close();
        }

    /** Returns if there have been changes to the TeamManager
     * 
     * @return if there are changes
     */
    boolean modified() {
        return modified;
    }

    /** Opens the team file at the given file path and loads all the statistics
     * into the TeamManager
     * 
     * @param file the file Path to open
     * @throws IOException 
     */
    void openTeamsFile(Path file) throws IOException {
        Charset charset = Charset.forName("US-ASCII");
        
        BufferedReader reader = Files.newBufferedReader(file, charset);
        String line;
        while ((line = reader.readLine()) != null) {
            String[] info =  line.split(",");
            for (int i = 0; i < info.length; ++i) {
                //info[i] = info[i].replace(" ", "");
                if (info[i].isEmpty()) {
                    info[i] = "0";
                }
            }

            teams.add(new Team(info));
        }
        length = teams.size();
    }
}
