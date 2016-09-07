package com.janssen.games.ical;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Date;

/**
 * @author Stephan Janssen
 */
public class Game {

    private String id;
    private String homeTeam;
    private String uitTeam;
    private long fromDate;
    private String accomodation;
    private String pouleNaam;

    public Game(final String id,
                final String homeTeam,
                final String uitTeam,
                final long fromDate,
                final String accomodation,
                final String pouleNaam) {
        this.id = id;
        this.homeTeam = homeTeam;
        this.uitTeam = uitTeam;
        this.fromDate = fromDate;
        this.accomodation = accomodation;
        this.pouleNaam = pouleNaam;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getUitTeam() {
        return uitTeam;
    }

    public Date getFromDate() {
         return new DateTime(fromDate, DateTimeZone.forOffsetHours(0)).minusHours(2).toDate();
    }

    public String getAccomodation() {
        return accomodation;
    }

    public void setAccomodation(final String accomodation) {
        this.accomodation = accomodation;
    }

    public String getPouleNaam() {
        return pouleNaam;
    }

    public String toString() {
        return homeTeam + " - " + uitTeam;
    }
}
