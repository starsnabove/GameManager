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

/** Class for managing a team names and statistics
 *
 * @author Vincent Kruger
 */
public class Team implements Comparable{
    
    
    public int wins;
    public int losses;
    public int scoredDraws;
    public int noScoreDraws;
    public int goalsScored;
    public int goalsAgainst;
    public String name;

    /** Creates a new team with the given name
     * 
     * @param name the name of the team
     */
    public Team(String name) {
        this.name = name;
    }
    
    /** Creates a new team with the given name and statistics
     * 
     * @param name the team name
     * @param goalsScored how many goals team scored
     * @param goalsAgainst how many goals scored against team
     * @param wins how many wins the team has
     * @param losses how many losses the team has
     * @param scoredDraws how many socred draws the team has
     * @param noScoreDraws how many scoreless draws the teams has
     */
    public Team(String name, int goalsScored, int goalsAgainst, int wins, 
            int losses, int scoredDraws, int noScoreDraws) {
        this.name = name;
        this.goalsScored = goalsScored;
        this.goalsAgainst = goalsAgainst;
        this.wins = wins;
        this.losses = losses;
        this.scoredDraws = scoredDraws;
        this.noScoreDraws = noScoreDraws;
    }
    
    /** creates a new team given the team name and statistics as all strings
     * 
     * @param fromFile array of the team data
     */
    public Team(String [] fromFile) {
        this.name = fromFile[0];
        this.wins = Integer.parseInt(fromFile[1]);
        this.losses = Integer.parseInt(fromFile[2]);
        this.scoredDraws = Integer.parseInt(fromFile[3]);
        this.noScoreDraws = Integer.parseInt(fromFile[4]);
        this.goalsScored = Integer.parseInt(fromFile[5]);
        this.goalsAgainst = Integer.parseInt(fromFile[6]);
    }
    
    /** returns the teams name
     * 
     * @return the team name
     */
    public String getName() {
        return name;
    }
    
    /** the number of wins the team has had
     * 
     * @return the number of wins
     */
    public int getWins() {
        return wins;
    }

    /** the number of team losses
     * 
     * @return the number of losses
     */
    public int getLosses() {
        return losses;
    }

    /** the number of the teams scored draws
     * 
     * @return the number of scored draws
     */
    public int getScoredDraws() {
        return scoredDraws;
    }

    /** the number of the teams scoreless draws
     * 
     * @return the number of scoreless draws
     */
    public int getNoScoreDraws() {
        return noScoreDraws;
    }
    
    /** increases the number of wins of the team by one
     * 
     */
    private void incrementWins() {
        wins++;
    }

    /** increases the number of losses of the team by one
     * 
     */
    private void incrementLosses() {
        losses++;
    }

    /** increases the number of scored draws of the team by one
     * 
     */
    private void incrementScoredDraws() {
        scoredDraws++;
    }

    /** increases the number of scoreless draws of the team by one
     * 
     */
    private void incrementNoScoreDraws() {
        noScoreDraws++;
    }
    
    /** adds the match results for the team
     * 
     * @param goalsScored the goals scored by the team in the match
     * @param goalsAgainst the goals scored against the team in the match
     */
    public void addMatchResults(int goalsScored, int goalsAgainst) {
        this.goalsScored += goalsScored;
        this.goalsAgainst += goalsAgainst;
        
        if (goalsScored > goalsAgainst) {
            incrementWins();
        } else if (goalsAgainst > goalsScored) {
            incrementLosses();
        } else if (goalsScored > 0) {
            incrementScoredDraws();
        } else {
            incrementNoScoreDraws();
        }
    }
 
        
    /** adds the match results for the team
     * 
     * @param goalsScored the goals scored by the team in the match
     * @param goalsAgainst the goals scored against the team in the match
     */
    public void undoMatchResults(int goalsScored, int goalsAgainst) {
        this.goalsScored -= goalsScored;
        this.goalsAgainst -= goalsAgainst;
        if (goalsScored > goalsAgainst) {
            wins--;
        } else if (goalsAgainst > goalsScored) {
            losses--;
        } else if (goalsScored > 0) {
            scoredDraws--;
        } else {
            noScoreDraws--;
        }
        
    }
    
    
    /** get the goal difference of the team
     * 
     * @return the goal difference
     */
    public int getGoalDifference() {
        return goalsScored - goalsAgainst;
    }
    
    /** get the number of goals socred by the team
     * 
     * @return the number of goals scored
     */
    public int getGoalsScored() {
        return goalsScored;
    }
    
    /** get the number of goals scored against the team
     * 
     * @return the number of goals scored against the team
     */
    public int getGoalsAgainst() {
        return goalsAgainst;
    }
    
    /** gets the total score of the team
     * 
     * @return the total score
     */
    public int getScore() {
        return wins * 3 + scoredDraws * 2 + noScoreDraws;
    }

    /** the to string method - aimed at saving the div files
     * 
     * @return a comma separated string of the team data
     */
    @Override
    public String toString() {
        return String.format("%s,%d,%d,%d,%d,%d,%d", name, wins, losses,
                scoredDraws, noScoreDraws, goalsScored, goalsAgainst); 
    }
    
    /** generates the string for storing the team data in a csv file
     * 
     * @return the comma separated string of all the team data
     */
    public String csvString() {
        return String.format("%s,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,", name,
                wins, wins * 3, scoredDraws, scoredDraws * 2, noScoreDraws,
                noScoreDraws, losses, goalsScored, goalsAgainst, 
                getGoalDifference(), getScore());
    }
    
    /** determines if this team has the same statistics as another team
     * 
     * @param other the other team to compare with
     * @return if the teams have exactly the same statistics
     */
    public boolean equals(Team other) {
        return (wins == other.wins && losses == other.losses &&
                scoredDraws == other.scoredDraws &&
                noScoreDraws == other.noScoreDraws &&
                goalsScored == other.goalsScored &&
                goalsAgainst == other.goalsAgainst);
    }
    
    /** compares this team to another given team based primarily on score and
     * secondarily on goal difference
     * 
     * @param other the other Team to be compared with
     * @return integer representing the ordering of the two teams
     */
    @Override
    public int compareTo(Object other) {
        if (other == null) throw new NullPointerException();
        if (this.equals(other)) return 0;
        else if (wins > ((Team) other).wins) return 1;
        else if (wins == ((Team) other).wins &&
                this.getGoalDifference() > ((Team) other).getGoalDifference())
            return 1;
        else return -1;
    }
}
