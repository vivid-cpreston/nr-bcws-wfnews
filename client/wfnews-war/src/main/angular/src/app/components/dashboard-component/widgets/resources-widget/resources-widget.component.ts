import { AfterViewInit, Component } from "@angular/core"
import { PublishedIncidentService } from "@app/services/published-incident-service"
import { isMobileView } from "@app/utils"

@Component({
  selector: 'resources-widget',
  templateUrl: './resources-widget.component.html',
  styleUrls: ['./resources-widget.component.scss']
})
export class ResourcesWidget implements AfterViewInit {
  public startupComplete = false
  public situationReport

  public isMobileView = isMobileView

  constructor(private publishedIncidentService: PublishedIncidentService) { }

  ngAfterViewInit (): void {
    this.publishedIncidentService.fetchSituationReportList(0, 10, true, true).toPromise()
    .then(sitrep => {
      if (sitrep?.collection?.length > 0) {
        const validReports = sitrep.collection.filter(r => r.publishedInd && !r.archivedInd)
        validReports.sort((a,b) =>(a.createdTimestamp > b.createdTimestamp) ? 1 : ((b.createdTimestamp > a.createdTimestamp) ? -1 : 0))
        this.situationReport = validReports[validReports.length - 1]
      }

      this.startupComplete = true
    })
  }
}
