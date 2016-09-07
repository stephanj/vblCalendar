package com.janssen.games.ical;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

/**
 * @author Stephan Janssen
 */
@Path("/api/ical")
public class GameCalenderService {

    @EJB
    private GamesCalenderController controller;

    @GET
    @Path("/team/{teamId}")
    @Produces("text/calendar")
    public Response allGames(@PathParam("teamId") String teamId) {

        try {
            final String calendar = controller.createCalendar(teamId);

            return Response.ok(calendar, "text/calendar")
                    .header("Content-Disposition", "attachment; filename=vblgames"+ new Date().getTime() +".ics" ).build();

        } catch (IOException | ParseException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
