package dev.serrodcal;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.ResponseStatus;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.util.List;

@Path("/v1/locations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LocationResource {

    @Inject
    LocationRepositoy locationRepositoy;

    @POST
    @ResponseStatus(201)
    @Transactional
    public void newPoint(@Valid NewLocationRequest newLocationRequest) {
        Log.info(newLocationRequest.toString());

        GeometryFactory geometryFactory = new GeometryFactory();
        Point point = geometryFactory.createPoint(new Coordinate(newLocationRequest.lat(), newLocationRequest.lon()));

        LocationEntity locationEntity = new LocationEntity();
        locationEntity.point = point;

        this.locationRepositoy.persist(locationEntity);
    }

    @GET
    @SessionScoped
    public List<LocationResponse> all() {
        return this.locationRepositoy.listAll().stream()
                .map(i -> new LocationResponse(i.id, i.point.getX(), i.point.getY(), i.createdAt, i.updatedAt))
                .toList();
    }

}
