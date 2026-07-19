/**
 * @author Braa Oudeh
 * @author Duc Tan Tran
 * @author Khanh Nguyen
 * @author Paloma Ortiz
 * Purpose: this is the controller for the Planner ap, it acts as a connection between the PlannerView and PlannerModel
 *      The controller also handles the networking as well. It manages user authentication, calendar behavior, task behavior
 */

import java.time.LocalDate;
import javafx.application.Platform;
import java.util.ArrayList;

public class PlannerController {
    private PlannerModel model;
    private User user;
    private Calendar calendar;
    private PlannerClient client;
    private PlannerView view;
    private String curMusic;
    private Audio music;

    /**
     * PlannerController() -- constructor for this class. The model is used as the input, networking and audio is also initiated.
     * @param model - PlannerModel object
     */
    public PlannerController(PlannerModel model){
        this.model = model;
        this.user = null;
        this.calendar = null;
        client = new PlannerClient(this);
        client.connectToServer();
        this.music = new Audio("Login_music_Estavius (Phyrnna).wav");
    }

    /**
     * createUser() -- calls the model to create a new user
     * @param userName - the user name
     * @param passWord - the user password
     * @return true if the user was created, false otherwise
     */
    public boolean createUser(String userName, String passWord){
        return model.createUser(userName, passWord);
    }

    /**
     * login() -- log the user in with their account and loads the user calendar
     * @param userName - the user name
     * @param passWord - the user password
     * @return true if login was successful, false otherwise
     */
    public boolean login(String userName, String passWord){
        User currentUser = model.getUser(userName, passWord);
        if (currentUser == null){
            return false;
        }
        else{
            this.user = currentUser;
            this.calendar = user.getCalendar();
            String[] settings = model.getSettings();
            changeMusic(settings[0]);
            music.playMusic();
            client.sendUserNameToServer(userName);
            return true;
        }
    }
    
    public String[] getSettings() {
    	return model.getSettings();
    }

    /**
     * logout() -- log the user out
     */
    public void logout(){
    	saveUserData();
        this.calendar = null;
        this.user = null;
    }

    /**
     * getCalendarFromUser() -- get the calendar from the current user
     * @return user's Calendar obj
     */
    public Calendar getCalendarFromUser(){
        return this.calendar;
    }

    /**
     * getUser() -- simpler getter method to get the user object
     */
    public User getUser(String userName, String passWord){
        return model.getUser(userName, passWord);
    }

    /**
     * addTask() -- add a task to a specific date in the calendar, also calls the view and model (to save the data)
     * @param dateEncode - date in format YYMMDD
     * @param title - task title
     * @param timeStart - task time start
     * @param timeEnd - task time end
     * @param description - task description
     * @throws Exception if the day doesn't exist
     */
    public void addTask(int dateEncode, String title, int timeStart, int timeEnd, String description) throws Exception{
        Day currDay = calendar.getDate(dateEncode);
        if (currDay != null){
            currDay.addTask(timeStart, timeEnd, title, description);
            System.out.println(currDay.toString());
            String taskData = "ADD|" + dateEncode + "|" + title + "|" + timeStart + "|" + timeEnd + "|" + description;
            client.sendTaskToPlanner(taskData);
        }
        else{
            throw new Exception("Day doesnt exist. Make sure you enter the year as the final 2 digits, the month and day are reasonable numbers.");
        }
    }

    /**
     * editTask() -- edit the task, also send the information to the server
     * @param dateEncode - date in format YYMMDD
     * @param oldTitle - old title
     * @param newTitle - new title
     * @param timeStart - task time start
     * @param timeEnd - task time end
     * @param description - task description
     */
    public void editTask(int dateEncode, String oldTitle, String newTitle, int timeStart, int timeEnd, String description)
    {
        Day currDay = calendar.getDate(dateEncode);
        if (currDay != null){
            Task taskToEdit = currDay.getTask(oldTitle);
            if (taskToEdit != null){
                taskToEdit.setTime(timeStart,timeEnd);
                taskToEdit.setDescription(description);
                taskToEdit.setTitle(newTitle);
                String taskData = "EDIT|" + dateEncode + "|" + oldTitle + "|" + newTitle + "|" + timeStart + "|" + timeEnd + "|" + description;
                client.sendTaskToPlanner(taskData);
                
            }
        }
    }

