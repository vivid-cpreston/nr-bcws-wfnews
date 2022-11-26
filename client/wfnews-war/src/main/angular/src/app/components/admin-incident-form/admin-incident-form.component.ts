import { ChangeDetectorRef,  Directive, Input, OnChanges, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { IncidentCauseResource, WildfireIncidentResource, PublishedIncidentResource } from '@wf1/incidents-rest-api';
import * as Editor from '@ckeditor/ckeditor5-build-decoupled-document';
import { CustomImageUploader } from './incident-details-panel/custom-uploader';
import { RootState } from '../../store';
import { Store } from '@ngrx/store';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PublishDialogComponent } from './publish-dialog/publish-dialog.component';
import { PublishedIncidentService } from '../../services/published-incident-service';
import { IncidentDetailsPanel } from './incident-details-panel/incident-details-panel.component';
import { getIncident } from '../../store/incident/incident.action';
import { ContactsDetailsPanel } from './contacts-details-panel/contacts-details-panel.component';
import { HttpClient } from '@angular/common/http';

@Directive()
export class AdminIncidentForm implements OnInit, OnChanges {
  // This is a stub used for testing purposes only
  // when an actual resource model is in place, use that
  // and load from the store/api
  @Input() adminIncident: any;
  @Input() adminIncidentCause: any;
  @ViewChild('detailsPanelComponent') detailsPanelComponent: IncidentDetailsPanel;
  @ViewChild('ContactDetailsPanel') contactDetailsPanelComponent: ContactsDetailsPanel;

  public Editor = Editor;

  // TODO: Remove the default values here.

  public incident = {
    fireNumber: 0,
    wildfireYear: new Date().getFullYear(),
    incidentNumberSequence: 0,
    fireName: undefined,
    traditionalTerritory: undefined,
    lastPublished: undefined,
    publishedStatus: 'DRAFT',
    fireOfNote: false,
    location: undefined,
    sizeType: 1,
    sizeHectares: 0,
    sizeComments: undefined,
    cause: 0,
    stageOfControlCode: undefined,
    causeComments: undefined,
    responseComments: undefined,
    wildifreCrewsInd: false,
    crewsComments: undefined,
    aviationInd: false,
    aviationComments: undefined,
    incidentManagementInd: false,
    incidentManagementComments: undefined,
    heavyEquipmentInd: false,
    heavyEquipmentComments: undefined,
    structureProtectionInd: false,
    structureProtectionComments: undefined,
    contact: {
      isPrimary: true,
      fireCentre: null,
      phoneNumber: null,
      emailAddress: null
    },
    geometry: {
      x: -115,
      y: 50
    },
    incidentOverview: '',
    evacOrders: [],
    mapAttachments: [],
    incidentData: null
  }

  wildFireYear: string;
  incidentNumberSequnce: string;
  currentAdminIncident: WildfireIncidentResource;
  currentAdminIncidentCause: IncidentCauseResource;
  publishedIncidentType: string;
  publishedIncidentDetailGuid: string;

  private loaded = false;

  public readonly incidentForm: FormGroup

  constructor(private readonly formBuilder: FormBuilder,
              private router: ActivatedRoute,
              private componentRouter: Router,
              protected cdr: ChangeDetectorRef,
              protected dialog: MatDialog,
              private publishedIncidentService: PublishedIncidentService,
              protected snackbarService: MatSnackBar,
              protected http: HttpClient) {
    this.incidentForm = this.formBuilder.group({
      fireName: [],
      incidentNumberSequence: [],
      incidentLocation:[],
      traditionalTerritory: [],
      lastPublished: [],
      publishedStatus: [],
      fireOfNote: [],
      location: [],
      sizeType: [],
      sizeHectares: [],
      incidentSituation: [],
      sizeComments: [],
      cause: [],
      causeComments: [],
      responseComments:[],
      wildifreCrewsInd: [],
      crewsComments: [],
      aviationInd: [],
      aviationComments: [],
      incidentManagementInd: [],
      incidentManagementComments: [],
      heavyEquipmentInd: [],
      heavyEquipmentComments: [],
      structureProtectionInd: [],
      structureProtectionComments: [],
      contact: this.formBuilder.group({
        fireCentre: [],
        phoneNumber: [],
        emailAddress: []
      }),
      evacOrders: this.formBuilder.array([])
    })
  }

