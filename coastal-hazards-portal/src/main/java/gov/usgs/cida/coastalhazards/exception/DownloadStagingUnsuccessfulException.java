package gov.usgs.cida.coastalhazards.exception;

/**
 *
 * @author Jordan Walker <jiwalker@usgs.gov>
 */
public class DownloadStagingUnsuccessfulException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    @Override
    public String getMessage() {
        return "Unable to stage download from remote source";
    }
    
}