    /**
     * removeTaskByTitle() -- remove the task by its title (loop)
     * @param dateEncode date in format YYMMDD
     * @param title - task title
     * @return the removed Task if found, null otherwise
     */
    public Task removeTaskByTitle(int dateEncode, String title){
        Day currDay = calendar.getDate(dateEncode);
        Task result = null;
        if (currDay != null){
            result = currDay.removeTaskByTitle(title);
            String taskData = "DELETE|" + dateEncode + "|" + title;
            client.sendTaskToPlanner(taskData);
            return result;
        }
        return null;
    }

    /**
     * various getters and setters
     */
    public void playMusic(){
        music.playMusic();
    }

    public void changeMusic(String fileName){
        music.changeMusic(fileName);
        curMusic = fileName;
    }

    public void stopMusic(){
        music.stopMusic();
    }

    public void continueMusic(){
        music.continueMusic();
    }

    public void decreaseVolumn(){
        music.decreaseVolumn();
    }

    public void increaseVolumn(){
        music.increaseVolumn();
    }

    public ArrayList<String> getAllTracks(){
        return music.getAllTracks();
    }

    public Day getFirstDayOfMonth(int year, int month){
        return calendar.getDate(year * 10000 + month * 100 + 1);
    }

    public Day getFirstDayOfWeek(int dayEncode){
        Day day = calendar.getDate(dayEncode);
        int daysAwayFromMonday = day.getDay();

        for (int i = 0; i < daysAwayFromMonday; i++) {
            day = day.getPreviousDay();
        }
        return day;
    }

    public void saveUserData(){
        model.saveUserAll(curMusic, view.getDisplayMode(), view.getHeaderBackground(), view.saveColors());
    }
    

    public int getTodayDate(){
        return dateEncode(String.valueOf(LocalDate.now()));
    }

    public void setDashBoard(PlannerView view){
        this.view = view;
    }

    /**
     * getFirstDayOfWeekGridView() - get the first day of a week grid view
     * @param firstDayOfWeek - the first day of the week that a month start in
     * @param year - the year
     * @param month - the month
     * @return Day object for the first day in the grid
     */
    public Day getFirstDayOfWeekGridView(Day firstDayOfWeek, int year, int month){
        int weekDayFirstDayOfMonth = firstDayOfWeek.getDay();
        if (weekDayFirstDayOfMonth == 0){
            return firstDayOfWeek;
        }

        int daysAwayFromMonday = weekDayFirstDayOfMonth;
        int prevMonthYear;
        int prevMonth;
        if (month == 1){
            prevMonth = 12;
            prevMonthYear = year-1;
        }
        else{
            prevMonth = month-1;
            prevMonthYear = year;
        }
        if((firstDayOfWeek.getDate() - daysAwayFromMonday) <= 0) {
        	 int prevMonthLength = calendar.getMonthLength(prevMonth);

             return calendar.getDate(prevMonthYear * 10000 + (prevMonth) * 100 + (prevMonthLength - daysAwayFromMonday + 1));
        }
        else {
        	if(prevMonth==12) {
        		return calendar.getDate(prevMonthYear * 10000 + (1) * 100 + 	// fixed bug where prevMonth == 12 -> prevMonth +1 ==13 out of bound
           			 (firstDayOfWeek.getDate() - daysAwayFromMonday + 1));
        	}
        	else
        	 return calendar.getDate(prevMonthYear * 10000 + (prevMonth+1) * 100 + 
        			 (firstDayOfWeek.getDate() - daysAwayFromMonday + 1));
        }
    }

