/**
 * @author Khanh Nguyen
 * @author Duc Tan Tran
 * @author Paloma Ortiz
 * @author Braa Oudeh
 * filename Calendar.java
 * 
 * This file implements the Calendar model for Project Planner. A calendar consist of an array of years
 * 		This files also implements the year , week and month class.
 */
import java.util.ArrayList;
import java.util.HashMap;

public class Calendar {
	private HashMap<Integer, Integer> MONTHDAYS;
	private ArrayList<Year> years;
	private int creationYear;

	/**
	 * Class Year
	 * 
	 * Represent a year with 12 Months, Day objects are linked together across months
	 */
	class Year {
		private int year;
		private ArrayList<Month> months;
		private ArrayList<Week> weeks;

		/**
		 * Year() -- constructor of the year class.
		 * @param year - the year
		 */
		public Year(int year) {
			this.year = year;
			this.months = new ArrayList<Month>();
			this.weeks = new ArrayList<Week>();
			// populate year with 12 months
			for (int i = 1; i <= 12; i++) {
				months.add(new Month(i, year));
			}
			// set .nextDay pointer for consecutive days between months
			for (int i = 0; i < 11; i++) {
				Day lastDay = months.get(i).getLastDay();
				Day FirstDayNextMonth = months.get(i + 1).getDay(1);
				lastDay.setNextDay(FirstDayNextMonth);
				FirstDayNextMonth.setPreviousDay(lastDay);
			}
			
			// populate the weeks
			Day dayPointer = months.get(0).getDay(1);
			while (dayPointer!=null) {
				Week newWeek = new Week();
				weeks.add(newWeek);
				dayPointer = newWeek.populate(dayPointer);
				if (dayPointer!=null) {
					dayPointer = dayPointer.getNextDay();
				}
			}

		}

		/**
		 * various getters
		 */
		public Month getMonth(int month) {
			return months.get(month - 1);
		}
		public int getYear() {
			return year;
		}
		public ArrayList<Week> getWeeks(){
			return weeks;
		}

		/**
		 * printWeek() -- print the week out
		 */
		public void printWeek() {
			for (Week week:weeks) {
				System.out.println(week.toString());
			}
		}
	}

	/**
	 * Class Month
	 * 
	 * This class represents a Month in a year
	 * Contain a list of Day objects and handles day creation and linking.
	 */
	class Month {
		private int month;
		private int year;
		private ArrayList<Day> days;

		/**
		 * Month - constructor of this class
		 * @param month - the month
		 * @param year - the year
		 */
		public Month(int month, int year) {
			this.days = new ArrayList<Day>();
			this.month = month;
			this.year = year%100;
			populate();

		}
		
		/**
		 * populate() -- fills the Month with the appropriate Days
		 */
		private void populate() {
			int leapYearAdjustment = 0;
			if (year % 4 == 0 && month == 2) {
				leapYearAdjustment = 1;
			}
			int daysCount = MONTHDAYS.get(month);
			daysCount += leapYearAdjustment;
			int firstDateEncode = year * 10000 + month * 100;
			for (int i = 1; i <= daysCount; i++) {
				Day newDay = new Day(firstDateEncode + i);
//				newDay.setCurMonth(this);
				if (!days.isEmpty()) {
					Day lastDay = days.getLast();
					lastDay.setNextDay(newDay);
					newDay.setPreviousDay(lastDay);
				}
				days.add(newDay);
			}
		}

		/**
		 * Various getters
		 */
		public Day getDay(int date) {
			return days.get((date-1) % 100);
		}

		public Day getLastDay() {
			return days.getLast();
		}

		public ArrayList<Day> getDayList() {
			return days;
		}
	}

	/**
	 * Class Week
	 * This class represents a week in a year. If a day of the week falls outside of the year, it is represented as Null
	 */
	class Week {
		private ArrayList<Day> days;
		private Week nextWeek;

		/**
		 * Week() -- constructor of this class
		 */
		public Week() {
			this.days = new ArrayList<Day>();
		}

