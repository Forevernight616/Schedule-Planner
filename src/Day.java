/**
 * @author Khanh Nguyen
 * @author Duc Tan Tran
 * @author Paloma Ortiz
 * @author Braa Oudeh
 * filename Day.java
 * 
 * This file holds the implementation of class Day
 */

import java.util.ArrayList;

/**
 * class Day
 * This class represents a Day in a planner
 * holds the tasks for the day
 */
public class Day {
	private ArrayList<Task> tasks;
	private int weekDay;
	private int month;
	private int date;
	private int year;
	private Day nextDay;
	private Day previousDay;
	private int dateEncode;
	
	private Calendar.Week curWeek;
	private Calendar.Month curMonth;
	private Calendar.Year curYear;
	
	/**
	 * constructor
	 * 
	 * @param dateEncode - the integer encoding of the date, format YYMMDD
	 */
	public Day(int dateEncode) {
		this.dateEncode = dateEncode;
		this.tasks = new ArrayList<Task>();
		this.month = (dateEncode/100)%100;
		this.date = dateEncode%100;
		this.year = dateEncode/10000;
		this.weekDay = calculateDay();
		this.nextDay=null;
		this.previousDay=null;
		this.curWeek = null;
		this.curMonth = null;
		this.curYear = null;
	}
	
	/**
	 * getDay()
	 * Calculate the weekDay of this Day
	 * 0 = Monday, 1 = Tuesday, 2 = Wednesday, 3 = Thursday, 4 = Friday, 5= Saturday, 6 = Sunday
	 * @return the weekday of the date of this Task
	 */
	private int calculateDay() {
		int monthVal = month;
		int yearVal = year;
		if (month ==1 || month ==2) {
			monthVal += 12;
			yearVal-=1;
		}
		int monthCode = 13*(monthVal+1)/5;
		int yearCode = yearVal + yearVal/4;
		int centuryCode = 20/4-2*20;
		int sum = date + monthCode + yearCode + centuryCode; 
		int result =  (sum%7) -2;
		while(result<0) {
			result+=7;
		}
		return result;
	}
	
	public void setCurWeek(Calendar.Week week) {
		this.curWeek = week;
	}
	
	public void setCurMonth(Calendar.Month month) {
		this.curMonth = month;
	}
	public void setCurYear(Calendar.Year year) {
		this.curYear = year;
	}
	
	public Calendar.Week getCurWeek() {
		return curWeek;
	}
	public Calendar.Month getCurMonth() {
		return curMonth;
	}
	public Calendar.Year getCurYear() {
		return curYear;
	}
	
	/**
	 * Add new task to the Day, automatically sort the Task list by time
	 * @param time	- the time of the task
	 * @param title	- the title of the task. Temporarily is a key value
	 * @param description	- the description of the task
	 * 
	 * TODO decide if title should be key, or time should be key, or no restriction at all
	 */
	public void addTask(int timeStart, int timeEnd, String title, String description) {
		Task newTask = new Task(timeStart, timeEnd, title);
		newTask.setDescription(description);
		tasks.add(newTask);
		tasks.sort(null);
	}
	
	/**
	 * Remove the first occurrence of task with title specified by parameter
	 * 
	 * @param title the title of the task
	 */
	public Task removeTaskByTitle(String title) {
		for (Task task: tasks) {
			if (task.getTitle().equals(title)) {
				tasks.remove(task);
				return task;	// This deletion works during iteration, because return exit the method before the Iterator.getNext() is called 
			}
		}
		return null;
	}
	/**
	 * Remove the task using reference to the exact task
	 * This method should be called when the [REMOVE TASK] button is pressed in the GUI
	 * 
	 * @param task
	 * @return
	 */
	public void removeTaskByReference(Task task) {
		tasks.remove(task);
	}
	
	/**
	 * Various getters and setters
	 */
	public int getDay() {
		return weekDay;
	}
	public int getDate() {
		return date;
	}
	public int getMonth() {
		return month;
	}
	public int getYear() {
		return year;
	}
	
	public Day getNextDay() {
		return nextDay;
	}
	public Day getPreviousDay()
	{
		return previousDay;
	}
	public boolean isEmpty() {
		return tasks.isEmpty();
	}
	
	public void setNextDay(Day nextDay) {
		this.nextDay = nextDay;
	}
	
	public void setPreviousDay(Day previousDay) {
		this.previousDay = previousDay;
	}
	
	public Task getTask(String title) {
		for (Task task: tasks) {
			if (task.getTitle().equals(title)) {
				return task;
			}
		}
		return null;
	}
	
	public ArrayList<Task> getTaskList() {
		return tasks;
	}
	
	public String toString() {
		return "(" + String.valueOf(getDay())+ ")"+ String.valueOf(getYear()) +"/" + String.valueOf(getMonth()) +"/"+ String.valueOf(getDate());
	}
	
	public int getDateEncode() {
		return dateEncode;
	}
}
