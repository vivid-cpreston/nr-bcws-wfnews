package ca.bc.gov.nrs.wfnews.service.api.v1;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import ca.bc.gov.nrs.common.persistence.dao.DaoException;
import ca.bc.gov.nrs.wfnews.api.model.v1.PublishedIncident;
import ca.bc.gov.nrs.wfnews.api.rest.v1.resource.AttachmentListResource;
import ca.bc.gov.nrs.wfnews.api.rest.v1.resource.AttachmentResource;
import ca.bc.gov.nrs.wfnews.api.rest.v1.resource.ExternalUriListResource;
import ca.bc.gov.nrs.wfnews.api.rest.v1.resource.ExternalUriResource;
import ca.bc.gov.nrs.wfnews.api.rest.v1.resource.PublishedIncidentListResource;
import ca.bc.gov.nrs.wfnews.api.rest.v1.resource.PublishedIncidentResource;
import ca.bc.gov.nrs.common.service.ConflictException;
import ca.bc.gov.nrs.common.service.NotFoundException;
import ca.bc.gov.nrs.common.service.ValidationFailureException;
import ca.bc.gov.nrs.wfone.common.service.api.model.factory.FactoryContext;
import ca.bc.gov.nrs.wfone.common.webade.authentication.WebAdeAuthentication;

public interface IncidentsService {
	@Transactional(readOnly = false, rollbackFor=Exception.class)
	PublishedIncidentResource createPublishedWildfireIncident(PublishedIncident publishedIncident, FactoryContext factoryContext) throws ValidationFailureException, ConflictException, NotFoundException, Exception;

	@Transactional(readOnly = false, rollbackFor=Exception.class)
	PublishedIncidentResource updatePublishedWildfireIncident(PublishedIncident publishedIncident, FactoryContext factoryContext) throws ValidationFailureException, ConflictException, NotFoundException, Exception;
	
	@Transactional(readOnly = true, rollbackFor=Exception.class)
	PublishedIncidentResource getPublishedIncidentByIncidentGuid(String incidentGuid, WebAdeAuthentication webAdeAuthentication, FactoryContext factoryContext) throws DaoException, NotFoundException;
	
	@Transactional(readOnly = true, rollbackFor=Exception.class)
	PublishedIncidentResource getPublishedIncident(String publishedIncidentDetailGuid, WebAdeAuthentication webAdeAuthentication, FactoryContext factoryContext) throws DaoException, NotFoundException;

	@Transactional(readOnly = true, rollbackFor=Exception.class)
	String getPublishedIncidentsAsJson(String stageOfControl, String bbox, WebAdeAuthentication webAdeAuthentication, FactoryContext factoryContext) throws DaoException;

	@Transactional(readOnly = true, rollbackFor=Exception.class)
	String getFireOfNoteAsJson(String bbox, WebAdeAuthentication webAdeAuthentication, FactoryContext factoryContext) throws DaoException;
	
	@Transactional(readOnly = false, rollbackFor=Exception.class)
	void deletePublishedIncident(String publishedIncidentDetailGuid, FactoryContext factoryContext) throws NotFoundException, ConflictException;

	@Transactional(readOnly = false, rollbackFor=Exception.class)
	void flush(FactoryContext factoryContext) throws NotFoundException, ConflictException;

	@Transactional(readOnly = true, rollbackFor=Exception.class)
	PublishedIncidentListResource getPublishedIncidentList(String searchText, Integer pageNumber, Integer pageRowCount, String orderBy, Boolean fireOfNote, List<String> stageOfControlList, Boolean newfires, String fireCentre, String bbox, Double latitude, Double longitude, Double radius, FactoryContext factoryContext);

	@Transactional(readOnly = false, rollbackFor=Exception.class)
	ExternalUriResource createExternalUri(ExternalUriResource externalUri, FactoryContext factoryContext) throws ValidationFailureException, ConflictException, NotFoundException, Exception;
	
	@Transactional(readOnly = false, rollbackFor=Exception.class)
	ExternalUriResource updateExternalUri(ExternalUriResource externalUri, FactoryContext factoryContext) throws ValidationFailureException, ConflictException, NotFoundException, Exception;
	
	@Transactional(readOnly = false, rollbackFor=Exception.class)
	void deleteExternalUri(String ExternalUriDetailGuid, FactoryContext factoryContext) throws NotFoundException, ConflictException;
	
	@Transactional(readOnly = true, rollbackFor=Exception.class)
	ExternalUriResource getExternalUri(String externalUriGuid, WebAdeAuthentication webAdeAuthentication, FactoryContext factoryContext) throws DaoException, NotFoundException;
	
	@Transactional(readOnly = true, rollbackFor=Exception.class)
	ExternalUriListResource getExternalUriList(String sourceObjectUniqueId, Integer pageNumber, 
			Integer pageRowCount, FactoryContext factoryContext);
  
	@Transactional(readOnly = true, rollbackFor=Exception.class)
	AttachmentListResource getIncidentAttachmentList(String incidentNumberSequence, boolean primaryIndicator, String[] sourceObjectNameCodes, String[] attachmentTypeCodes, Integer pageNumber, Integer pageRowCount, String[] orderBy, FactoryContext factoryContext);

	@Transactional(readOnly = false, rollbackFor=Exception.class)
	AttachmentResource createIncidentAttachment(AttachmentResource attachment, WebAdeAuthentication webAdeAuthentication, FactoryContext factoryContext) throws ValidationFailureException, ConflictException, NotFoundException, Exception;

	@Transactional(readOnly = false, rollbackFor=Exception.class)
	AttachmentResource updateIncidentAttachment(AttachmentResource attachment, WebAdeAuthentication webAdeAuthentication, FactoryContext factoryContext) throws ValidationFailureException, ConflictException, NotFoundException, Exception;

	@Transactional(readOnly = false, rollbackFor=Exception.class)
	AttachmentResource deleteIncidentAttachment(String attachmentGuid, WebAdeAuthentication webAdeAuthentication, FactoryContext factoryContext) throws ValidationFailureException, ConflictException, NotFoundException, Exception;

	@Transactional(readOnly = false, rollbackFor=Exception.class)
	AttachmentResource getIncidentAttachment(String attachmentGuid, FactoryContext factoryContext) throws ValidationFailureException, ConflictException, NotFoundException, Exception;

	
}
