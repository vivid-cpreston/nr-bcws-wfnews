package ca.bc.gov.nrs.wfnews.api.rest.v1.endpoints.impl;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import ca.bc.gov.nrs.wfnews.api.rest.v1.common.AttachmentsAwsConfig;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.springframework.beans.factory.annotation.Autowired;

import ca.bc.gov.nrs.common.wfone.rest.resource.MessageListRsrc;
import ca.bc.gov.nrs.wfnews.api.rest.v1.endpoints.AttachmentsEndpoint;
import ca.bc.gov.nrs.wfnews.api.rest.v1.endpoints.security.Scopes;
import ca.bc.gov.nrs.wfnews.api.rest.v1.resource.AttachmentResource;
import ca.bc.gov.nrs.wfnews.service.api.v1.IncidentsService;
import ca.bc.gov.nrs.wfone.common.rest.endpoints.BaseEndpointsImpl;
import ca.bc.gov.nrs.wfone.common.service.api.ConflictException;
import ca.bc.gov.nrs.wfone.common.service.api.ForbiddenException;
import ca.bc.gov.nrs.wfone.common.service.api.NotFoundException;
import ca.bc.gov.nrs.wfone.common.service.api.ValidationFailureException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.utils.IoUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;

public class AttachmentsEndpointImpl extends BaseEndpointsImpl implements AttachmentsEndpoint {

  @Autowired
  private IncidentsService incidentsService;

  @Autowired
  private AttachmentsAwsConfig attachmentsAwsConfig;

  @Override
  public Response getIncidentAttachment(String incidentNumberSequence, String attachmentGuid) {
    Response response = null;
		
		logRequest();

		try {
			AttachmentResource result = incidentsService.getIncidentAttachment(
					attachmentGuid,
					getFactoryContext());
			response = Response.ok(result).tag(result.getUnquotedETag()).build();
		} catch (ForbiddenException e) {
			response = Response.status(Status.FORBIDDEN).build();
		} catch (NotFoundException e) {
			response = Response.status(Status.NOT_FOUND).build();
			
		} catch (Throwable t) {
			response = getInternalServerErrorResponse(t);
		}
		
		logResponse(response);

		return response;
  }

  @Override
  public Response updateIncidentAttachment(String incidentNumberSequence, String attachmentGuid, AttachmentResource attachment) {
    Response response = null;
		
		logRequest();
		
		if(!hasAuthority(Scopes.UPDATE_ATTACHMENT)) {
			return Response.status(Status.FORBIDDEN).build();
		}

		try {

			AttachmentResource current = this.incidentsService.getIncidentAttachment(
					attachmentGuid,
					getFactoryContext());
			
			EntityTag currentTag = EntityTag.valueOf(current.getQuotedETag());

			ResponseBuilder responseBuilder = this.evaluatePreconditions(currentTag);

			if (responseBuilder == null) {
				AttachmentResource result = incidentsService.updateIncidentAttachment(
						attachment,
						getWebAdeAuthentication(),
						getFactoryContext());

				response = Response.ok(result).tag(result.getUnquotedETag()).build();
			} else {
				response = responseBuilder.tag(currentTag).build();
			}

		} catch (ForbiddenException e) {
			response = Response.status(Status.FORBIDDEN).build();
		} catch(ValidationFailureException e) {
			response = Response.status(Status.BAD_REQUEST).entity(new MessageListRsrc(e.getValidationErrors())).build();
		} catch (ConflictException e) {
			response = Response.status(Status.CONFLICT).entity(e.getMessage()).build();
		} catch (NotFoundException e) {
			response = Response.status(Status.NOT_FOUND).build();
		} catch (Throwable t) {
			response = getInternalServerErrorResponse(t);
		}
		
		logResponse(response);

		return response;
  }

