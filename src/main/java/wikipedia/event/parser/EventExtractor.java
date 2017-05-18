package wikipedia.event.parser;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import java.time.MonthDay;
import java.time.Year;

import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.jsoup.select.Elements;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wikipedia.event.model.Event;

/**
 * Class for querying events that occurred on a specific day of month. Events
 * are extracted from the English Wikipedia page about a specific day of month.
 */
public class EventExtractor {

	private static Logger logger = LoggerFactory.getLogger(EventExtractor.class);

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[G' ']y[' 'G]").withLocale(Locale.ENGLISH);

	/**
	 * Default value of the timeout for accessing Wikipedia pages.
	 */
	private static final int TIMEOUT = 5000;

	/**
	 * The timeout for accessing Wikipedia pages in milliseconds.
	 */
	private int timeout = TIMEOUT;

	/**
	 * Constructs a {@code EventExtractor} object with default timeout.
	 *
	 * @see #getTimeout()
	 * @see #setTimeout(int)
	 */
	public EventExtractor() {
	}

	/**
	 * Constructs a {@code EventExtractor} object with the timeout specified.
	 *
	 * @param timeout the timeout for accessing Wikipedia pages in milliseconds
	 *
	 * @see #getTimeout()
	 * @see #setTimeout(int)
	 */
	public EventExtractor(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * Returns the timeout for accessing Wikipedia pages.
	 *
	 * @return the timeout for accessing Wikipedia pages in milliseconds
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * Sets the timeout for accessing Wikipedia pages.
	 *
	 * @param timeout the timeout for accessing Wikipedia pages in milliseconds
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * Returns the web page address of the Wikipedia page about the day of
	 * month specified.
	 *
	 * @param monthDay an object that wraps the month of year and the day of
	 *                 month
	 * @return the web page address of the Wikipedia page about the day of
	 *         month specified, as a string
	 */
	private String getWikipediaURL(MonthDay monthDay) {
		final String	month = monthDay.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
		return String.format("http://en.wikipedia.org/wiki/%s_%d", month, monthDay.getDayOfMonth());
	}

	/**
	 * Returns events that occurred on the day of month specified.
	 *
	 * @param monthDay an object that wraps the month of year and the day of
	 *                 month
	 * @return the list of objects that represent events that occurred on the
	 *         day of month specified
	 * @throws IOException if any I/O error occurs
	 */
	public List<Event> getEvents(MonthDay monthDay) throws IOException {
		String	url = getWikipediaURL(monthDay);
		logger.info("Retrieving web page from {}", url);
		Document	doc = Jsoup.connect(url).timeout(timeout).get();
		List<Event>	events = new ArrayList<Event>();
		Elements	elements = doc.select("h2:has(#Events) + ul > li");
		logger.info("Found {} event(s)", elements.size());
		for (Element element: elements) {
			logger.debug("Text to be parsed: {}", element.text());
			String[]	parts = element.text().split(" \u2013 ", 2);
			if (parts.length != 2) {
				logger.warn("Skipping a malformed event");
				continue;
			}
			parts[0] = parts[0].trim();
			parts[1] = parts[1].trim();
			int	year = Year.parse(parts[0], formatter).getValue();
			Event	event = new Event(monthDay.atYear(year), parts[1]);
			logger.debug("Event created: {}", event);
			events.add(event);
		}
		return events;
	}

	/**
	 * Returns events that occurred on the day of month specified.
	 *
	 * @param monthOfYear the month of the year (1&ndash;12)
	 * @param dayOfMonth the day of the month (1&ndash;31)
	 * @return the list of objects that represent events that occurred on the
	 *         day of month specified
	 * @throws IOException if any I/O error occurs
	 */
	public List<Event> getEvents(int monthOfYear, int dayOfMonth) throws IOException {
		return getEvents(MonthDay.of(monthOfYear, dayOfMonth));
	}

}
