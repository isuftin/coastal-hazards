package gov.usgs.cida.coastalhazards.rest.data;

import com.google.gson.Gson;
import gov.usgs.cida.coastalhazards.gson.GsonUtil;
import gov.usgs.cida.coastalhazards.jpa.DataDomainManager;
import gov.usgs.cida.coastalhazards.jpa.ItemManager;
import gov.usgs.cida.coastalhazards.model.Item;
import gov.usgs.cida.coastalhazards.model.util.DataDomain;
import gov.usgs.cida.coastalhazards.rest.security.CoastalHazardsTokenBasedSecurityFilter;
import gov.usgs.cida.utilities.HTTPCachingUtil;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

/**
 * This is tied closely to the ItemResource, it should be wiped when an item is updated.
 * Locking is handled by the DataDomainManager so that requests for the same item
 * will lock for gathering the domain.
 * 
 * @author Jordan Walker <jiwalker@usgs.gov>
 */
@Path(DataURI.DOMAIN_PATH)
@PermitAll //says that all methods, unless otherwise secured, will be allowed by default
public class DataDomainResource {
    
    @GET
    @Path("/item/{id}")
    public Response getDataDomain(@PathParam("id") String id, @Context Request request) {
        Response response = null;
        try (ItemManager itemManager = new ItemManager(); DataDomainManager domainManager = new DataDomainManager()) {
            Item item = itemManager.load(id);
            if (item == null || item.getType() != Item.Type.historical) {
                throw new NotFoundException("Only historical is supported at this time");
            }
            DataDomain domain = domainManager.getDomainForItem(item);
            Response checkModified = HTTPCachingUtil.checkModified(request, domain);
            if (checkModified != null) {
                response = checkModified;
            } else {
                Gson serializer = GsonUtil.getDefault();
                String domainJson = serializer.toJson(domain);
                response = Response.ok(domainJson, MediaType.APPLICATION_JSON_TYPE).lastModified(domain.getLastModified()).build();
            }
        }
        return response;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({CoastalHazardsTokenBasedSecurityFilter.CCH_ADMIN_ROLE})
    @Path("/regenall")
    public Response regenerateAllDataDomains() {
        Response response = null;
        try (ItemManager itemManager = new ItemManager(); DataDomainManager domainManager = new DataDomainManager()) {
	    List<Item> rootItems = itemManager.loadRootItems();
	    
	    if(rootItems.size() > 0){
		List<String> generatedIds =  new ArrayList<>();
		
		for(Item item : rootItems){
		    if(item.getItemType() == Item.ItemType.uber)
		    {
			generatedIds.addAll(domainManager.regenerateAllDomains(item));
		    }
		}
		Gson gson = GsonUtil.getDefault();
		String json = gson.toJson(generatedIds);
		response = Response.ok(json).build();
	    } else {
		throw new NotFoundException("Root Item could not be idenfitied");
	    }
        } catch (Exception e) {
	   response =  Response.status(500).build();
	}
        return response;
    }
}
