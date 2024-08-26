package dev.serrodcal;

import io.quarkus.logging.Log;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.geolatte.geom.Circle;
import org.jboss.resteasy.reactive.ResponseStatus;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        Point point = geometryFactory.createPoint(new Coordinate(newLocationRequest.lon(), newLocationRequest.lat()));

        LocationEntity locationEntity = new LocationEntity();
        locationEntity.name = newLocationRequest.name();
        locationEntity.point = point;

        this.locationRepositoy.persist(locationEntity);
    }

    @GET
    @SessionScoped
    public List<LocationResponse> all() {
        Log.info("Get all");
        return this.locationRepositoy.listAll().stream()
                .map(i -> new LocationResponse(i.id, i.name, i.point.getX(), i.point.getY(), i.createdAt, i.updatedAt))
                .toList();
    }

    @GET
    @Path("/radius")
    @SessionScoped
    public RadiusResponse radius(
            @QueryParam("latitude") Double latitude,
            @QueryParam("longitud") Double longitud,
            @QueryParam("radius") Double radius
    ) {
        Log.info(latitude + ", " + longitud + ", " + radius);
        if (Objects.isNull(latitude) || Objects.isNull(longitud) || Objects.isNull(radius))
            throw new BadRequestException("Latitude, longitud and radius are mandatory as query param");

        Parameters params = Parameters.with("window", getWindow(latitude, longitud, radius));

        //'POLYGON ((5 5, 5 10, 10 10, 10 5, 5 5))'
        List<LocationEntity> locationEntities = this.locationRepositoy
                .find("select l from location l where within(l.point, :window) = true", params)
                .stream()
                .toList();

        List<LocationResponse> locationResponses = locationEntities.stream()
                .map(i -> new LocationResponse(i.id, i.name, i.point.getX(), i.point.getY(), i.createdAt, i.updatedAt))
                .toList();

        return new RadiusResponse(locationResponses);


    }

    private Polygon getWindow(Double latitude, Double longitud, Double radius) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate coordinate = new Coordinate(longitud, latitude);
        Coordinate[] coordinates = createPolygonByRadius(coordinate, radius.doubleValue());
        Polygon window = geometryFactory.createPolygon(coordinates);
        return window;
    }

    private Coordinate[] createPolygonByRadius(Coordinate coordinate, double radius) {
        double lon = coordinate.getX();
        double lat = coordinate.getY();

        var coordinates = new Coordinate[]{new Coordinate(lon - radius, lat - radius),
                new Coordinate(lon - radius, lat + radius), new Coordinate(lon + radius, lat + radius),
                new Coordinate(lon + radius, lat - radius), new Coordinate(lon - radius, lat - radius)};

        return coordinates;
    }


}