		/**
		 * populate() -- populate the week starting from startDate
		 * @param startDate - the first day in the week
		 * @return the last Day this week
		 */
		public Day populate(Day startDate) {
			if(startDate == null) {
				return null;
			}
			int startDay = startDate.getDay();
			for (int i = 0; i < startDay; i++) {
				days.add(null);
			}
			Day dayPointer = startDate;
			while (days.size() < 7) {
				if (dayPointer != null) {
					days.add(dayPointer);
//					dayPointer.setCurWeek(this);
					dayPointer = dayPointer.getNextDay();
				} else {
					days.add(null);
				}
			}
			return days.getLast();
		}

		// getter method
		public Week getNextWeek() {
			return nextWeek;
		}

		/**
		 * toString() -- return a string representation
		 * @return
		 */
		public String toString() {
			String ret ="";
			for (Day day: days) {
				if(day == null) {
					ret+= "NULL->";
				}else {
				ret+=day.toString() + "->";
				}
			}
			return ret;
		}
	}

	/**
	 * Calendar() -- constructor for calendar class
	 * @param year - current year
	 */
	public Calendar(int year) {
		this.MONTHDAYS = new HashMap<Integer, Integer>();
		this.years=new ArrayList<Year>();
		MONTHDAYS.put(1, 31);
		MONTHDAYS.put(2, 28);
		MONTHDAYS.put(3, 31);
		MONTHDAYS.put(4, 30);
		MONTHDAYS.put(5, 31);
		MONTHDAYS.put(6, 30);
		MONTHDAYS.put(7, 31);
		MONTHDAYS.put(8, 31);
		MONTHDAYS.put(9, 30);
		MONTHDAYS.put(10, 31);
		MONTHDAYS.put(11, 30);
		MONTHDAYS.put(12, 31);
		
		this.creationYear = year;
		this.years.add(new Year(creationYear));
	}

	/**
	 * printWeeks() -- print out all the weeks
	 * @param index -- the year
	 */
	public void printWeeks(int index) {
		years.get(index).printWeek();
	}

	// getter method
	public int getMonthLength(int month){
		return MONTHDAYS.get(month);
	}

	/**
	 * generateNextYear() -- add a new year to the calendar
	 */
	public void generateNextYear() {
		int newYear = years.getLast().getYear()+1;
		Day lastDay = years.getLast().getMonth(12).getLastDay();
		this.years.add(new Year(newYear));
		Day newFirstDay = getFirstDateOf(newYear);
		lastDay.setNextDay(newFirstDay);
		newFirstDay.setPreviousDay(lastDay);
	}

	/**
	 * search for a day in the calendar
	 * if not exists, return 
	 * @param dateEncode - the integer representation of a date, format YYMMDD
	 * @return the pointer to the Day object if found, null if not found
	 */
	public Day getDate(int dateEncode) {
		int month = (dateEncode/100)%100;
		int date = dateEncode%100;
		int year = dateEncode/10000;
		for (Year curYear: years) {
			if (curYear.getYear() == year) {
				Month curMonth = curYear.getMonth(month);
				return curMonth.getDay(date);
			}
		}
		return null;
	}

	/**
	 * getFirstDate() - get the first day
	 * @return Day object
	 */
	public Day getFirstDate() {
		return years.get(0).getMonth(1).getDay(1);
	}

	/**
	 * getFirstDateOf() -- look at the year and get the first day of that year
	 * @param year - the year
	 * @return first Day obj of that year
	 */
	public Day getFirstDateOf(int year) {
		for (Year curYear: years) {
			if (curYear.getYear() == year) {
				return curYear.getMonth(1).getDay(1);
			}
		}
		return null;
	}

	/**
	 * addTask() -- add a task to a date
	 * @param dayEncode day format YYMMDD
	 * @param timeStart task time start
	 * @param timeEnd task time end
	 * @param title task title
	 * @param description task description
	 */
	public void addTask(int dayEncode,int timeStart, int timeEnd, String title, String description) {
		getDate(dayEncode).addTask(timeStart, timeEnd, title, description);
	}

	/**
	 * printYears() -- print out the years
	 */
	public void printYears() {
		for (Year year: years) {
			System.out.println(year.getYear());
		}
	}

}
