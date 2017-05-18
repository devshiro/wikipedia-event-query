package wikipedia.event.model;

import java.time.LocalDate;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import lombok.Data;

/**
 * Class representing an event that occurred on a specific date.
 */
@Data
public class Event {

	/**
	 * Date of the event.
	 */
	private LocalDate date;

	/**
	 * Description of the event.
	 */
	private String description;

	/**
	 * Constructor for creating an {@code Event} object.
	 *
	 * @param date the date of the event
	 * @param description the description of the event
	 */
	public Event(LocalDate date, String description) {
		this.date = date;
		this.description = description;
	}

	/**
	 * Returns the string representation of this event.
	 *
	 * @return the string representation of this event in the form
	 *         <span><em>date</em><code> - </code><em>description</em></span>
	 */
	public String toString() {
		return String.format("%s - %s", date.toString(), description);
	}

}
