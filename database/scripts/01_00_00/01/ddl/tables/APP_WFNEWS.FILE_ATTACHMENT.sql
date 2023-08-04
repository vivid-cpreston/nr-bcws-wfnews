/* ---------------------------------------------------- */
/*  Generated by Enterprise Architect Version 12.0    */
/*  Created On : 24-Jun-2022 11:01:59 AM        */
/*  DBMS       : PostgreSQL             */
/* ---------------------------------------------------- */

/* Create Tables */

CREATE TABLE "wfnews"."file_attachment"
(
  "file_attachment_guid" VARCHAR(36) NOT NULL,    -- FILE_ATTACHMENT_GUID is a unique identifier for the record.
  "source_object_name_code" varchar(10)  NOT NULL,    -- SOURCE_OBJECT_NAME_CODE: Is a foreign key to SOURCE_OBJECT_NAME_CODE: Source Object Name Code is a table to hold the name of the source object class that the attachment belongs to. Visually, the source object class is usually exposed via a user interface screen, so you can think of attachments being associated to a screen.  For instance, when an incident photo is attached to an incident, the code value in this table will be Incident, and the attachment would be viewable in the UI via the Incident page.   Current values: * Article
  "source_object_unique_id" varchar(50)  NOT NULL,    -- Source Object Unique Identifier is the actual primary key value of the source object that owns the attachment. This, along with the Source Object Name will fully identify what the attachment belongs to. With including future source object types, we don't know if the ID of future objects is going to be of type number, string or some other data type. In order to accommodate any date type, this column is of type string.
  "document_path" varchar(2000)  NOT NULL,    -- Document Path is the full path to where the attachment document is located. Currently, this will be a full URL on a server including the document name and file extension.
  "file_identifier" varchar(256)   NULL,    -- File Identifier is the Identifier of a file stored in the Document Management System.
  "file_attachment_type_code" varchar(10)  NOT NULL,    -- FILE_ATTACHMENT_TYPE_CODE: Is a foreign key to FILE_ATTACHMENT_TYPE_CODE: File Attachment Type Code describes the types of attachments supported by the application.  The current values are:
  "attachment_title" varchar(200)  NULL,    -- Attachment Title is a display quality title for the file attachment. A file attachment may have an esoteric file name, such as file names for photos. Since the original file name needs to be preserved, it is necessary to be able to assign a title to a file for display purposes.
  "attachment_description" varchar(150)  NULL,    -- Attachment Description is a short description of the attachment, or a user friendly name for the attachment. This is to help users understand in a user friendly manner what the attachment is, rather than relying on the document URL.
  "attachment_read_only_ind" varchar(1)  NOT NULL DEFAULT 'N',    -- Attachment Read Only Ind indicates whether the wildfire attachment file is read only (Y) or not (N). If it is not read only it can be deleted and overwritten.
  "mime_type" varchar(300)   NULL,    -- Mime Type is a descriptor that describes the content type of the file.
  "private_ind" varchar(1)   NOT NULL DEFAULT 'N',    -- Private Ind indicates whether the file attachment is deemed private, and contains confidential information. For example, some incident photos may be tagged as private and distribution will be managed accordingly.
  "published_ind" varchar(1)   NOT NULL DEFAULT 'N',    -- Published Ind indicates whether the file attachment is published (Y) or not (N).
  "archived_ind" varchar(1)  NOT NULL DEFAULT 'N',    -- Archived Ind indicates whether the attachment is archived (Y) or not (N).
  "created_timestamp" timestamp NOT NULL DEFAULT DATE_TRUNC('day', current_date ),    -- Created Timestamp is the date and time the record was created.
  "uploaded_by_user_type" varchar(3)   NULL,    -- Uploaded By User Type corresponds user types stored in the WebADE User_Type_Code table, which has values BC Services Card, Government, Business Partner, Unverified Individual, Verified, Individual, Service Client.
  "uploaded_by_userid" varchar(100)  NULL,    -- Uploaded By Userid is the IDIR or BCEID userid of the person that uploaded the attachment.
  "uploaded_by_user_guid" varchar(32)  NULL,    -- Uploaded By User Guid is the user GUID of the person that uploaded the attachment.  The user guid is stored in this field corresponds to a user authorized in the WebADE platform.
  "uploaded_by_timestamp" timestamp NULL,    -- Uploaded By Timestamp is the date and time the attachment was uploaded.
  "revision_count" decimal(10) NOT NULL DEFAULT 0,    -- REVISION_COUNT is the number of times that the row of data has been changed. The column is used for optimistic locking via application code.
  "create_user" varchar(64)  NOT NULL,    -- CREATE_USER is an audit column that indicates the user that created the record.
  "create_date" timestamp NOT NULL DEFAULT current_date,    -- CREATE_DATE is the date and time the row of data was created.
  "update_user" varchar(64)  NOT NULL,    -- UPDATE_USER is an audit column that indicates the user that updated the record.
  "update_date" timestamp NOT NULL DEFAULT current_date    -- UPDATE_DATE is the date and time the row of data was updated.
)
;

