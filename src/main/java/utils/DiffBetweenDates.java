package utils;

/**
 *
 * @author MBellaj
 */
public class DiffBetweenDates {

	private int years;

	private int months;

	private int days;

	private int totalDays;

	public DiffBetweenDates() {
	}

	public DiffBetweenDates(int _iYears, int _iMonths, int _iDays, int _iTotalDays) {
		this.years = _iYears;
		this.months = _iMonths;
		this.days = _iDays;
		this.totalDays = _iTotalDays;
	}

	public int getYears() {
		return years;
	}

	public void setYears(int _iYears) {
		this.years = _iYears;
	}

	public int getMonths() {
		return months;
	}

	public void setMonths(int _iMonths) {
		this.months = _iMonths;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int _iDays) {
		this.days = _iDays;
	}

	public int getTotalDays() {
		return totalDays;
	}

	public void setTotalDays(int _iTotalDays) {
		this.totalDays = _iTotalDays;
	}

	@Override
	public String toString() {
		return "DiffBetweenDates{" + "years=" + years + ", months=" + months + ", days=" + days + ", totalDays="
				+ totalDays + "}";
	}

}
