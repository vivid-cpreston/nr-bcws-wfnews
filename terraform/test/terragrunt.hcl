terraform {
  source = ".."
}

include {
  path = find_in_parent_folders()
}

locals {
  sec_group = "Web_sg"
  github_release_name = get_env("GITHUB_RELEASE_NAME")
  db_pass = get_env("DB_PASS")
  db_size = get_env("DB_SIZE")
  db_postgres_version = get_env("DB_POSTGRES_VERSION")
  server_image = get_env("SERVER_IMAGE")
  client_image = get_env("CLIENT_IMAGE")
  liquibase_image = get_env("LIQUIBASE_IMAGE")
  nginx_image = get_env("NGINX_IMAGE")
  pointid_image = get_env("POINTID_IMAGE")

  client_cpu_units = get_env("CLIENT_CPU_UNITS")
  client_memory = get_env("CLIENT_MEMORY")
  server_cpu_units = get_env("SERVER_CPU_UNITS")
  server_memory = get_env("SERVER_MEMORY")
  db_instance_type = get_env("DB_INSTANCE_TYPE")
  app_count = get_env("INSTANCE_COUNT")

  logging_level = get_env("LOGGING_LEVEL")

  api_key = get_env("API_KEY")
  target_env = get_env("TARGET_ENV")
  alb_name = get_env("ALB_NAME")
  vpc_name = get_env("VPC_NAME")
  subnet_filter = get_env("SUBNET_FILTER")
  license_plate = get_env("TFC_PROJECT")
  sns_email_targets = get_env("SNS_EMAIL_TARGETS")
  cloudfront_header = get_env("CLOUDFRONT_HEADER")
  #server env vars
  WEBADE-OAUTH2_TOKEN_CLIENT_URL = get_env("WEBADE-OAUTH2_TOKEN_CLIENT_URL")
  YOUTUBE_API_KEY = get_env("YOUTUBE_API_KEY")
  YOUTUBE_CHANNEL_ID = get_env("YOUTUBE_CHANNEL_ID")
  WEBADE-OAUTH2_TOKEN_URL = get_env("WEBADE-OAUTH2_TOKEN_URL")
  WEBADE_OAUTH2_WFNEWS_REST_CLIENT_SECRET = get_env("WEBADE_OAUTH2_WFNEWS_REST_CLIENT_SECRET")
  WFDM_REST_URL = get_env("WFDM_REST_URL")
  FIRE_REPORT_API_URL = get_env("FIRE_REPORT_API_URL")
  NOTIFICATION_API_URL = get_env("NOTIFICATION_API_URL")
  POINT_ID_URL = get_env("POINT_ID_URL")
  WFIM_CLIENT_URL = get_env("WFIM_CLIENT_URL")
  WFIM_REST_URL = get_env("WFIM_REST_URL")
  WFIM_CODE_TABLES_URL = get_env("WFIM_CODE_TABLES_URL")
  WEBADE-OAUTH2_CHECK_TOKEN_URL = get_env("WEBADE-OAUTH2_CHECK_TOKEN_URL")
  WFNEWS_EMAIL_NOTIFICATIONS_ENABLED= get_env("WFNEWS_EMAIL_NOTIFICATIONS_ENABLED")
  SMTP_HOST_NAME = get_env("SMTP_HOST_NAME")
  SMTP_PASSWORD = get_env("SMTP_PASSWORD")
  SMTP_FROM_EMAIL = get_env("SMTP_FROM_EMAIL")
  SMTP_ADMIN_EMAIL = get_env("SMTP_ADMIN_EMAIL")
  SMTP_EMAIL_SYNC_ERROR_FREQ= get_env("SMTP_EMAIL_SYNC_ERROR_FREQ")
  SMTP_EMAIL_FREQ = get_env("SMTP_EMAIL_FREQ")
  SMTP_EMAIL_ERROR_SUBJECT = get_env("SMTP_EMAIL_ERROR_SUBJECT")
  SMTP_EMAIL_SUBJECT = get_env("SMTP_EMAIL_SUBJECT")
  DEFAULT_APPLICATION_ENVIRONMENT= get_env("DEFAULT_APPLICATION_ENVIRONMENT")
  WFNEWS_AGOL_QUERY_URL = get_env("WFNEWS_AGOL_QUERY_URL")
  WFNEWS_USERNAME = get_env("WFNEWS_USERNAME")
  WFNEWS_MAX_CONNECTIONS = get_env("WFNEWS_MAX_CONNECTIONS")
  WEBADE_OAUTH2_REST_CLIENT_ID = get_env("WEBADE_OAUTH2_REST_CLIENT_ID")
  WEBADE_OAUTH2_UI_CLIENT_ID = get_env("WEBADE_OAUTH2_UI_CLIENT_ID")
  WEBADE_OAUTH2_AUTHORIZE_URL = get_env("WEBADE_OAUTH2_AUTHORIZE_URL")
  #client-only env vars
  //Client-only variables
  agolUrl = get_env("agolUrl")
  drivebcBaseUrl = get_env("drivebcBaseUrl")
  openmapsBaseUrl = get_env("openmapsBaseUrl")
  siteMinderURLPrefix = get_env("siteMinderURLPrefix")
  syncIntervalMinutes = get_env("syncIntervalMinutes")
  agolAreaRestrictions = get_env("agolAreaRestrictions")
  agolBansAndProhibitions = get_env("agolBansAndProhibitions")
  agolDangerRatings = get_env("agolDangerRatings")
  WEBADE_OAUTH2_WFNEWS_UI_CLIENT_SECRET = get_env("WEBADE_OAUTH2_WFNEWS_UI_CLIENT_SECRET")
  MAX_RECEIVED_COUNT = get_env("MAX_RECEIVED_COUNT")
  VISIBILITY_TIMEOUT_SECONDS = get_env("VISIBILITY_TIMEOUT_SECONDS")
  ACCEPTED_IPS = get_env("ACCEPTED_IPS")
  PUSH_NOTIFICATION_AWS_USER = get_env("PUSH_NOTIFICATION_AWS_USER")
  EVENT_BRIDGE_ARN = get_env("EVENT_BRIDGE_ARN")
  SECRET_NAME = get_env("SECRET_NAME")
  BAN_PROHIBITION_MONITOR_KEY = get_env("BAN_PROHIBITION_MONITOR_KEY")
  ACTIVE_FIRE_MONITOR_KEY = get_env("ACTIVE_FIRE_MONITOR_KEY")
  AREA_RESTRICTIONS_MONITOR_KEY = get_env("AREA_RESTRICTIONS_MONITOR_KEY")
  EVACUATION_MONITOR_KEY = get_env("EVACUATION_MONITOR_KEY")
  LAMBDA_LAYER_KEY = get_env("LAMBDA_LAYER_KEY")
  WFONE_NOTIFICATIONS_API_IMAGE = get_env("WFONE_NOTIFICATIONS_API_IMAGE")

  # WFONE_NOTIFICATIONS_API
  WFONE_NOTIFICATIONS_API_DATASOURCE_MAX_CONNECTIONS = get_env("WFONE_NOTIFICATIONS_API_DATASOURCE_MAX_CONNECTIONS")
  WFONE_NOTIFICATIONS_API_DATASOURCE_USER = get_env("WFONE_NOTIFICATIONS_API_DATASOURCE_USER")
  WFONE_NOTIFICATIONS_API_EMAIL_ADMIN_EMAIL = get_env("WFONE_NOTIFICATIONS_API_EMAIL_ADMIN_EMAIL")
  WFONE_NOTIFICATIONS_API_EMAIL_FROM_EMAIL = get_env("WFONE_NOTIFICATIONS_API_EMAIL_FROM_EMAIL")
  WFONE_NOTIFICATIONS_API_EMAIL_NOTIFICATIONS_ENABLED = get_env("WFONE_NOTIFICATIONS_API_EMAIL_NOTIFICATIONS_ENABLED")
  WFONE_NOTIFICATIONS_API_EMAIL_SYNC_SEND_ERROR_FREQ = get_env("WFONE_NOTIFICATIONS_API_EMAIL_SYNC_SEND_ERROR_FREQ")
  WFONE_NOTIFICATIONS_API_EMAIL_SYNC_SEND_ERROR_SUBJECT = get_env("WFONE_NOTIFICATIONS_API_EMAIL_SYNC_SEND_ERROR_SUBJECT")
  WFONE_NOTIFICATIONS_API_EMAIL_SYNC_SEND_FREQ = get_env("WFONE_NOTIFICATIONS_API_EMAIL_SYNC_SEND_FREQ")
  WFONE_NOTIFICATIONS_API_EMAIL_SYNC_SUBJECT = get_env("WFONE_NOTIFICATIONS_API_EMAIL_SYNC_SUBJECT")
  WFONE_NOTIFICATIONS_API_PUSH_ITEM_EXPIRE_HOURS = get_env("WFONE_NOTIFICATIONS_API_PUSH_ITEM_EXPIRE_HOURS")
  WFONE_NOTIFICATIONS_API_QUARTZ_CONSUMER_INTERVAL_SECONDS = get_env("WFONE_NOTIFICATIONS_API_QUARTZ_CONSUMER_INTERVAL_SECONDS")
  WFONE_NOTIFICATIONS_API_SMTP_CREDENTIALS_PASSWORD = get_env("WFONE_NOTIFICATIONS_API_SMTP_CREDENTIALS_PASSWORD")
  WFONE_NOTIFICATIONS_API_SMTP_CREDENTIALS_USER = get_env("WFONE_NOTIFICATIONS_API_SMTP_CREDENTIALS_USER")
  WFONE_NOTIFICATIONS_API_SMTP_HOST_NAME = get_env("WFONE_NOTIFICATIONS_API_SMTP_HOST_NAME")
  WFONE_NOTIFICATIONS_API_WEBADE_OAUTH2_CLIENT_ID = get_env("WFONE_NOTIFICATIONS_API_WEBADE_OAUTH2_CLIENT_ID")
  WFONE_NOTIFICATIONS_API_WEBADE_OAUTH2_REST_CLIENT_SECRET = get_env("WFONE_NOTIFICATIONS_API_WEBADE_OAUTH2_REST_CLIENT_SECRET")
  WFONE_NOTIFICATIONS_API_WEBADE_OAUTH2_WFIM_CLIENT_ID = get_env("WFONE_NOTIFICATIONS_API_WEBADE_OAUTH2_WFIM_CLIENT_ID")
}

