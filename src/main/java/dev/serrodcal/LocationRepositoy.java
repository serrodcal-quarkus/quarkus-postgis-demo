package dev.serrodcal;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LocationRepositoy implements PanacheRepository<LocationEntity> { }
