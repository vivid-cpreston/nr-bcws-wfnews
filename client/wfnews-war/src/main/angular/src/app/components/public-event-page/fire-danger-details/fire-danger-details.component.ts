import { Component } from '@angular/core';
import { RelatedTopicsLink } from '@app/components/full-details/cards/related-topics-card/related-topics-card.component';

@Component({
  // eslint-disable-next-line @angular-eslint/component-selector
  selector: 'fire-danger-details',
  templateUrl: './fire-danger-details.component.html',
  styleUrls: ['./fire-danger-details.component.scss']
})
export class FireDangerDetailsComponent {

  relatedTopicLinks: RelatedTopicsLink[] = [
    { 
      text: 'Fire Danger Rating', 
      url: 'https://www2.gov.bc.ca/gov/content/safety/wildfire-status/prepare/weather-fire-danger/fire-danger' 
    },
    { 
      text: 'Current Fire Bans and Restrictions', 
      url: 'https://www2.gov.bc.ca/gov/content/safety/wildfire-status/prevention/fire-bans-and-restrictions' 
    },
  ];
}
