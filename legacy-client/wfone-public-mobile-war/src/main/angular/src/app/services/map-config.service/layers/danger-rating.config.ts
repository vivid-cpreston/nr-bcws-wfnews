import { AppResourcesConfig } from "../../app-config.service";

export function DangerRatingLayerConfig( res: AppResourcesConfig ) {
	return [
    {
      "type": "wms",
      "id": "danger-rating",
      "title": "BC Wildfire Danger Rating",
      "serviceUrl": res.openmapsBaseUrl,
      // "serviceUrl": "https://delivery.openmaps.gov.bc.ca/geo/pub/ows",
      // "#serviceUrl": "https://openmaps.gov.bc.ca/geo/pub/ows",
      "layerName": "pub:WHSE_LAND_AND_NATURAL_RESOURCE.PROT_DANGER_RATING_SP",
      "styleName": "7734",
      "isQueryable": true,
      "popupTemplate": "@wf-feature",
      "titleAttribute": "DANGER_RATING_DESC",
      "geometryAttribute": "SHAPE",
      "attributes": [
          {
              "name": "DANGER_RATING_DESC",
              "title": "Danger Rating"
          }
      ]
    }
  ]
}