  ngOnInit() {
    this.router.queryParams.subscribe(
      (params:ParamMap) => {
        if (params && params['wildFireYear'] && params['incidentNumberSequence']) {
          this.wildFireYear = params['wildFireYear'];
          this.incidentNumberSequnce = params['incidentNumberSequence']

          const self = this;

          this.publishedIncidentService.fetchIMIncident(this.wildFireYear, this.incidentNumberSequnce).subscribe(incidentResponse => {
            console.log('Loading incicent...', incidentResponse)
            self.currentAdminIncident = incidentResponse.response;
            this.publishedIncidentType = self.currentAdminIncident.type;
            self.incident.incidentData = self.currentAdminIncident
            self.incident.fireNumber = self.currentAdminIncident.incidentNumberSequence;
            self.incident.wildfireYear = self.currentAdminIncident.wildfireYear;
            self.incident.fireOfNote = self.currentAdminIncident.fireOfNotePublishedInd;
            self.incident.incidentNumberSequence= self.currentAdminIncident.incidentNumberSequence;
            self.incident.fireName = self.currentAdminIncident.incidentName || self.currentAdminIncident.incidentLabel;
            self.incident.publishedStatus = 'DRAFT';
            self.incident.location = self.currentAdminIncident.incidentLocation.geographicDescription;

            self.incident.sizeType = 0
            self.incident.sizeHectares = self.currentAdminIncident.incidentSituation.fireSizeHectares

            const causeCode = self.currentAdminIncident.suspectedCauseCategoryCode === 'Undetermined' ? 3 : self.currentAdminIncident.suspectedCauseCategoryCode === 'Lightning' ? 2 : 1
            self.incident.cause = causeCode
            self.detailsPanelComponent.setCauseDisclaimer(causeCode)
            self.incident.causeComments = self.detailsPanelComponent.causeOptions.find(c => c.id === causeCode).disclaimer

            self.incident.contact.isPrimary = true;

            self.incident.contact.fireCentre = self.currentAdminIncident.fireCentreOrgUnitIdentifier

            let mappedCentre = 6
            if (self.incident.contact.fireCentre == 50) mappedCentre = 2
            else if (self.incident.contact.fireCentre == 42) mappedCentre = 3
            else if (self.incident.contact.fireCentre == 34) mappedCentre = 4
            else if (self.incident.contact.fireCentre == 25) mappedCentre = 5
            else if (self.incident.contact.fireCentre == 2) mappedCentre = 7

            self.incident.contact.fireCentre = '' + mappedCentre

            this.http.get('../../../../assets/data/fire-center-contacts.json').subscribe(data => {
              self.incident.contact.phoneNumber = data[mappedCentre].phone
              self.incident.contact.emailAddress = data[mappedCentre].url
              this.incidentForm.patchValue(this.incident);
              this.cdr.detectChanges();
            });

            incidentResponse.getPublishedIncident.subscribe((response) => {
              console.log('Loading Published data...', response)
              self.publishedIncidentDetailGuid = response.publishedIncidentDetailGuid;
              self.incident.traditionalTerritory = response.traditionalTerritoryDetail;
              self.incident.lastPublished = response.publishedTimestamp;
              self.incident.location = response.incidentLocation;

              self.incident.sizeComments = response.incidentSizeDetail;
              self.incident.causeComments = response.incidentCauseDetail;

              self.incident.publishedStatus = response.newsPublicationStatusCode;
              self.incident.responseComments = self.currentAdminIncident.responseObjectiveDescription;

              self.incident.wildifreCrewsInd = response.wildfireCrewResourcesInd;
              self.incident.crewsComments = response.wildfireCrewResourcesDetail;

              self.incident.aviationInd = response.wildfireAviationResourceInd;
              self.incident.aviationComments = response.wildfireAviationResourceDetail;

              self.incident.incidentManagementInd = response.incidentMgmtCrewRsrcInd;
              self.incident.incidentManagementComments = response.incidentMgmtCrewRsrcDetail;
              self.incident.heavyEquipmentInd = response.heavyEquipmentResourcesInd;
              self.incident.heavyEquipmentComments = response.heavyEquipmentResourcesDetail;
              self.incident.structureProtectionInd = response.structureProtectionRsrcInd;
              self.incident.structureProtectionComments = response.structureProtectionRsrcDetail;

              self.incident.contact.fireCentre = response.contactOrgUnitIdentifer;
              self.incident.contact.phoneNumber = response.contactPhoneNumber;
              self.incident.contact.emailAddress = response.contactEmailAddress;
              self.incident.incidentOverview = response.incidentOverview;
            }, (error) => {
              console.log('No published data found...')
              console.error(error)
              self.publishedIncidentDetailGuid = null;
            })

            this.incidentForm.patchValue(this.incident);
            this.cdr.detectChanges();
          },
          (incidentResponseError) => {
            console.error(incidentResponseError)
            this.snackbarService.open('Failed to fetch Incident: ' + JSON.stringify(incidentResponseError), 'OK', { duration: 10000, panelClass: 'snackbar-error' });
          });
        }
      }
    )
  }