    /**
     * getFirstDayOfMonthGridView() -- get the first day for a month grid view
     * @param firstDayOfMonth - first day of the month
     * @param year - the year
     * @param month - the month
     * @return Day object representing the first day of the month grid ( not technically the first day of the month)
     */
    public Day getFirstDayOfMonthGridView(Day firstDayOfMonth, int year, int month){
        int weekDayFirstDayOfMonth = firstDayOfMonth.getDay();
        if (weekDayFirstDayOfMonth == 0){
            return firstDayOfMonth;
        }

        int daysAwayFromMonday = weekDayFirstDayOfMonth;
        int prevMonthYear;
        int prevMonth;
        if (month == 1){
            prevMonth = 12;
            prevMonthYear = year-1;
        }
        else{
            prevMonth = month-1;
            prevMonthYear = year;
        }
        int prevMonthLength = calendar.getMonthLength(prevMonth);

        return calendar.getDate(prevMonthYear * 10000 + (prevMonth) * 100 + (prevMonthLength - daysAwayFromMonday + 1));
    }

    /**
     * calculateMonthRows() -- calculates how many row is needed for the month view
     * @param firstDayOfMonth -
     * @param year
     * @param month
     * @return
     */
    public int calculateMonthRows(Day firstDayOfMonth, int year, int month){
        int weekDayFirstDayOfMonth = firstDayOfMonth.getDay();
        int monthLength = calendar.getMonthLength(month);

        int totalCells = monthLength + weekDayFirstDayOfMonth;

        double totalRows = (double) totalCells / 7;
        return (int) Math.ceil(totalRows);
    }


    /**
     * getNextDay() -- get the next day, create the next year if the next day doesnt exist
     *     had to use a wrapper because frontend uses int dateEncode, Day class uses Day object
     * @param dayEncode date in format YYMMDD
     * @return Day object of the next day
     */
    public int getNextDay(int dayEncode){
        Day day = calendar.getDate(dayEncode);
        if (day.getNextDay() == null) {	// if the last day is reached
        	calendar.generateNextYear();
        }
        Day nextDay = day.getNextDay();
        return nextDay.getDateEncode();
    }

    /**
     * getPreviousDay() -- get the previous day
     * @param dayEncode date in format YYMMDD
     * @return previous Day object
     */
    public int getPreviousDay(int dayEncode){
        Day day = calendar.getDate(dayEncode);
        Day prevDay = day.getPreviousDay();
        return prevDay.getDateEncode();
    }

    /**
     * getNextWeek() -- get the next week by looping 7 times
     * @param dateEncode date in format YYMMDD
     * @return encoded date of the next week
     */
    public int getNextWeek(int dateEncode) {
        Day day = calendar.getDate(dateEncode);
        int year = (dateEncode / 10000);
        int month = (dateEncode / 100) % 100;
        int date = dateEncode%100;
        
        for (int i = 0; i < 7; i++) {
        	 if(month==12 && date >=20 && calendar.getDate((year+1) * 10000 + month * 100 + date) == null) {
             	calendar.generateNextYear();
             	calendar.printYears();
             }
             
            day = day.getNextDay();
        }
        return day.getDateEncode();
    }

    /**
     * getPrevWeek() -- get the previous week by looping 7 times
     * @param dateEncode date in format YYMMDD
     * @return encoded date of the previous week
     */
    public int getPrevWeek(int dateEncode) {
        Day day = calendar.getDate(dateEncode);
        for (int i = 0; i < 7; i++) {
            day = day.getPreviousDay();
        }
        return day.getDateEncode();
    }

    /**
     * getPrevMonth() -- get the previous month
     * @param dateEncode date in format YYMMDD
     * @return encoded date of the start of the previous month
     */
    public int getPrevMonth(int dateEncode){
        int year = (dateEncode / 10000);
        int month = (dateEncode / 100) % 100;
        int day = 1;


        if (month == 1){
            year -= 1;
            month = 12;
            return year * 10000 + month * 100 + day;
        }
        else{
            month -= 1;
            return year * 10000 + month * 100 + day;
        }
    }
    /**
     * getNextMonth() -- get the next month
     * @param dateEncode date in format YYMMDD
     * @return encoded date of the start of the next month
     */
    public int getNextMonth(int dateEncode){
        int year = (dateEncode / 10000);
        int month = (dateEncode / 100) % 100;
        int day = 1;

        if (month == 12){
            year += 1;
            month = 1;
            
        }
        else{
            month += 1;
        }
        
        int limitTesting = (year+1) * 10000 + month * 100 + day;
        if(calendar.getDate(limitTesting) == null) {
        	calendar.generateNextYear();
        	calendar.printYears();
        }
        
        return year * 10000 + month * 100 + day;
    }

