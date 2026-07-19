/**
 * @author Khanh Nguyen
 * @author Duc Tan Tran
 * @author Paloma Ortiz
 * @author Braa Oudeh
 * filename Task.java
 * 
 * This file implement class Task, a Day object contains all the tasks for that day
 */

/**
 * class Task
 * 
 * This class represent a single task
 * 
 */
public class Task implements Comparable<Task> {

	private int timeStart;
    private int timeEnd;
	private String description;
	private String title;

	/**
	 * Constructor
	 * 
	 * @param date        - the date of the task, format YYMMDD
	 * @param timeStart   - the Start time of the task
     * @param timeEnd     - The End time of the task
	 * @param description - the description
	 */
	public Task(int timeStart, int timeEnd, String title) {
		this.timeStart = timeStart;
        this.timeEnd = timeEnd;
		this.title = title;
		this.description = "[place holder]";
	}

	/**
	 * Constructor
	 * 
	 * @param date        the date of the task, format YYMMDD
	 * @param timeStart   - the Start time of the task
     * @param timeEnd     - The End time of the task	 
     * @param description
	 */
	public Task(int timeStart, int timeEnd, String title, String description) {
		this.timeStart = timeStart;
        this.timeEnd = timeEnd;
		this.title = title;
		this.description = description;
	}

	/**
	 * Various getters and setters
	 * 
	 * @return
	 */
	public int getTimeStart() {
		return timeStart;
	}

	public int getTimeEnd() {
		return timeEnd;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setTime(int timeStart, int timeEnd) {
		this.timeStart = timeStart;
        this.timeEnd = timeEnd;
	}


	/**
	 * compareTo(Task o) compare the time this Task against the Task object
	 * specified by the parameter
	 * 
	 * @param Task o - the Task to compare to
	 * @return >0 for greater, <0 for less
	 */
	@Override
	public int compareTo(Task o) {
		return (this.getTimeStart() - o.getTimeStart());
	}

}