  ngOnChanges(changes: SimpleChanges) {
    // TODO: This can be removed once the onInit is updated to map the form correctly
  }

  publishChanges () {
    const self = this;
    let dialogRef = this.dialog.open(PublishDialogComponent, {
      width: '350px',
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result && result.publish) {
        const publishedIncidentResource = {
          publishedIncidentDetailGuid: this.publishedIncidentDetailGuid,
          incidentGuid: this.currentAdminIncident['wildfireIncidentGuid'],
          newsCreatedTimestamp: new Date().valueOf().toString(),
          discoveryDate: new Date().valueOf().toString(),
          newsPublicationStatusCode: 'PUBLISHED',
          fireOfNoteInd: this.incident.fireOfNote,
          incidentName: this.incident.fireName,
          incidentLocation: this.incident.location,
          incidentOverview: this.incident.incidentOverview,
          traditionalTerritoryDetail: this.incident.traditionalTerritory,
          incidentSizeDetail: this.incident.sizeComments,
          incidentCauseDetail: this.incident.causeComments,
          contactOrgUnitIdentifer: this.incident.contact.fireCentre,
          contactPhoneNumber: this.incident.contact.phoneNumber,
          contactEmailAddress: this.incident.contact.emailAddress,
          resourceDetail: this.incident.responseComments,
          wildfireCrewResourcesInd: this.incident.wildifreCrewsInd,
          wildfireCrewResourcesDetail: this.incident.crewsComments,
          wildfireAviationResourceInd: this.incident.aviationInd,
          wildfireAviationResourceDetail: this.incident.aviationComments,
          heavyEquipmentResourcesInd: this.incident.heavyEquipmentInd,
          heavyEquipmentResourcesDetail: this.incident.heavyEquipmentComments,
          incidentMgmtCrewRsrcInd: this.incident.incidentManagementInd,
          incidentMgmtCrewRsrcDetail: this.incident.incidentManagementComments,
          structureProtectionRsrcInd: this.incident.structureProtectionInd,
          structureProtectionRsrcDetail: this.incident.structureProtectionComments,
          type: this.publishedIncidentType,
          '@type' : 'http://wfim.nrs.gov.bc.ca/v1/publishedIncident'
        };

        self.publishIncident(publishedIncidentResource).then(doc => {
          this.snackbarService.open('Incident Published Successfully', 'OK', { duration: 100000, panelClass: 'snackbar-success-v2' });
          this.publishedIncidentDetailGuid = doc.publishedIncidentDetailGuid
        }).catch(err => {
            this.snackbarService.open('Failed to Publish Incident: ' + JSON.stringify(err.message), 'OK', { duration: 10000, panelClass: 'snackbar-error' });
          }).finally(() => {
            self.loaded = false;
            this.cdr.detectChanges();
          }).catch(err => {
            this.snackbarService.open('Failed to Publish Incident: ' + JSON.stringify(err.message), 'OK', { duration: 10000, panelClass: 'snackbar-error' });
          })
      }
    });
  }

  publishIncident(incident): Promise<any> {
    return this.publishedIncidentService.saveIMPublishedIncident(incident).toPromise();
  }

  onShowPreview() {
    let mappedIncident = {
      incidentGuid: this.currentAdminIncident['wildfireIncidentGuid'],
      incidentNumberLabel: this.currentAdminIncident.incidentLabel,
      stageOfControlCode: this.incident.incidentData.incidentStatusCode,
      generalIncidentCauseCatId: this.incidentForm.controls['cause'].value == 'Human' ? 1 : this.incidentForm.controls['cause'].value == 'Lightning' ? 2 : 3,
      discoveryDate: new Date(this.incident.incidentData.discoveryTimestamp).toString(),
      fireCentre: this.currentAdminIncident.fireCentreOrgUnitIdentifier,
      fireOfNoteInd: this.incidentForm.controls['fireOfNote'].value,
      incidentName: this.incidentForm.controls['fireName'].value,
      incidentLocation: this.incidentForm.controls['location'].value,
      incidentOverview: this.incident.incidentOverview,
      traditionalTerritoryDetail: this.incidentForm.controls['traditionalTerritory'].value,
      incidentSizeType: this.incidentForm.controls['sizeType'].value,
      incidentSizeEstimatedHa: this.incidentForm.controls['sizeHectares'].value,
      incidentSizeDetail: this.incidentForm.controls['sizeComments'].value,
      incidentCauseDetail: this.incidentForm.controls['causeComments'].value,
      contactOrgUnitIdentifer: this.currentAdminIncident.fireCentreOrgUnitIdentifier,
      contactPhoneNumber:  (this.incidentForm.controls['contact'] as FormGroup).controls['phoneNumber'].value,
      contactEmailAddress:  (this.incidentForm.controls['contact'] as FormGroup).controls['emailAddress'].value,
      wildfireCrewResourcesInd: this.incidentForm.controls['wildifreCrewsInd'].value,
      wildfireCrewResourcesDetail: this.incidentForm.controls['crewsComments'].value,
      wildfireAviationResourceInd: this.incidentForm.controls['aviationInd'].value,
      wildfireAviationResourceDetail: this.incidentForm.controls['aviationComments'].value,
      heavyEquipmentResourcesInd: this.incidentForm.controls['heavyEquipmentInd'].value,
      heavyEquipmentResourcesDetail: this.incidentForm.controls['heavyEquipmentComments'].value,
      incidentMgmtCrewRsrcInd: this.incidentForm.controls['incidentManagementInd'].value,
      incidentMgmtCrewRsrcDetail: this.incidentForm.controls['incidentManagementComments'].value,
      structureProtectionRsrcInd: this.incidentForm.controls['structureProtectionInd'].value,
      structureProtectionRsrcDetail: this.incidentForm.controls['structureProtectionComments'].value,
      lastUpdatedTimestamp: new Date(this.incident.incidentData.lastUpdatedTimestamp).toString(),
      latitude: this.incident.incidentData.incidentLocation.latitude,
      longitude: this.incident.incidentData.incidentLocation.longitude,
      fireYear: this.incident.wildfireYear,
      resourceDetail: this.incidentForm.controls['responseComments'].value,
    }

    if (localStorage.getItem('preview_incident') != null) {
      localStorage.removeItem('preview_incident');
    }

    localStorage.setItem('preview_incident', JSON.stringify(mappedIncident));

    const url = this.componentRouter.serializeUrl(
      this.componentRouter.createUrlTree(['incidents'], { queryParams: { preview: true } })
    );

    window.open(url, '_blank');
  }

  // for decoupled editor
  public onReady( editor ) {
    editor.ui.getEditableElement().parentElement.insertBefore(
        editor.ui.view.toolbar.element,
        editor.ui.getEditableElement()
    );

    editor.plugins.get( 'FileRepository' ).createUploadAdapter = (loader) => {
      return new CustomImageUploader( loader )
    }
  }
}
