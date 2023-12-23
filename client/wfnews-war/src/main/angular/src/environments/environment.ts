// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.

export const environment = {
  production: false,
  override_webade_preference_config: '',
  webade_preference_config: '',
  app_config_location: '/assets/data/appConfig.json',
  app_base: '/',
  document_management_proxy_auth_url: '/auth',
};
