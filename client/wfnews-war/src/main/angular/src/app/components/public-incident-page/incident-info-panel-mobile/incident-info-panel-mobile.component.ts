import { Component, OnInit } from '@angular/core';
import { IncidentInfoPanel } from '../incident-info-panel/incident-info-panel.component';
import { ResourcesRoutes, convertToDateYear } from '@app/utils';

@Component({
  selector: 'incident-info-panel-mobile',
  templateUrl: './incident-info-panel-mobile.component.html',
  styleUrls: ['./incident-info-panel-mobile.component.scss']
})
export class IncidentInfoPanelMobileComponent extends IncidentInfoPanel implements OnInit {
  mobileEvacOrders = [];
  mobileEvacAlerts = [];
  convertToDateYear = convertToDateYear;

  ngOnInit(): void {
    this.populateOrdersAndAlerts()
  }

  populateOrdersAndAlerts() {
    if (this.evacOrders) {
      for (const evac of this.evacOrders) {
        if (evac.orderAlertStatus === 'Order') this.mobileEvacOrders.push(evac)
        else if (evac.orderAlertStatus === 'Alert') this.mobileEvacAlerts.push(evac)
        else console.error('Could not determine orderAlertStatus for mobile evacuations')
      }
    }

  }

  navigateToMap() {
    this.route.navigate([ResourcesRoutes.ACTIVEWILDFIREMAP])
  }

  navigateToEvac(evac) {
    let type = null;
    if (evac.orderAlertStatus=== 'Alert') type = "evac-alert";
    else if (evac.orderAlertStatus  === 'Order') type = "evac-order";
    this.route.navigate([ResourcesRoutes.FULL_DETAILS], { queryParams: { type: type, id: evac.emrgOAAsysID, source: [ResourcesRoutes.PUBLIC_INCIDENT] } });
  }

  navigateToAreaRestriction(area) {
    if (area && area.protRsSysID) 
      this.route.navigate([ResourcesRoutes.FULL_DETAILS], { queryParams: { type: 'area-restriction', id: area.protRsSysID, source: [ResourcesRoutes.PUBLIC_INCIDENT] }});
  }

  scrollToSection(sectionId) {
    const section = document.getElementById(sectionId);
    if (section) section.scrollIntoView({ behavior: 'smooth' });
  }

}
