package gov.usgs.cida.coastalhazards.rest;

import com.sun.jersey.api.core.PackagesResourceConfig;
import javax.ws.rs.ApplicationPath;

/**
 * TODO come up with good rest path
 * @author Jordan Walker <jiwalker@usgs.gov>
 */
@ApplicationPath("rest")
public class RestApplication extends PackagesResourceConfig {
     public RestApplication() {
         super("gov.usgs.cida.coastalhazards.rest");
     }    
}