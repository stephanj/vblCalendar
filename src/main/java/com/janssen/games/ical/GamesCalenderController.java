/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
*/
package com.janssen.games.ical;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.io.TimezoneAssignment;
import biweekly.util.Duration;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.ejb.Stateless;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * @author Stephan Janssen
 */
@Stateless
public class GamesCalenderController {

    private static final String VBL_URL =
            "http://vblcb.wisseq.eu/VBLCB_WebService/data/TeamMatchesByGuid?teamGuid=";


    // TimeZone.getTimeZone("Europe/Brussels"),
    private TimezoneAssignment brussels = TimezoneAssignment.download(
            TimeZone.getTimeZone("Europe/Brussels"),
            true
    );

    public String createCalendar(final String teamId) throws IOException, ParseException {

        final ICalendar calendar = new ICalendar();
        calendar.getTimezoneInfo().setDefaultTimezone(brussels);

        List<Game> games = getGames(teamId);

        if (games != null) {
            for (Game game : games) {
                createEvent(calendar, game);
            }

            return Biweekly.write(calendar).go();
        }

        throw new IllegalArgumentException("No calendar info found");
    }

    /**
     *
     * @param teamId
     * @return
     * @throws IOException
     * @throws ParseException
     */
    private List<Game> getGames(final String teamId) throws IOException, ParseException {

        List<Game> games = new ArrayList<>();

        final Document doc = Jsoup.connect(VBL_URL + teamId)
                .timeout(5000)
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .execute()
                .parse();

        final JsonElement parse = new JsonParser().parse(doc.text());
        final JsonArray asJsonArray = parse.getAsJsonArray();
        final long now = new Date().getTime();

        for (int i = 0; i < asJsonArray.size(); i++) {
            final JsonObject game = asJsonArray.get(i).getAsJsonObject();

            final long gameDate = game.get("jsDTCode").getAsLong();
            if (gameDate < now) {
                continue;
            }

            final String beginTijd = game.get("beginTijd").getAsString();
            if (beginTijd.trim().length() == 0) {
                continue;
            }

            final String wedID = game.get("wedID").getAsString();
            final String thuisNaam = game.get("tTNaam").getAsString();
            final String uitNaam = game.get("tUNaam").getAsString();
            final String accomodatie = game.get("accNaam").getAsString();
            final String pouleNaam = game.get("pouleNaam").getAsString();

            games.add(new Game(wedID, thuisNaam, uitNaam, gameDate, accomodatie, pouleNaam));
        }

        return games;
    }

    /**
     *
     * @param calendar
     * @param game
     */
    private void createEvent(final ICalendar calendar, final Game game) {

        VEvent event = new VEvent();

        event.setSummary(game.getHomeTeam() + " - " + game.getUitTeam());

        event.setDateStart(game.getFromDate(), true);
        event.setLocation(game.getAccomodation());
        event.setDescription(game.getPouleNaam());
        event.addCategories("VBL");
        Duration duration = new Duration.Builder().hours(2).build();
        event.setDuration(duration);

        calendar.addEvent(event);
    }
}