/* Create Table Comments, Sequences for Autonumber Columns */

COMMENT ON TABLE "wfnews"."file_attachment"
  IS 'File Attachment is used to track file attachments for the wildfire application.   The File Attachment  table allows for attachments to be linked to various business objects in the system by mapping a file attachment to the ID of the Source Object and the Source Object Name Code.   This table will hold a document path, a foreign key to the Source Object Name table and an ID of the Source Object that owns the attachment. This will bring together the document path with the object that owns it and the table where the object is located.'
;

COMMENT ON COLUMN "wfnews"."file_attachment"."file_attachment_guid"
  IS 'FILE_ATTACHMENT_GUID is a unique identifier for the record.'
;

COMMENT ON COLUMN "wfnews"."file_attachment"."source_object_name_code"
  IS 'SOURCE_OBJECT_NAME_CODE: Is a foreign key to SOURCE_OBJECT_NAME_CODE: Source Object Name Code is a table to hold the name of the source object class that the attachment belongs to. Visually, the source object class is usually exposed via a user interface screen, so you can think of attachments being associated to a screen.  For instance, when an incident photo is attached to an incident, the code value in this table will be Incident, and the attachment would be viewable in the UI via the Incident page.   Current values: * Article'
;

COMMENT ON COLUMN "wfnews"."file_attachment"."source_object_unique_id"
  IS 'Source Object Unique Identifier is the actual primary key value of the source object that owns the attachment. This, along with the Source Object Name will fully identify what the attachment belongs to. With including future source object types, we don''t know if the ID of future objects is going to be of type number, string or some other data type. In order to accommodate any date type, this column is of type string.'
;

COMMENT ON COLUMN "wfnews"."file_attachment"."document_path"
  IS 'Document Path is the full path to where the attachment document is located. Currently, this will be a full URL on a server including the document name and file extension.'
;

COMMENT ON COLUMN "wfnews"."file_attachment"."file_identifier"
  IS 'File Identifier is the Identifier of a file stored in the Document Management System.'
;

COMMENT ON COLUMN "wfnews"."file_attachment"."file_attachment_type_code"
  IS 'FILE_ATTACHMENT_TYPE_CODE: Is a foreign key to FILE_ATTACHMENT_TYPE_CODE: File Attachment Type Code describes the types of attachments supported by the application.  The current values are:'
;

COMMENT ON COLUMN "wfnews"."file_attachment"."attachment_title"
  IS 'Attachment Title is a display quality title for the file attachment. A file attachment may have an esoteric file name, such as file names for photos. Since the original file name needs to be preserved, it is necessary to be able to assign a title to a file for display purposes.'
;

COMMENT ON COLUMN "wfnews"."file_attachment"."attachment_description"
  IS 'Attachment Description is a short description of the attachment, or a user friendly name for the attachment. This is to help users understand in a user friendly manner what the attachment is, rather than relying on the document URL.'
;

COMMENT ON COLUMN "wfnews"."file_attachment"."attachment_read_only_ind"
  IS 'Attachment Read Only Ind indicates whether the wildfire attachment file is read only (Y) or not (N). If it is not read only it can be deleted and overwritten.'
;