  @Override
  public Response deleteIncidentAttachment(String incidentNumberSequence, String attachmentGuid) {
    Response response = null;
		
		logRequest();
		
		if(!hasAuthority(Scopes.DELETE_ATTACHMENT)) {
			return Response.status(Status.FORBIDDEN).build();
		}
		
		try {
				
			AttachmentResource current = incidentsService.getIncidentAttachment(
					attachmentGuid,
					getFactoryContext());

			EntityTag currentTag = EntityTag.valueOf(current.getQuotedETag());

			ResponseBuilder responseBuilder = this.evaluatePreconditions(currentTag);

			if (responseBuilder == null) {
				incidentsService.deleteIncidentAttachment(
						attachmentGuid,
						getWebAdeAuthentication(),
						getFactoryContext());

				response = Response.status(204).build();
			} else {
				response = responseBuilder.tag(currentTag).build();
			}

		} catch (ForbiddenException e) {
			response = Response.status(Status.FORBIDDEN).build();
		} catch (ConflictException e) {
			response = Response.status(Status.CONFLICT).entity(e.getMessage()).build();
		} catch (NotFoundException e) {
			response = Response.status(Status.NOT_FOUND).build();
		} catch (Throwable t) {
			response = getInternalServerErrorResponse(t);
		}
		
		logResponse(response);

		return response;
  }

	@Override
	public Response createIncidentAttachmentBytes(String incidentNumberSequence, String attachmentGuid, FormDataBodyPart file) {
		Response response = null;
		
		logRequest();

		InputStream inputStream = null;

		try {
			AttachmentResource result = incidentsService.getIncidentAttachment(attachmentGuid, getFactoryContext());

			if (result != null) {
				S3Client s3Client = S3Client.builder().region(Region.CA_CENTRAL_1).build();
        
				PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(attachmentsAwsConfig.getBucketName()).key(incidentNumberSequence + FileSystems.getDefault().getSeparator() + result.getFileName()).contentType(result.getMimeType()).build();
				inputStream = file.getEntityAs(InputStream.class);
				final PutObjectResponse s3Object = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(inputStream.readAllBytes()));

				response = Response.status(s3Object.sdkHttpResponse().statusCode()).build();
			} else {
				response = Response.status(Status.NOT_FOUND).build();
			}
		} catch (ForbiddenException e) {
			response = Response.status(Status.FORBIDDEN).build();
		} catch (NotFoundException e) {
			response = Response.status(Status.NOT_FOUND).build();
		} catch (Throwable t) {
			response = getInternalServerErrorResponse(t);
		}
		
		logResponse(response);

		return response;
	}

	@Override
	public Response getIncidentAttachmentBytes(String incidentNumberSequence, String attachmentGuid) {
		Response response = null;

		logRequest();

		try {
			AttachmentResource result = incidentsService.getIncidentAttachment(attachmentGuid, getFactoryContext());

			if (result != null) {
				S3Client s3Client = S3Client.builder().region(Region.CA_CENTRAL_1).build();

				GetObjectRequest getObjectRequest = GetObjectRequest.builder()
						.bucket(attachmentsAwsConfig.getBucketName())
						.key(incidentNumberSequence + FileSystems.getDefault().getSeparator() + result.getFileName())
						.build();

				byte[] content;

				final ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
				content = IoUtils.toByteArray(s3Object);
				s3Object.close();
				response = Response.status(200)
						.header("Content-type", "application/octet-stream")
						.header("Content-disposition", "attachment; filename=\"" + result.getFileName() + "\"")
						.header("Cache-Control", "no-cache")
						.header("Content-Length", content.length)
						.entity(content)
						.build();
			} else {
				response = Response.status(404).build();
			}
		} catch (NoSuchKeyException e) {
			response = Response.status(404).build();
		} catch (IOException e) {
			response = getInternalServerErrorResponse(e);
		} catch (Throwable t) {
			response = getInternalServerErrorResponse(t);
		}

		logResponse(response);

		return response;
	}
}
