package ca.bc.gov.nrs.wfnews.service.api.v1.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import ca.bc.gov.nrs.common.persistence.dao.DaoException;
import ca.bc.gov.nrs.common.persistence.dao.IntegrityConstraintViolatedDaoException;
import ca.bc.gov.nrs.common.persistence.dao.NotFoundDaoException;
import ca.bc.gov.nrs.common.persistence.dao.OptimisticLockingFailureDaoException;
import ca.bc.gov.nrs.common.service.ConflictException;
import ca.bc.gov.nrs.common.service.NotFoundException;
import ca.bc.gov.nrs.common.service.ServiceException;
import ca.bc.gov.nrs.common.service.ValidationFailureException;
import ca.bc.gov.nrs.wfnews.api.model.v1.PublishedIncident;
import ca.bc.gov.nrs.wfnews.api.rest.v1.resource.AttachmentListResource;
import ca.bc.gov.nrs.wfnews.api.rest.v1.resource.AttachmentResource;
import ca.bc.gov.nrs.wfnews.api.rest.v1.resource.ExternalUriListResource;
import ca.bc.gov.nrs.wfnews.api.rest.v1.resource.ExternalUriResource;
import ca.bc.gov.nrs.wfnews.api.rest.v1.resource.IncidentListResource;
import ca.bc.gov.nrs.wfnews.api.rest.v1.resource.IncidentResource;
import ca.bc.gov.nrs.wfnews.api.rest.v1.resource.PublishedIncidentListResource;
import ca.bc.gov.nrs.wfnews.api.rest.v1.resource.PublishedIncidentResource;
import ca.bc.gov.nrs.wfnews.persistence.v1.dao.AttachmentDao;
import ca.bc.gov.nrs.wfnews.persistence.v1.dao.ExternalUriDao;
import ca.bc.gov.nrs.wfnews.persistence.v1.dao.PublishedIncidentDao;
import ca.bc.gov.nrs.wfnews.persistence.v1.dto.AttachmentDto;
import ca.bc.gov.nrs.wfnews.persistence.v1.dto.ExternalUriDto;
import ca.bc.gov.nrs.wfnews.persistence.v1.dto.PagedDtos;
import ca.bc.gov.nrs.wfnews.persistence.v1.dto.PublishedIncidentDto;
import ca.bc.gov.nrs.wfnews.service.api.v1.IncidentsService;
import ca.bc.gov.nrs.wfnews.service.api.v1.model.factory.AttachmentFactory;
import ca.bc.gov.nrs.wfnews.service.api.v1.model.factory.ExternalUriFactory;
import ca.bc.gov.nrs.wfnews.service.api.v1.model.factory.PublishedIncidentFactory;
import ca.bc.gov.nrs.wfnews.service.api.v1.validation.ModelValidator;
import ca.bc.gov.nrs.wfnews.service.api.v1.validation.exception.ValidationException;
import ca.bc.gov.nrs.wfone.common.model.Message;
import ca.bc.gov.nrs.wfone.common.rest.endpoints.BaseEndpointsImpl;
import ca.bc.gov.nrs.wfone.common.service.api.model.factory.FactoryContext;
import ca.bc.gov.nrs.wfone.common.webade.authentication.WebAdeAuthentication;

public class IncidentsServiceImpl extends BaseEndpointsImpl implements IncidentsService {

	private static final Logger logger = LoggerFactory.getLogger(IncidentsServiceImpl.class);

	String topLevelRestURL;

	private OAuth2RestTemplate restTemplate;

	private PublishedIncidentFactory publishedIncidentFactory;
	private ExternalUriFactory externalUriFactory;
	private AttachmentFactory attachmentFactory;

	private PublishedIncidentDao publishedIncidentDao;
	private AttachmentDao attachmentDao;
	private ExternalUriDao externalUriDao;

	@Autowired
	private ModelValidator modelValidator;

