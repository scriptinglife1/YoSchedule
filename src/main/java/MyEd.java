package main.java;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.function.Predicate;

public class MyEd {


    public static void main(String[] args) throws Exception {
        final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_38);
        webClient.getOptions().setJavaScriptEnabled(false);
        final HtmlPage page = webClient.getPage("https://www.ease.ed.ac.uk/cosign.cgi?cosign-eucsCosign-www.myed.ed.ac.uk&https://www.myed.ed.ac.uk/uPortal/");

        final String pageAsXml = page.asXml();

        final String pageAsText = page.asText();
        HtmlForm form = (HtmlForm)getChildrenWhere(page.getElementsByTagName("body").get(0),
                (DomElement ele) -> ele.getAttribute("action").equals("/cosign.cgi"),"form").get(0);
        form.getInputByName("login").setValueAttribute("s1457539");
        form.getInputByName("password").setValueAttribute(args[0]);
        System.out.println(form.getInputByName("submit").click().getWebResponse().getContentAsString());
        final HtmlPage page2 = webClient.getPage("https://www.ted.is.ed.ac.uk/UOE1516_SWS/mytimetablestudent.asp");
        System.out.println(page2.asText());
        System.out.println(page2.getWebResponse().getContentAsString());
        HtmlPage page3 = ((HtmlForm)(page2.getElementsByTagName("form").get(0))).getInputByValue("Continue").click();
        HtmlPage page4 = page3.getFormByName("swsform").getInputByName("bGetTimetable").click();
        System.out.println(page4.asText());
        LinkedList<Lecture> lectures = getLectures(page4);
        for(Lecture lecture:lectures){
            System.out.println(lecture);
        }
        CalendarQuickstart cal = new CalendarQuickstart();
        cal.addLectureUsingBatch(lectures);
    }
    private static LinkedList<Lecture> getLectures(HtmlPage timetable) throws IOException {
        LinkedList<Lecture> lectures = new LinkedList<>();
        DomElement tempElement = timetable.getElementsByTagName("body").get(0);
        ArrayList<DomElement> elements = getChildrenWhere(tempElement,
                (DomElement ele) -> ele.getAttribute("class").equals("object-cell-border"),"td");
        for (DomElement ele: elements){
            lectures.add(Lecture.fromHtml(ele));
        }
        return lectures;
    }

    /**
     * Gets all the children of a {@link com.gargoylesoftware.htmlunit.html.DomElement} with the specified tag
     * name which are accepted by the predicate.
     *
     * @param element The DomElement
     * @param predicate The Predicate
     * @param tagName The tag name
     * @return An {@link java.util.ArrayList} containing the
     * accepted {@link DomElement}s
     */
    private static ArrayList<DomElement> getChildrenWhere(DomElement element, Predicate<DomElement> predicate,
                                                          String tagName){
        ArrayList<DomElement> elements = new ArrayList<DomElement>();
        for (DomElement ele : element.getElementsByTagName(tagName)){
            System.out.println(ele.getTagName());
            if (predicate.test(ele)){
                elements.add(ele);
            }
        }
        return elements;

    }
}