generate "test_tfvars" {
  path              = "terragrunt.auto.tfvars"
  if_exists         = "overwrite"
  disable_signature = true
  contents          = <<-EOF
    cloudfront = true
    cloudfront_origin_domain = "cfront_test.html"
    cloudfront_header = "${local.cloudfront_header}"
    app_image = "tomcat:jdk8-corretto"
    service_names = ["wfnews-project"]
    aws_sec_group = "App_sg"
    github_release_name = "${local.github_release_name}"
    target_env = "${local.target_env}"
    target_aws_account_id = "636235676406"
    server_image     = "${local.server_image}"
    client_image     = "${local.client_image}"
    liquibase_image     = "${local.liquibase_image}"
    nginx_image = "${local.nginx_image}"
    pointid_image = "${local.pointid_image}"
    client_cpu_units = "${local.client_cpu_units}"
    client_memory = "${local.client_memory}"
    server_cpu_units = "${local.server_cpu_units}"
    server_memory = "${local.server_memory}"
    db_instance_type = "${local.db_instance_type}"
    db_size = "${local.db_size}"
    logging_level = "${local.logging_level}"
    app_count = "${local.app_count}"
    api_key = "${local.api_key}"
    db_pass = "${local.db_pass}"
    db_multi_az = true
    db_postgres_version = "${local.db_postgres_version}"
    alb_name = "${local.alb_name}"
    client_port = 8080
    server_port=8080
    vpc_name = "${local.vpc_name}"
    subnet_filter = "${local.subnet_filter}"
    license_plate = "${local.license_plate}"
    sns_email_targets = "${local.sns_email_targets}"
    certificate_arn = "${get_env("BCWILDFIRE_CERT_ARN")}"
    base_certificate_arn = "${get_env("BASE_BCWILDFIRE_CERT_ARN")}"
    WEBADE-OAUTH2_TOKEN_CLIENT_URL = "${local.WEBADE-OAUTH2_TOKEN_CLIENT_URL}"
    YOUTUBE_API_KEY = "${local.YOUTUBE_API_KEY}"
    YOUTUBE_CHANNEL_ID = "${local.YOUTUBE_CHANNEL_ID}"
    WEBADE-OAUTH2_TOKEN_URL ="${local.WEBADE-OAUTH2_TOKEN_URL}"
    WEBADE_OAUTH2_WFNEWS_REST_CLIENT_SECRET ="${local.WEBADE_OAUTH2_WFNEWS_REST_CLIENT_SECRET}"
    WFDM_REST_URL ="${local.WFDM_REST_URL}"
    FIRE_REPORT_API_URL ="${local.FIRE_REPORT_API_URL}"
    NOTIFICATION_API_URL ="${local.NOTIFICATION_API_URL}"
    POINT_ID_URL ="${local.POINT_ID_URL}"
    WFIM_CLIENT_URL ="${local.WFIM_CLIENT_URL}"
    WFIM_REST_URL ="${local.WFIM_REST_URL}"
    WFIM_CODE_TABLES_URL ="${local.WFIM_CODE_TABLES_URL}"
    WEBADE-OAUTH2_CHECK_TOKEN_URL ="${local.WEBADE-OAUTH2_CHECK_TOKEN_URL}"
    WFNEWS_EMAIL_NOTIFICATIONS_ENABLED="${local.WFNEWS_EMAIL_NOTIFICATIONS_ENABLED}"
    SMTP_HOST_NAME ="${local.SMTP_HOST_NAME}"
    SMTP_PASSWORD ="${local.SMTP_PASSWORD}"
    SMTP_FROM_EMAIL ="${local.SMTP_FROM_EMAIL}"
    SMTP_ADMIN_EMAIL ="${local.SMTP_ADMIN_EMAIL}"
    SMTP_EMAIL_SYNC_ERROR_FREQ="${local.SMTP_EMAIL_SYNC_ERROR_FREQ}"
    SMTP_EMAIL_FREQ ="${local.SMTP_EMAIL_FREQ}"
    SMTP_EMAIL_ERROR_SUBJECT ="${local.SMTP_EMAIL_ERROR_SUBJECT}"
    SMTP_EMAIL_SUBJECT ="${local.SMTP_EMAIL_SUBJECT}"
    DEFAULT_APPLICATION_ENVIRONMENT="${local.DEFAULT_APPLICATION_ENVIRONMENT}"
    WFNEWS_AGOL_QUERY_URL ="${local.WFNEWS_AGOL_QUERY_URL}"
    WFNEWS_USERNAME = "${local.WFNEWS_USERNAME}"
    WFNEWS_MAX_CONNECTIONS ="${local.WFNEWS_MAX_CONNECTIONS}"
    WEBADE_OAUTH2_REST_CLIENT_ID = "${local.WEBADE_OAUTH2_REST_CLIENT_ID}"
    WEBADE_OAUTH2_UI_CLIENT_ID = "${local.WEBADE_OAUTH2_UI_CLIENT_ID}"
    WEBADE_OAUTH2_AUTHORIZE_URL = "${local.WEBADE_OAUTH2_AUTHORIZE_URL}"
    agolUrl = "${local.agolUrl}"
    drivebcBaseUrl = "${local.drivebcBaseUrl}"
    openmapsBaseUrl = "${local.openmapsBaseUrl}"
    siteMinderURLPrefix = "${local.siteMinderURLPrefix}"
    syncIntervalMinutes = "${local.syncIntervalMinutes}"
    agolAreaRestrictions = "${local.agolAreaRestrictions}"
    agolBansAndProhibitions = "${local.agolBansAndProhibitions}"
    agolDangerRatings = "${local.agolDangerRatings}"
    WEBADE_OAUTH2_WFNEWS_UI_CLIENT_SECRET = "${local.WEBADE_OAUTH2_WFNEWS_UI_CLIENT_SECRET}"
    MAX_RECEIVED_COUNT = "${local.MAX_RECEIVED_COUNT}"
    VISIBILITY_TIMEOUT_SECONDS = "${local.VISIBILITY_TIMEOUT_SECONDS}"
    ACCEPTED_IPS = "${local.ACCEPTED_IPS}"
    PUSH_NOTIFICATION_AWS_USER = "${local.PUSH_NOTIFICATION_AWS_USER}"
    EVENT_BRIDGE_ARN = "${local.EVENT_BRIDGE_ARN}"
    SECRET_NAME = "${local.SECRET_NAME}"
    BAN_PROHIBITION_MONITOR_KEY = "${local.BAN_PROHIBITION_MONITOR_KEY}"
    ACTIVE_FIRE_MONITOR_KEY = "${local.ACTIVE_FIRE_MONITOR_KEY}"
    AREA_RESTRICTIONS_MONITOR_KEY = "${local.AREA_RESTRICTIONS_MONITOR_KEY}"
    EVACUATION_MONITOR_KEY = "${local.EVACUATION_MONITOR_KEY}"
    LAMBDA_LAYER_KEY = "${local.LAMBDA_LAYER_KEY}"
    WFONE_NOTIFICATIONS_API_DATASOURCE_MAX_CONNECTIONS = "${local.WFONE_NOTIFICATIONS_API_DATASOURCE_MAX_CONNECTIONS}"
    WFONE_NOTIFICATIONS_API_DATASOURCE_USER = "${local.WFONE_NOTIFICATIONS_API_DATASOURCE_USER}"
    WFONE_NOTIFICATIONS_API_EMAIL_ADMIN_EMAIL = "${local.WFONE_NOTIFICATIONS_API_EMAIL_ADMIN_EMAIL}"
    WFONE_NOTIFICATIONS_API_EMAIL_FROM_EMAIL = "${local.WFONE_NOTIFICATIONS_API_EMAIL_FROM_EMAIL}"
    WFONE_NOTIFICATIONS_API_EMAIL_NOTIFICATIONS_ENABLED = "${local.WFONE_NOTIFICATIONS_API_EMAIL_NOTIFICATIONS_ENABLED}"
    WFONE_NOTIFICATIONS_API_EMAIL_SYNC_SEND_ERROR_FREQ = "${local.WFONE_NOTIFICATIONS_API_EMAIL_SYNC_SEND_ERROR_FREQ}"
    WFONE_NOTIFICATIONS_API_EMAIL_SYNC_SEND_ERROR_SUBJECT = "${local.WFONE_NOTIFICATIONS_API_EMAIL_SYNC_SEND_ERROR_SUBJECT}"
    WFONE_NOTIFICATIONS_API_EMAIL_SYNC_SEND_FREQ = "${local.WFONE_NOTIFICATIONS_API_EMAIL_SYNC_SEND_FREQ}"
    WFONE_NOTIFICATIONS_API_EMAIL_SYNC_SUBJECT = "${local.WFONE_NOTIFICATIONS_API_EMAIL_SYNC_SUBJECT}"
    WFONE_NOTIFICATIONS_API_PUSH_ITEM_EXPIRE_HOURS = "${local.WFONE_NOTIFICATIONS_API_PUSH_ITEM_EXPIRE_HOURS}"
    WFONE_NOTIFICATIONS_API_QUARTZ_CONSUMER_INTERVAL_SECONDS = "${local.WFONE_NOTIFICATIONS_API_QUARTZ_CONSUMER_INTERVAL_SECONDS}"
    WFONE_NOTIFICATIONS_API_SMTP_CREDENTIALS_PASSWORD = "${local.WFONE_NOTIFICATIONS_API_SMTP_CREDENTIALS_PASSWORD}"
    WFONE_NOTIFICATIONS_API_SMTP_CREDENTIALS_USER = "${local.WFONE_NOTIFICATIONS_API_SMTP_CREDENTIALS_USER}"
    WFONE_NOTIFICATIONS_API_SMTP_HOST_NAME = "${local.WFONE_NOTIFICATIONS_API_SMTP_HOST_NAME}"
    WFONE_NOTIFICATIONS_API_WEBADE_OAUTH2_CLIENT_ID = "${local.WFONE_NOTIFICATIONS_API_WEBADE_OAUTH2_CLIENT_ID}"
    WFONE_NOTIFICATIONS_API_WEBADE_OAUTH2_REST_CLIENT_SECRET = "${local.WFONE_NOTIFICATIONS_API_WEBADE_OAUTH2_REST_CLIENT_SECRET}"
    WFONE_NOTIFICATIONS_API_WEBADE_OAUTH2_WFIM_CLIENT_ID = "${local.WFONE_NOTIFICATIONS_API_WEBADE_OAUTH2_WFIM_CLIENT_ID}"
    WFONE_NOTIFICATIONS_API_WEBADE_OAUTH2_CHECK_TOKEN_URL = "${get_env("WFONE_NOTIFICATIONS_API_WEBADE_OAUTH2_CHECK_TOKEN_URL")}"
    WFONE_NOTIFICATIONS_API_WEBADE_OAUTH2_TOKEN_CLIENT_URL = "${get_env("WFONE_NOTIFICATIONS_API_WEBADE_OAUTH2_TOKEN_CLIENT_URL")}"
    WFONE_NOTIFICATIONS_API_WEBADE_OAUTH2_TOKEN_URL = "${get_env("WFONE_NOTIFICATIONS_API_WEBADE_OAUTH2_TOKEN_URL")}"
    wfone_notifications_api_image = "${local.WFONE_NOTIFICATIONS_API_IMAGE}"

    UNIQUE_DEPLOY_ID = "${get_env("UNIQUE_DEPLOY_ID")}"

    DATABASE_WEATHER_URL = "${get_env("DATABASE_WEATHER_URL")}"
    DATABASE_WEATHER_USER = "${get_env("DATABASE_WEATHER_USER")}"
    BCGW_URL = "${get_env("BCGW_URL")}"
    WFGS_URL = "${get_env("WFGS_URL")}"
    MAX_ALLOWED_RADIUS = "${get_env("MAX_ALLOWED_RADIUS")}"
    POINTID_ASYNC_JOB_INTERVAL = "${get_env("POINTID_ASYNC_JOB_INTERVAL")}"
    POINTID_ASYNC_JOB_REF_LAT = "${get_env("POINTID_ASYNC_JOB_REF_LAT")}"
    POINTID_ASYNC_JOB_REF_LONG = "${get_env("POINTID_ASYNC_JOB_REF_LONG")}"
    POINTID_ASYNC_JOB_REF_RADIUS = "${get_env("POINTID_ASYNC_JOB_REF_RADIUS")}"
    WEATHER_HOST = "${get_env("WEATHER_HOST")}"
    WEATHER_USER = "${get_env("WEATHER_USER")}"
    WFARCGIS_URL = "${get_env("WFARCGIS_URL")}"
    WFARCGIS_LAYER_AREA_RESTRICTIONS = "${get_env("WFARCGIS_LAYER_AREA_RESTRICTIONS")}"
    WFARCGIS_LAYER_BANS_PROHIBITION_AREAS = "${get_env("WFARCGIS_LAYER_BANS_PROHIBITION_AREAS")}"
    WFARCGIS_LAYER_DANGER_RATING = "${get_env("WFARCGIS_LAYER_DANGER_RATING")}"
    WFARCGIS_LAYER_ACTIVE_FIRES = "${get_env("WFARCGIS_LAYER_ACTIVE_FIRES")}"
    WFARCGIS_LAYER_EVACUATION_ORDERS_ALERTS = "${get_env("WFARCGIS_LAYER_EVACUATION_ORDERS_ALERTS")}"
    WFARCGIS_LAYER_FIRE_CENTRE_BOUNDARIES = "${get_env("WFARCGIS_LAYER_FIRE_CENTRE_BOUNDARIES")}"
    POINTID_WEBADE_OAUTH2_CLIENT_ID = "${get_env("POINTID_WEBADE_OAUTH2_CLIENT_ID")}"
    POINTID_WEBADE_OAUTH2_TOKEN_URL = "${get_env("POINTID_WEBADE_OAUTH2_TOKEN_URL")}"
    POINTID_WEBADE_OAUTH2_CLIENT_SCOPES = "${get_env("POINTID_WEBADE_OAUTH2_CLIENT_SCOPES")}"
    FIREWEATHER_BASEURL = "${get_env("FIREWEATHER_BASEURL")}"
    FIREWEATHER_STATIONS_KEY = "${get_env("FIREWEATHER_STATIONS_KEY")}"
    WFNEWS_QUEUESIZE = "${get_env("WFNEWS_QUEUESIZE")}"
    WFONE_MONITORS_NAME_MAP = {"active-fires" = {"EXPIRE_HOURS" = "${get_env("WFONE_PUSH_ITEM_EXPIRE_HOURS_FIRE")}"},"area-restrictions" = {"EXPIRE_HOURS" = "${get_env("WFONE_PUSH_ITEM_EXPIRE_HOURS_RESTRICTED_AREA")}"},"evac-orders" = {"EXPIRE_HOURS" = "${get_env("WFONE_PUSH_ITEM_EXPIRE_HOURS_EVAC")}"},"bans-prohibitions" = {"EXPIRE_HOURS" = "${get_env("WFONE_PUSH_ITEM_EXPIRE_HOURS_BAN")}"}}
    WFONE_NOTIFICATIONS_PUSH_SQS_MONITOR_ATTRIBUTE = "${get_env("WFONE_NOTIFICATIONS_PUSH_SQS_MONITOR_ATTRIBUTE")}"
    WFONE_NOTIFICATIONS_PUSH_SQS_MAX_MESSAGES = "${get_env("WFONE_NOTIFICATIONS_PUSH_SQS_MAX_MESSAGES")}"
    WFONE_NOTIFICATIONS_PUSH_SQS_WAIT_SECONDS = "${get_env("WFONE_NOTIFICATIONS_PUSH_SQS_WAIT_SECONDS")}"
    WFONE_NOTIFICATIONS_PUSH_CONSUMER_INTERVAL_SECONDS = "${get_env("WFONE_NOTIFICATIONS_PUSH_CONSUMER_INTERVAL_SECONDS")}"
    WFONE_FIREBASE_DB_URL = "${get_env("WFONE_FIREBASE_DB_URL")}"
    WFONE_NOTIFICATIONS_PUSH_PREFIX = "${get_env("WFONE_PUSH_NOTIFICATION_PREFIX")}"
    WFONE_NOTIFICATIONS_PUSH_NEAR_ME_INTERVAL_SECONDS = "${get_env("WFONE_NOTIFICATIONS_PUSH_NEAR_ME_INTERVAL_SECONDS")}"
    WFONE_NOTIFICATIONS_PUSH_AWS_ACCESS_KEY = "${get_env("WFONE_NOTIFICATIONS_PUSH_AWS_ACCESS_KEY")}"
    WFONE_NOTIFICATIONS_PUSH_AWS_SECRET_KEY = "${get_env("WFONE_NOTIFICATIONS_PUSH_AWS_SECRET_KEY")}"
    FIREBASE_CONFIG_JSON = "${get_env("FIREBASE_CONFIG_JSON")}"
    WFONE_DB_PASS = "${get_env("WFONE_DB_PASS")}"
    WFONE_NOTIFICATIONS_PUSH_API_IMAGE = "${get_env("WFONE_NOTIFICATIONS_PUSH_API_IMAGE")}"
    POINTID_WEBADE_OAUTH2_CLIENT_SECRET = "${get_env("POINTID_WEBADE_OAUTH2_CLIENT_SECRET")}"
  EOF
}