COMMENT ON COLUMN "wfnews"."file_attachment"."archived_ind"
  IS 'Archived Ind indicates whether the attachment is archived (Y) or not (N).'
;

COMMENT ON COLUMN "wfnews"."file_attachment"."published_ind" 
  IS 'Published Ind indicates whether the file attachment is published (Y) or not (N).   File attachments may be attached to a Source Object by a staff person, but not published until the source object has been deemed complete. It is a method to ensure attachments are not shown in an application to general users prematurely.'
;


COMMENT ON COLUMN "wfnews"."file_attachment"."mime_type"
  IS 'Mime Type is a descriptor that describes the content type of the file.'
;

COMMENT ON COLUMN "wfnews"."file_attachment"."private_ind"
  IS 'Private Ind indicates whether the file attachment is deemed private, and contains confidential information. For example, some incident photos may be tagged as private and distribution will be managed accordingly.'
;

COMMENT ON COLUMN "wfnews"."file_attachment"."created_timestamp"
  IS 'Created Timestamp is the date and time the record was created.'
;

COMMENT ON COLUMN "wfnews"."file_attachment"."uploaded_by_user_type"
  IS 'Uploaded By User Type corresponds user types stored in the WebADE User_Type_Code table, which has values BC Services Card, Government, Business Partner, Unverified Individual, Verified, Individual, Service Client.'
;

COMMENT ON COLUMN "wfnews"."file_attachment"."uploaded_by_userid"
  IS 'Uploaded By Userid is the IDIR or BCEID userid of the person that uploaded the attachment.'
;

COMMENT ON COLUMN "wfnews"."file_attachment"."uploaded_by_user_guid"
  IS 'Uploaded By User Guid is the user GUID of the person that uploaded the attachment.  The user guid is stored in this field corresponds to a user authorized in the WebADE platform.'
;

COMMENT ON COLUMN "wfnews"."file_attachment"."uploaded_by_timestamp"
  IS 'Uploaded By Timestamp is the date and time the attachment was uploaded.'
;

COMMENT ON COLUMN "wfnews"."file_attachment"."revision_count"
  IS 'REVISION_COUNT is the number of times that the row of data has been changed. The column is used for optimistic locking via application code.'
;

COMMENT ON COLUMN "wfnews"."file_attachment"."create_user"
  IS 'CREATE_USER is an audit column that indicates the user that created the record.'
;

COMMENT ON COLUMN "wfnews"."file_attachment"."create_date"
  IS 'CREATE_DATE is the date and time the row of data was created.'
;

COMMENT ON COLUMN "wfnews"."file_attachment"."update_user"
  IS 'UPDATE_USER is an audit column that indicates the user that updated the record.'
;

COMMENT ON COLUMN "wfnews"."file_attachment"."update_date"
  IS 'UPDATE_DATE is the date and time the row of data was updated.'
;

/* Create Primary Keys, Indexes, Uniques, Checks */

ALTER TABLE "wfnews"."file_attachment" ADD CONSTRAINT "flattch_pk"
  PRIMARY KEY ("file_attachment_guid")
;

ALTER TABLE "wfnews"."file_attachment" ADD CONSTRAINT "flattch_uk" UNIQUE ("source_object_name_code","source_object_unique_id","document_path","file_identifier")
;

CREATE INDEX "flattch_fatcd_idx" ON "wfnews"."file_attachment" ("file_attachment_type_code" ASC)
;

CREATE INDEX "flattch_soncd_idx" ON "wfnews"."file_attachment" ("source_object_name_code" ASC)
;

/* Create Foreign Key Constraints */

ALTER TABLE "wfnews"."file_attachment" ADD CONSTRAINT "flattch_fatcd_fk"
  FOREIGN KEY ("file_attachment_type_code") REFERENCES "wfnews"."file_attachment_type_code" ("file_attachment_type_code") ON DELETE No Action ON UPDATE No Action
;

ALTER TABLE "wfnews"."file_attachment" ADD CONSTRAINT "flattch_soncd_fk"
  FOREIGN KEY ("source_object_name_code") REFERENCES "wfnews"."source_object_name_code" ("source_object_name_code") ON DELETE No Action ON UPDATE No Action
;