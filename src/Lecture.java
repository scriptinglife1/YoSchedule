import com.gargoylesoftware.htmlunit.html.DomElement;

import java.time.DayOfWeek;
import java.time.LocalTime;


public class Lecture {
    private String title;
    private String location;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    public Lecture(String title, String location, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime){
        this.title = title;
        this.location = location;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static Lecture fromHtml(DomElement element){
        DayOfWeek dayOfWeek = Utils.getDay(element.getParentNode().asText().split("\n")[0].trim());
        DomElement infoRow = element.getElementsByTagName("table").get(0).getElementsByTagName("tr").get(0);
        String title = infoRow.getElementsByTagName("td").get(0).getTextContent();
        LocalTime startTime = LocalTime.parse(infoRow.getElementsByTagName("td").get(1).getTextContent());
        LocalTime endTime = LocalTime.parse(infoRow.getElementsByTagName("td").get(2).getTextContent());
        DomElement infoRow2 = element.getElementsByTagName("table").get(1).getElementsByTagName("tr").get(0);
        String building = infoRow2.getElementsByTagName("td").get(1).getTextContent();
        String room = infoRow2.getElementsByTagName("td").get(2).getTextContent();
        return new Lecture(title, building + " - " + room, dayOfWeek, startTime, endTime);
    }
    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    @Override
    public String toString(){
        return ("Title: " + title + ". Location: " + location + ". Day of the week: " + dayOfWeek + ". Start time: "
                + startTime + ". End time: " + endTime);
    }
}
