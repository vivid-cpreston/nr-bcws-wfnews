import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'response-details-panel',
  templateUrl: './response-details-panel.component.html',
  styleUrls: ['./response-details-panel.component.scss']
})
export class ResponseDetailsPanel implements OnInit {
  @Input() public readonly formGroup: FormGroup;
  @Input() public incident;

  @ViewChild("initialAttackCrews") initialAttackCrews: ElementRef;
  @ViewChild("unitCrews") unitCrews: ElementRef;
  @ViewChild("helicopters") helicopters: ElementRef;
  @ViewChild("airtankers") airtankers: ElementRef;
  @ViewChild("pieces") pieces: ElementRef;
  @ViewChild("structure") structure: ElementRef;
 
  responseDisclaimer: string = `The BC Wildfire Service relies on thousands of people each year to respond to wildfires. This includes firefighters, air crew, equipment operators, and support staff. For more information on resources assigned to this incident, please contact the information officer listed for this incident.`;
  incidentManagementComments: string = `An Incident Management Team has been assigned to this wildfire.`;
  
  ngOnInit() {
    this.formGroup.controls['responseComments'].setValue(this.responseDisclaimer);
  }

  onWildfireCrewsChecked(event) {
    if(event.checked) {
      this.formGroup.controls["crewsComments"].setValue(this.crewCommentsValue(this.initialAttackCrews.nativeElement.value, this.unitCrews.nativeElement.value));
    } else {
      this.formGroup.controls["crewsComments"].setValue("");
    }
  }

  onAviationChecked(event) {
    if(event.checked) {
      this.formGroup.controls["aviationComments"].setValue(this.aviationCommentsValue(this.helicopters.nativeElement.value, this.airtankers.nativeElement.value));
    } else {
      this.formGroup.controls["aviationComments"].setValue("");
    }
  }

  onIncidentManagementTeamsChecked(event) {
    if(event.checked) {
      this.formGroup.controls["incidentManagementComments"].setValue(this.incidentManagementComments);
    } else {
      this.formGroup.controls["incidentManagementComments"].setValue("");
    }
  }

  onHeavyEquipmentChecked(event) {
    if(event.checked) {
      this.formGroup.controls["heavyEquipmentComments"].setValue(this.heavyEquipmentCommentsValue(this.pieces.nativeElement.value));
    } else {
      this.formGroup.controls["heavyEquipmentComments"].setValue("");
    }
  }

  onStructureProtectionChecked(event) {
    if(event.checked) {
      this.formGroup.controls["structureProtectionComments"].setValue(this.structureProtectionCommentsValue(this.structure.nativeElement.value));
    } else {
      this.formGroup.controls["structureProtectionComments"].setValue("");
    }
  }
  
  crewCommentsValue(initialAttack, unityCrews) {
    return `There are currently ${initialAttack} Initial Attack and ${unityCrews} Unit Crews responding to this wildfire.`;
  }

  aviationCommentsValue(helicopters, airtankers) {
    return `There are currently ${helicopters} helicopters and ${airtankers} airtankers responding to this wildfire.`;
  }

  heavyEquipmentCommentsValue(pieces) {
    return `There are currently ${pieces} pieces of heavy equipment responding to this wildfire.`;
  }

  structureProtectionCommentsValue(structure) {
    return `There are currently ${structure} structure protection units responding to this incident.`;
  }
}