	public void setRestTemplate(OAuth2RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void setTopLevelRestURL(String topLevelRestURL) {
		this.topLevelRestURL = topLevelRestURL;
	}

	public void setAgolQueryUrl(String agolQueryUrl) {
		this.agolQueryUrl = agolQueryUrl;
	}

	public void setPublishedIncidentFactory(PublishedIncidentFactory publishedIncidentFactory) {
		this.publishedIncidentFactory = publishedIncidentFactory;
	}

	public void setExternalUriFactory(ExternalUriFactory externalUriFactory) {
		this.externalUriFactory = externalUriFactory;
	}

	public void setPublishedIncidentDao(PublishedIncidentDao publishedIncidentDao) {
		this.publishedIncidentDao = publishedIncidentDao;
	}

	public void setAttachmentDao(AttachmentDao attachmentDao) {
		this.attachmentDao = attachmentDao;
	}

	public void setAttachmentFactory(AttachmentFactory attachmentFactory) {
		this.attachmentFactory = attachmentFactory;
	}

	public void setExternalUriDao(ExternalUriDao externalUriDao) {
		this.externalUriDao = externalUriDao;
	}

	@Value("${WFNEWS_AGOL_QUERY_URL}")
	private String agolQueryUrl;

	private String concatenatedQueryString = "&f=pjson&outFields=*&inSR=4326";

	@Override
	public IncidentListResource getIncidents(String status, String date, Double minLatitude, Double maxLatitude,
			Double minLongitude, Double maxLongitude) {
		IncidentListResource result = new IncidentListResource();

		String queryUrl = agolQueryUrl;

		if (status != null && status != "") {
			queryUrl = queryUrl + "FIRE_STATUS+%3D+'" + status.replace(" ", "+") + "'";
		} else
			queryUrl = queryUrl + "FIRE_STATUS+%3C%3E+'OUT'";

		if (date != null && date != "") {
			queryUrl = queryUrl + "+AND+IGNITION_DATE+%3E%3D+'" + date + "+00%3A00%3A00'+AND+IGNITION_DATE+%3C%3D+'" + date
					+ "+23%3A59%3A59'";
		}

		if (minLatitude != null && maxLatitude != null && minLongitude != null && maxLongitude != null) {
			queryUrl = queryUrl + "&geometryType=esriGeometryEnvelope&geometry=" + minLongitude + "%2C+" + minLatitude
					+ "%2C+" + maxLongitude + "%2C+" + maxLatitude;
		}

		queryUrl = queryUrl + concatenatedQueryString;

		try {

			HttpResponse<JsonNode> response = Unirest
					.post(queryUrl)
					.header("Content-Type", "application/json")
					.header("Accept", "*/*")
					.asJson();

			if (response != null) {
				result = getIncidentResourceListFromJsonBody(response.getBody());
			}

		} catch (Exception e) {
			logger.error("Failed to retrive JSON from AGOL service for all incidents", e);
		}

		return result;

	}

	@Override
	public IncidentResource getIncidentByID(String id) {
		IncidentResource result = new IncidentResource();
		IncidentListResource incidentListResource = null;
		String queryUrl = agolQueryUrl + "FIRE_ID+%3D" + id + concatenatedQueryString;
		try {

			HttpResponse<JsonNode> response = Unirest
					.post(queryUrl)
					.header("Content-Type", "application/json")
					.header("Accept", "*/*")
					.asJson();

			if (response != null) {
				incidentListResource = getIncidentResourceListFromJsonBody(response.getBody());
			}

		} catch (Exception e) {
			logger.error("Failed to retrive JSON from AGOL service for all incidents", e);
		}

		if (incidentListResource != null && incidentListResource.getCollection() != null
				&& !incidentListResource.getCollection().isEmpty()) {
			result = incidentListResource.getCollection().get(0);
		}

		return result;
	}

	public IncidentListResource getIncidentResourceListFromJsonBody(JsonNode jsonNode) {
		IncidentListResource result = new IncidentListResource();
		List<IncidentResource> incidentResourceList = new ArrayList<IncidentResource>();

		JSONObject incidentJson = new JSONObject(jsonNode);
		JSONArray arrayJson = incidentJson.getJSONArray("array");
		JSONObject obj = arrayJson.optJSONObject(0);
		JSONArray featuresArr = obj.optJSONArray("features");

		if (featuresArr != null) {
			for (int i = 0; i < featuresArr.length(); i++) {
				IncidentResource incidentResource = new IncidentResource();

				JSONObject obj1 = featuresArr.optJSONObject(i);
				JSONObject attributesObj = obj1.optJSONObject("attributes");

				if (attributesObj.has("FIRE_NUMBER") && !attributesObj.optString("FIRE_NUMBER", "").equals(""))
					incidentResource.setFireNumber(attributesObj.optString("FIRE_NUMBER"));
				if (attributesObj.has("FIRE_YEAR") && (attributesObj.optInt("FIRE_YEAR") != (0)))
					incidentResource.setFireYear(attributesObj.optInt("FIRE_YEAR"));
				if (attributesObj.has("IGNITION_DATE") && (attributesObj.optLong("IGNITION_DATE") != (0)))
					incidentResource.setIgnitionDate(attributesObj.optLong("IGNITION_DATE"));
				if (attributesObj.has("FIRE_STATUS") && !attributesObj.optString("FIRE_STATUS", "").equals(""))
					incidentResource.setFireStatus(attributesObj.optString("FIRE_STATUS"));
				if (attributesObj.has("FIRE_CAUSE") && !attributesObj.optString("FIRE_CAUSE", "").equals(""))
					incidentResource.setFireCause(attributesObj.optString("FIRE_CAUSE"));
				if (attributesObj.has("FIRE_CENTRE") && (attributesObj.optInt("FIRE_CENTRE") != (0)))
					incidentResource.setFireCentre(attributesObj.optInt("FIRE_CENTRE"));
				if (attributesObj.has("FIRE_ID") && (attributesObj.optInt("FIRE_ID") != (0)))
					incidentResource.setFireID(attributesObj.optInt("FIRE_ID"));
				if (attributesObj.has("FIRE_TYPE") && !attributesObj.optString("FIRE_TYPE", "").equals(""))
					incidentResource.setFireType(attributesObj.optString("FIRE_TYPE"));
				if (attributesObj.has("GEOGRAPHIC_DESCRIPTION")
						&& !attributesObj.optString("GEOGRAPHIC_DESCRIPTION", "").equals(""))
					incidentResource.setGeographicDescription(attributesObj.optString("GEOGRAPHIC_DESCRIPTION"));
				if (attributesObj.has("ZONE") && (attributesObj.optInt("ZONE") != (0)))
					incidentResource.setZone(attributesObj.optInt("ZONE"));
				if (attributesObj.has("LATITUDE") && (attributesObj.optDouble("LATITUDE") != (0)))
					incidentResource.setLatitude(attributesObj.optDouble("LATITUDE"));
				if (attributesObj.has("LONGITUDE") && (attributesObj.optDouble("LONGITUDE") != (0)))
					incidentResource.setLongitude(attributesObj.optDouble("LONGITUDE"));
				if (attributesObj.has("CURRENT_SIZE") && (attributesObj.optInt("CURRENT_SIZE") != (0)))
					incidentResource.setCurrentSize(attributesObj.optInt("CURRENT_SIZE"));
				if (attributesObj.has("FIRE_OF_NOTE_URL") && !attributesObj.optString("FIRE_OF_NOTE_URL", "").equals(""))
					incidentResource.setFireOfNoteURL(attributesObj.optString("FIRE_OF_NOTE_URL"));
				if (attributesObj.has("FIRE_OF_NOTE_ID") && !attributesObj.optString("FIRE_OF_NOTE_ID", "").equals(""))
					incidentResource.setFireOfNoteID(attributesObj.optString("FIRE_OF_NOTE_ID"));
				if (attributesObj.has("FIRE_OF_NOTE_NAME") && !attributesObj.optString("FIRE_OF_NOTE_NAME", "").equals(""))
					incidentResource.setFireOfNoteName(attributesObj.optString("FIRE_OF_NOTE_NAME"));
				if (attributesObj.has("FEATURE_CODE") && !attributesObj.optString("FEATURE_CODE", "").equals(""))
					incidentResource.setFeatureCode(attributesObj.optString("FEATURE_CODE"));
				if (attributesObj.has("OBJECT_ID") && (attributesObj.optInt("OBJECT_ID") != (0)))
					incidentResource.setObjectID(attributesObj.optInt("OBJECT_ID"));
				if (attributesObj.has("GLOBAL_ID") && !attributesObj.optString("GLOBAL_ID", "").equals(""))
					incidentResource.setGlobalID(attributesObj.optString("GLOBAL_ID"));

				incidentResourceList.add(incidentResource);
			}

		}

		result.setCollection(incidentResourceList);

		return result;
	}

	@Override
	public PublishedIncidentResource createPublishedWildfireIncident(PublishedIncident publishedIncident,
			FactoryContext factoryContext)
			throws ValidationFailureException, ConflictException, NotFoundException, Exception {
		logger.debug("<createupdatePublishedWildfireIncident");
		PublishedIncidentResource response = null;

		long effectiveAsOfMillis = publishedIncident.getDiscoveryDate() == null ? System.currentTimeMillis()
				: publishedIncident.getDiscoveryDate().getTime();

		try {
			List<Message> errors = this.modelValidator.validatePublishedIncident(publishedIncident, effectiveAsOfMillis);

			if (!errors.isEmpty()) {
				throw new ValidationException(errors);
			}

			PublishedIncidentResource result = new PublishedIncidentResource();

			result = (PublishedIncidentResource) createPublishedWildfireIncident(
					publishedIncident,
					getWebAdeAuthentication(),
					factoryContext);

			response = result;

		} catch (IntegrityConstraintViolatedDaoException e) {
			throw new ConflictException(e.getMessage());
		} catch (NotFoundDaoException e) {
			throw new NotFoundException(e.getMessage());
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		}

		logger.debug(">createPublishedWildfireIncident");
		return response;

	}

	@Override
	public PublishedIncidentResource updatePublishedWildfireIncident(PublishedIncident publishedIncident,
			FactoryContext factoryContext)
			throws ValidationFailureException, ConflictException, NotFoundException, Exception {
		logger.debug("<updatePublishedWildfireIncident");
		PublishedIncidentResource response = null;

		long effectiveAsOfMillis = publishedIncident.getDiscoveryDate() == null ? System.currentTimeMillis()
				: publishedIncident.getDiscoveryDate().getTime();

		try {
			List<Message> errors = this.modelValidator.validatePublishedIncident(publishedIncident, effectiveAsOfMillis);

			if (!errors.isEmpty()) {
				throw new ValidationException(errors);
			}

			PublishedIncidentResource result = new PublishedIncidentResource();

			PublishedIncidentResource currentWildfireIncident = (PublishedIncidentResource) getPublishedIncident(
					publishedIncident.getPublishedIncidentDetailGuid(),
					getWebAdeAuthentication(),
					factoryContext);

			if (currentWildfireIncident != null) {
				result = (PublishedIncidentResource) updatePublishedWildfireIncident(
						publishedIncident,
						getWebAdeAuthentication(),
						factoryContext);
			} else {
				result = (PublishedIncidentResource) createPublishedWildfireIncident(
						publishedIncident,
						getWebAdeAuthentication(),
						factoryContext);
			}

			response = result;

		} catch (IntegrityConstraintViolatedDaoException e) {
			throw new ConflictException(e.getMessage());
		} catch (NotFoundDaoException e) {
			throw new NotFoundException(e.getMessage());
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		}

		logger.debug(">updatePublishedWildfireIncident");
		return response;

	}

	private PublishedIncidentResource updatePublishedWildfireIncident(PublishedIncident publishedIncident,
			WebAdeAuthentication webAdeAuthentication, FactoryContext factoryContext) throws DaoException {

		PublishedIncidentResource result = null;
		PublishedIncidentDto dto = new PublishedIncidentDto(publishedIncident);
		try {
			dto.setUpdateDate(new Date());
			if (webAdeAuthentication != null && webAdeAuthentication.getUserId() != null)
				dto.setUpdateUser(webAdeAuthentication.getUserId());
			if (dto.getCreateUser() == null && webAdeAuthentication != null && webAdeAuthentication.getUserId() != null)
				dto.setCreateUser(webAdeAuthentication.getUserId());
			if (dto.getCreateDate() == null && webAdeAuthentication != null && webAdeAuthentication.getUserId() != null)
				dto.setCreateDate(new Date());

			this.publishedIncidentDao.update(dto);
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(), e);
		}

		PublishedIncidentDto updatedDto = this.publishedIncidentDao.fetch(dto.getPublishedIncidentDetailGuid());

		result = this.publishedIncidentFactory.getPublishedWildfireIncident(updatedDto, factoryContext);
		return result;
	}