    /**
     * dateEncode() -- using the string YYMMDD, turn it to an actual int
     * @param date
     * @return
     */
    public int dateEncode(String date){
        String[] components = date.trim().split("-");
        int year = Integer.parseInt(components[0].substring(2));
        int month = Integer.parseInt(components[1]);
        int day = Integer.parseInt(components[2]);
        return year * 10000 + month * 100 + day;
    }

    /**
     * addTaskFromNetwork() -- add the task from the network, this is called in applyTasksUpdate which is run on the JavaFX thread to prevent memory leaks and concurrency issues
     * @param dateEncode
     * @param title
     * @param timeStart
     * @param timeEnd
     * @param description
     */
    private void addTaskFromNetwork(int dateEncode, String title, int timeStart, int timeEnd, String description) {
        Day currDay = calendar.getDate(dateEncode);
        if (currDay != null) {
            currDay.addTask(timeStart, timeEnd, title, description);
            model.saveUserData();
        }
    }

    /**
     * editTaskFromNetwork() -- edit the task from the network, this is called in applyTasksUpdate which is run on the JavaFX thread to prevent memory leaks and concurrency issues
     * @param dateEncode
     * @param oldTitle
     * @param newTitle
     * @param timeStart
     * @param timeEnd
     * @param description
     */
    private void editTaskFromNetwork(int dateEncode, String oldTitle, String newTitle, int timeStart, int timeEnd, String description) {
        Day currDay = calendar.getDate(dateEncode);
        if (currDay != null) {
            Task taskToEdit = currDay.getTask(oldTitle);
            if (taskToEdit != null) {
                taskToEdit.setTime(timeStart, timeEnd);
                taskToEdit.setDescription(description);
                taskToEdit.setTitle(newTitle);
                model.saveUserData();
            }
        }
    }

    /**
     * removeTaskByTitleFromNetwork() -- remove the task by title from the network, this is called in applyTasksUpdate which is run on the JavaFX thread to prevent memory leaks and concurrency issues
     * @param dateEncode
     * @param title
     * @return the removed Task if found, null otherwise
     */
    private Task removeTaskByTitleFromNetwork(int dateEncode, String title) {
        Day currDay = calendar.getDate(dateEncode);
        if (currDay != null) {
            return currDay.removeTaskByTitle(title);
        }
        return null;
    }

    /**
     * applyTasksUpdate() -- send the task to the calendar, support add, edit and delete
     * @param taskData
     */
    public void applyTasksUpdate(String taskData){
        String[] parts = taskData.split("\\|");
        String operation = parts[0];
        int dateEncode = Integer.parseInt(parts[1]);
        if (operation.equals("ADD")) {
            String title = parts[2];
            int timeStart = Integer.parseInt(parts[3]);
            int timeEnd = Integer.parseInt(parts[4]);
            String description = parts[5];
            addTaskFromNetwork(dateEncode, title, timeStart, timeEnd, description);
        } else if (operation.equals("EDIT")) {
            String oldTitle = parts[2];
            String newTitle = parts[3];
            int timeStart = Integer.parseInt(parts[4]);
            int timeEnd = Integer.parseInt(parts[5]);
            String description = parts[6];
            editTaskFromNetwork(dateEncode, oldTitle, newTitle, timeStart, timeEnd, description);
        } else if (operation.equals("DELETE")) {
            String title = parts[2];
            removeTaskByTitleFromNetwork(dateEncode, title);
            model.saveUserData();
        }
        Platform.runLater(() -> view.updateDashboard());
    }
    
}
