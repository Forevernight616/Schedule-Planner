/**
 * @author Khanh Nguyen
 * @author Duc Tan Tran
 * @author Paloma Ortiz
 * @author Braa Oudeh
 * Purpose: implements a user. A user has the name, the password, and a file that holds the user's data
 */
public class User {
	String username;
	String password;
	Calendar calendar;

	/**
	 * User - constructor of this class
	 * @param username the user name
	 * @param password the password
	 */
	public User(String username, String password) {
		this.username = username;
		this.password = password;
		calendar = new Calendar(26);
	}

	/**
	 * checkPassword() -- check the user password
	 * @param guess - user's guess
	 * @return true if its the user's password, false otherwise
	 */
	public boolean checkPassword(String guess) {
		return this.password.equals(guess);
	}

	// various getters
	public String getUsername() {
		return this.username;
	}
	
	public Calendar getCalendar() {
		return this.calendar;
	}

	/**
	 * addTask() -- add the task to the user
	 * @param dayEncode - date format YYMMDD
	 * @param timeStart task time start
	 * @param timeEnd task time end
	 * @param title task title
	 * @param description task description
	 */
	public void addTask(int dayEncode,int timeStart, int timeEnd, String title, String description) {
		calendar.getDate(dayEncode).addTask(timeStart, timeEnd, title, description);
	}
	

}
