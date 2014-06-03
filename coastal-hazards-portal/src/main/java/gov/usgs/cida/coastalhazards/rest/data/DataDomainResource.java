package gov.usgs.cida.coastalhazards.rest.data;

import com.google.gson.Gson;
import com.sun.jersey.api.NotFoundException;
import gov.usgs.cida.coastalhazards.gson.GsonUtil;
import gov.usgs.cida.coastalhazards.jpa.DataDomainManager;
import gov.usgs.cida.coastalhazards.jpa.ItemManager;
import gov.usgs.cida.coastalhazards.model.Item;
import gov.usgs.cida.coastalhazards.model.util.DataDomain;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * This is tied closely to the ItemResource, it should be wiped when an item is updated.
 * Locking is handled by the DataDomainManager so that requests for the same item
 * will lock for gathering the domain.
 * 
 * @author Jordan Walker <jiwalker@usgs.gov>
 */
@Path("domain")
public class DataDomainResource {
    
    @GET
    @Path("item/{id}")
    public Response getDataDomain(@PathParam("id") String id) {
        String domainJson = null;
        try (ItemManager itemManager = new ItemManager(); DataDomainManager domainManager = new DataDomainManager()) {
            Item item = itemManager.load(id);
            if (item == null || item.getType() != Item.Type.historical) {
                throw new NotFoundException("Only historical is supported at this time");
            }
            DataDomain domain = domainManager.getDomainForItem(item);
            Gson serializer = GsonUtil.getDefault();
            domainJson = serializer.toJson(domain);
        }
        return Response.ok(domainJson, MediaType.APPLICATION_JSON_TYPE).build();
    }
}