	private PublishedIncidentResource createPublishedWildfireIncident(PublishedIncident publishedIncident,
			WebAdeAuthentication webAdeAuthentication, FactoryContext factoryContext) throws DaoException {

		PublishedIncidentResource result = null;
		PublishedIncidentDto dto = new PublishedIncidentDto(publishedIncident);
		try {
			dto.setUpdateDate(new Date());
			dto.setRevisionCount(Long.valueOf(0));
			if (webAdeAuthentication != null && webAdeAuthentication.getUserId() != null)
				dto.setUpdateUser(webAdeAuthentication.getUserId());
			if (dto.getCreateUser() == null && webAdeAuthentication != null && webAdeAuthentication.getUserId() != null)
				dto.setCreateUser(webAdeAuthentication.getUserId());
			if (dto.getCreateDate() == null && webAdeAuthentication != null && webAdeAuthentication.getUserId() != null)
				dto.setCreateDate(new Date());

			this.publishedIncidentDao.insert(dto);
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(), e);
		}

		PublishedIncidentDto updatedDto = this.publishedIncidentDao.fetch(dto.getPublishedIncidentDetailGuid());

		result = this.publishedIncidentFactory.getPublishedWildfireIncident(updatedDto, factoryContext);
		return result;
	}

	@Override
	public String getPublishedIncidentsAsJson(String stageOfControl, String bbox,
			WebAdeAuthentication webAdeAuthentication, FactoryContext factoryContext) throws DaoException {
		return this.publishedIncidentDao.selectAsJson(stageOfControl, bbox);
	}

	@Override
	public String getFireOfNoteAsJson(String bbox, WebAdeAuthentication webAdeAuthentication,
			FactoryContext factoryContext) throws DaoException {
		return this.publishedIncidentDao.selectFireOfNoteAsJson(bbox);
	}

	@Override
	public PublishedIncidentResource getPublishedIncident(String publishedIncidentDetailGuid,
			WebAdeAuthentication webAdeAuthentication, FactoryContext factoryContext) throws DaoException, NotFoundException {

		PublishedIncidentResource result = null;
		PublishedIncidentDto fetchedDto = this.publishedIncidentDao.fetch(publishedIncidentDetailGuid);
		if (fetchedDto != null) {
			result = this.publishedIncidentFactory.getPublishedWildfireIncident(fetchedDto, factoryContext);
		} else
			throw new NotFoundException("Did not find the publishedIncidentDetailGuid: " + publishedIncidentDetailGuid);
		return result;
	}

	@Override
	public PublishedIncidentResource getPublishedIncidentByIncidentGuid(String incidentGuid,
			WebAdeAuthentication webAdeAuthentication, FactoryContext factoryContext) throws DaoException, NotFoundException {

		PublishedIncidentResource result = null;
		PublishedIncidentDto fetchedDto = this.publishedIncidentDao.fetchForIncidentGuid(incidentGuid);
		if (fetchedDto != null) {
			result = this.publishedIncidentFactory.getPublishedWildfireIncident(fetchedDto, factoryContext);
		} else
			throw new NotFoundException("Did not find the publishedIncidentDetailGuid: " + incidentGuid);
		return result;
	}

	@Override
	public void flush(FactoryContext factoryContext) throws NotFoundException, ConflictException {
		try {
			this.publishedIncidentDao.flush();
		} catch (Exception e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public void deletePublishedIncident(String publishedIncidentDetailGuid, FactoryContext factoryContext)
			throws NotFoundException, ConflictException {
		logger.debug("<deletePublishedIncident");

		try {

			PublishedIncidentDto dto = this.publishedIncidentDao.fetch(publishedIncidentDetailGuid);

			if (dto == null) {
				throw new NotFoundException("Did not find the PublishedIncident: " + publishedIncidentDetailGuid);
			}

			this.publishedIncidentDao.delete(publishedIncidentDetailGuid);

		} catch (IntegrityConstraintViolatedDaoException e) {
			throw new ConflictException(e.getMessage());
		} catch (OptimisticLockingFailureDaoException e) {
			throw new ConflictException(e.getMessage());
		} catch (NotFoundDaoException e) {
			throw new NotFoundException(e.getMessage());
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(), e);
		}

		logger.debug(">deletePublishedIncident");
	}

	@Override
	public PublishedIncidentListResource getPublishedIncidentList(String searchText, Integer pageNumber,
			Integer pageRowCount, String orderBy, Boolean fireOfNote, Boolean out, String fireCentre, String bbox,
			Double latitude, Double longitude, Double radius, FactoryContext factoryContext) {
		PublishedIncidentListResource results = null;
		PagedDtos<PublishedIncidentDto> publishedIncidentList = new PagedDtos<>();
		try {

			List<String> orderByList = new ArrayList<>();
			if (orderBy != null && orderBy.split(",").length > 0) {
				for (String orderbyString : orderBy.split(",")) {
					String daoDirection = null;
					String daoOrderByString = "";
					String[] split = orderbyString.split("\\s+");

					if (split != null && split.length > 0) {
						daoOrderByString = split[0];
					}

					if (split != null && daoOrderByString.length() > 0) {
						if (split.length > 1) {
							String direction = split[1];
							if (direction.equalsIgnoreCase("desc") || direction.equalsIgnoreCase("descending")) {
								daoDirection = "DESC";
							} else {
								daoDirection = "ASC";
							}
						} else {
							daoDirection = "ASC";
						}

						orderByList.add(daoOrderByString);
						orderByList.add(daoDirection);
					}
				}
			}

			publishedIncidentList = this.publishedIncidentDao.select(searchText, pageNumber, pageRowCount, orderByList,
					fireOfNote, out, fireCentre, bbox, latitude, longitude, radius);
			results = this.publishedIncidentFactory.getPublishedIncidentList(publishedIncidentList, pageNumber, pageRowCount,
					factoryContext);
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(), e);
		}

		return results;
	}

	@Override
	public ExternalUriResource createExternalUri(ExternalUriResource externalUri, FactoryContext factoryContext)
			throws ValidationFailureException, ConflictException, NotFoundException, Exception {
		ExternalUriResource response = null;
		long effectiveAsOfMillis = externalUri.getCreateDate() == null ? System.currentTimeMillis()
				: externalUri.getCreateDate().getTime();
		try {
			List<Message> errors = this.modelValidator.validateExternalUri(externalUri, effectiveAsOfMillis);
			if (!errors.isEmpty()) {
				throw new Exception("Validation failed for ExternalUri: " + errors.toString());
			}

			ExternalUriResource result = new ExternalUriResource();
			result = (ExternalUriResource) createExternalUri(
					externalUri,
					getWebAdeAuthentication(),
					factoryContext);

			response = result;

		} catch (IntegrityConstraintViolatedDaoException e) {
			throw new ConflictException(e.getMessage());
		} catch (NotFoundDaoException e) {
			throw new NotFoundException(e.getMessage());
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		}

		logger.debug(">PublishedWildfireIncident");
		return response;

	}

	@Override
	public ExternalUriResource updateExternalUri(ExternalUriResource externalUri, FactoryContext factoryContext)
			throws ValidationFailureException, ConflictException, NotFoundException, Exception {
		ExternalUriResource response = null;
		long effectiveAsOfMillis = externalUri.getCreateDate() == null ? System.currentTimeMillis()
				: externalUri.getCreateDate().getTime();
		try {
			List<Message> errors = this.modelValidator.validateExternalUri(externalUri, effectiveAsOfMillis);
			if (!errors.isEmpty()) {
				throw new Exception("Validation failed for ExternalUri: " + errors.toString());
			}

			ExternalUriResource result = new ExternalUriResource();

			ExternalUriResource currentExternalUri = (ExternalUriResource) getExternalUri(
					externalUri.getExternalUriGuid(),
					getWebAdeAuthentication(),
					factoryContext);

			if (currentExternalUri != null) {
				result = (ExternalUriResource) updateExternalUri(
						externalUri,
						getWebAdeAuthentication(),
						factoryContext);
			} else {
				result = (ExternalUriResource) createExternalUri(
						externalUri,
						getWebAdeAuthentication(),
						factoryContext);
			}

			response = result;

		} catch (IntegrityConstraintViolatedDaoException e) {
			throw new ConflictException(e.getMessage());
		} catch (NotFoundDaoException e) {
			throw new NotFoundException(e.getMessage());
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		}

		logger.debug(">PublishedWildfireIncident");
		return response;

	}

	@Override
	public ExternalUriResource getExternalUri(String externalUriGuid, WebAdeAuthentication webAdeAuthentication,
			FactoryContext factoryContext) throws DaoException, NotFoundException {

		ExternalUriResource result = null;
		ExternalUriDto fetchedDto = this.externalUriDao.fetch(externalUriGuid);
		if (fetchedDto != null) {
			result = this.externalUriFactory.getExternalUri(fetchedDto, factoryContext);
		} else
			throw new NotFoundException("Did not find the externalUriGuid: " + externalUriGuid);
		return result;
	}

	private ExternalUriResource createExternalUri(ExternalUriResource publishedIncident,
			WebAdeAuthentication webAdeAuthentication, FactoryContext factoryContext) throws DaoException {

		ExternalUriResource result = null;
		ExternalUriDto dto = new ExternalUriDto(publishedIncident);
		try {
			dto.setCreateDate(new Date());
			dto.setUpdateDate(new Date());
			dto.setRevisionCount(Long.valueOf(0));
			if (dto.getCreatedTimestamp() == null)
				dto.setCreatedTimestamp(new Date());
			if (webAdeAuthentication != null && webAdeAuthentication.getUserId() != null)
				dto.setUpdateUser(webAdeAuthentication.getUserId());
			if (webAdeAuthentication != null && webAdeAuthentication.getUserId() != null)
				dto.setCreateUser(webAdeAuthentication.getUserId());

			this.externalUriDao.insert(dto);
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(), e);
		}

		ExternalUriDto updatedDto = this.externalUriDao.fetch(dto.getExternalUriGuid());

		result = this.externalUriFactory.getExternalUri(updatedDto, factoryContext);
		return result;
	}

	private ExternalUriResource updateExternalUri(ExternalUriResource publishedIncident,
			WebAdeAuthentication webAdeAuthentication, FactoryContext factoryContext) throws DaoException {

		ExternalUriResource result = null;
		ExternalUriDto dto = new ExternalUriDto(publishedIncident);
		try {
			dto.setUpdateDate(new Date());
			if (dto.getCreateDate() == null)
				dto.setCreateDate(new Date());
			if (dto.getCreatedTimestamp() == null)
				dto.setCreatedTimestamp(new Date());
			if (webAdeAuthentication != null && webAdeAuthentication.getUserId() != null)
				dto.setUpdateUser(webAdeAuthentication.getUserId());
			if (webAdeAuthentication != null && webAdeAuthentication.getUserId() != null)
				dto.setCreateUser(webAdeAuthentication.getUserId());

			this.externalUriDao.update(dto);
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(), e);
		}

		ExternalUriDto updatedDto = this.externalUriDao.fetch(dto.getExternalUriGuid());

		result = this.externalUriFactory.getExternalUri(updatedDto, factoryContext);
		return result;
	}

	@Override
	public void deleteExternalUri(String externalUriGuid, FactoryContext factoryContext)
			throws NotFoundException, ConflictException {
		logger.debug("<deleteExternalUri");

		try {
			ExternalUriDto dto = this.externalUriDao.fetch(externalUriGuid);

			if (dto == null) {
				throw new NotFoundException("Did not find the externalUri: " + externalUriGuid);
			}

			this.externalUriDao.delete(externalUriGuid, getWebAdeAuthentication().getUserId());

		} catch (IntegrityConstraintViolatedDaoException | OptimisticLockingFailureDaoException e) {
			throw new ConflictException(e.getMessage());
		} catch (NotFoundDaoException e) {
			throw new NotFoundException(e.getMessage());
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(), e);
		}

		logger.debug(">deleteExternalUri");
	}

	@Override
	public ExternalUriListResource getExternalUriList(String sourceObjectUniqueId, Integer pageNumber,
			Integer pageRowCount, FactoryContext factoryContext) {
		ExternalUriListResource results = null;
		PagedDtos<ExternalUriDto> externalUriList = null;
		try {
			// if sourceObjectUniqueId is null return all
			if (sourceObjectUniqueId != null) {
				externalUriList = this.externalUriDao.selectForIncident(sourceObjectUniqueId, pageNumber, pageRowCount);
			} else
				externalUriList = this.externalUriDao.select(pageNumber, pageRowCount);
			results = this.externalUriFactory.getExternalUriList(externalUriList, pageNumber, pageRowCount, factoryContext);
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(), e);
		}

		return results;
	}

	@Override
	public AttachmentListResource getIncidentAttachmentList(String incidentNumberSequence, boolean primaryIndicator,
			String[] sourceObjectNameCodes, String[] attachmentTypeCodes, Integer pageNumber, Integer pageRowCount,
			String[] orderBy, FactoryContext factoryContext) {
		AttachmentListResource result = new AttachmentListResource();

		PagedDtos<PublishedIncidentDto> publishedIncidentList = new PagedDtos<>();
		try {

			List<String> orderByList = new ArrayList<>();
			if (orderBy != null && orderBy.length > 0) {
				for (String orderbyString : orderBy) {
					String daoDirection = null;
					String daoOrderByString = "";
					String[] split = orderbyString.split("\\s+");
					if (split != null && split.length > 0) {
						String orderByProperty = split[0];

						switch (orderByProperty) {
							case "attachmentTypeCode":
								daoOrderByString = orderByProperty;
								break;
							case "sourceObjectNameCode":
								daoOrderByString = orderByProperty;
								break;
							case "sourceObjectUniqueId":
								daoOrderByString = orderByProperty;
								break;
							case "uploadedByUserType":
								daoOrderByString = orderByProperty;
								break;
							case "uploadedByUserId":
								daoOrderByString = orderByProperty;
								break;
							case "uploadedByUserGuid":
								daoOrderByString = orderByProperty;
								break;

							default: {
								logger.warn("Ignoring unsupported order by: " + orderByProperty);
								continue;
							}
						}
					}
					if (daoOrderByString.length() > 0) {
						if (split.length > 1) {
							String direction = split[1];
							if (direction.equalsIgnoreCase("desc") || direction.equalsIgnoreCase("descending")) {
								daoDirection = "DESC";
							} else {
								daoDirection = "ASC";
							}
						} else {
							daoDirection = "ASC";
						}

						orderByList.add(daoOrderByString);
						orderByList.add(daoDirection);
					}
				}
			}

			String[] newOrderBy = null;
			if (orderByList.size() > 0) {
				newOrderBy = orderByList.toArray(new String[0]);
			}

			PagedDtos<AttachmentDto> list = this.attachmentDao.select(incidentNumberSequence, primaryIndicator,
					sourceObjectNameCodes, attachmentTypeCodes, pageNumber, pageRowCount, newOrderBy);
			result = this.attachmentFactory.getAttachmentList(list, pageNumber, pageRowCount, factoryContext);
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(), e);
		}

		return result;
	}

	@Override
	public AttachmentResource createIncidentAttachment(AttachmentResource attachment,
			WebAdeAuthentication webAdeAuthentication, FactoryContext factoryContext)
			throws ValidationFailureException, ConflictException, NotFoundException, Exception {
		AttachmentResource response = null;
		long effectiveAsOfMillis = attachment.getCreatedTimestamp() == null ? System.currentTimeMillis()
				: attachment.getCreatedTimestamp().getTime();
		try {
			List<Message> errors = this.modelValidator.validateAttachment(attachment, effectiveAsOfMillis);
			if (!errors.isEmpty()) {
				throw new Exception("Validation failed for attachment: " + errors.toString());
			}

			AttachmentResource result = new AttachmentResource();
			AttachmentDto dto = new AttachmentDto(attachment);
			try {
				dto.setCreateDate(new Date());
				dto.setUpdateDate(new Date());
				dto.setRevisionCount(Long.valueOf(0));
				if (dto.getCreatedTimestamp() == null)
					dto.setCreatedTimestamp(new Date());
				if (webAdeAuthentication != null && webAdeAuthentication.getUserId() != null)
					dto.setUpdateUser(webAdeAuthentication.getUserId());
				if (webAdeAuthentication != null && webAdeAuthentication.getUserId() != null)
					dto.setCreateUser(webAdeAuthentication.getUserId());

				this.attachmentDao.insert(dto);
			} catch (DaoException e) {
				throw new ServiceException(e.getMessage(), e);
			}

			AttachmentDto updatedDto = this.attachmentDao.fetch(dto.getAttachmentGuid());

			result = this.attachmentFactory.getAttachment(updatedDto, factoryContext);

			response = result;

			/* Also Call WFDM, and copy the attachment bytes into the AWS bucket! */

		} catch (IntegrityConstraintViolatedDaoException e) {
			throw new ConflictException(e.getMessage());
		} catch (NotFoundDaoException e) {
			throw new NotFoundException(e.getMessage());
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		}

		logger.debug(">PublishedWildfireIncident");
		return response;
	}

	@Override
	public AttachmentResource updateIncidentAttachment(AttachmentResource attachment,
			WebAdeAuthentication webAdeAuthentication, FactoryContext factoryContext)
			throws ValidationFailureException, ConflictException, NotFoundException, Exception {
		AttachmentResource response = null;
		long effectiveAsOfMillis = attachment.getCreatedTimestamp() == null ? System.currentTimeMillis()
				: attachment.getCreatedTimestamp().getTime();

		try {
			List<Message> errors = this.modelValidator.validateAttachment(attachment, effectiveAsOfMillis);

			if (!errors.isEmpty()) {
				throw new Exception("Validation failed for attachment: " + errors.toString());
			}

			AttachmentResource result = new AttachmentResource();
			AttachmentResource currentAttachment = getIncidentAttachment(attachment.getAttachmentGuid(), factoryContext);

			if (currentAttachment != null) {
				AttachmentDto dto = new AttachmentDto(attachment);

				try {
					dto.setUpdateDate(new Date());
					if (dto.getCreateDate() == null)
						dto.setCreateDate(new Date());
					if (dto.getCreatedTimestamp() == null)
						dto.setCreatedTimestamp(new Date());
					if (webAdeAuthentication != null && webAdeAuthentication.getUserId() != null)
						dto.setUpdateUser(webAdeAuthentication.getUserId());
					if (webAdeAuthentication != null && webAdeAuthentication.getUserId() != null)
						dto.setCreateUser(webAdeAuthentication.getUserId());

					this.attachmentDao.update(dto);
				} catch (DaoException e) {
					throw new ServiceException(e.getMessage(), e);
				}

				AttachmentDto updatedDto = this.attachmentDao.fetch(dto.getAttachmentGuid());
				result = this.attachmentFactory.getAttachment(updatedDto, factoryContext);

				/* Also Re-upload the attachment from WFDM into the AWS bucket! */
			}

			response = result;

		} catch (IntegrityConstraintViolatedDaoException e) {
			throw new ConflictException(e.getMessage());
		} catch (NotFoundDaoException e) {
			throw new NotFoundException(e.getMessage());
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e);
		}

		logger.debug(">updateIncidentAttachment");
		return response;
	}

	@Override
	public AttachmentResource deleteIncidentAttachment(String attachmentGuid, WebAdeAuthentication webAdeAuthentication,
			FactoryContext factoryContext)
			throws ValidationFailureException, ConflictException, NotFoundException, Exception {
		logger.debug("<deleteIncidentAttachment");

		try {
			AttachmentDto dto = this.attachmentDao.fetch(attachmentGuid);
			AttachmentResource result = this.attachmentFactory.getAttachment(dto, factoryContext);

			if (dto == null) {
				throw new NotFoundException("Did not find the attachment: " + attachmentGuid);
			}

			this.attachmentDao.delete(attachmentGuid, getWebAdeAuthentication().getUserId());

			/* Also Delete the attachment from the AWS bucket! */

			return result;
		} catch (IntegrityConstraintViolatedDaoException | OptimisticLockingFailureDaoException e) {
			throw new ConflictException(e.getMessage());
		} catch (NotFoundDaoException e) {
			throw new NotFoundException(e.getMessage());
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public AttachmentResource getIncidentAttachment(String attachmentGuid, FactoryContext factoryContext)
			throws ValidationFailureException, ConflictException, NotFoundException, Exception {
		try {
			AttachmentDto dto = this.attachmentDao.fetch(attachmentGuid);
			return this.attachmentFactory.getAttachment(dto, factoryContext);
		} catch (IntegrityConstraintViolatedDaoException | OptimisticLockingFailureDaoException e) {
			throw new ConflictException(e.getMessage());
		} catch (NotFoundDaoException e) {
			throw new NotFoundException(e.getMessage());
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}
}
