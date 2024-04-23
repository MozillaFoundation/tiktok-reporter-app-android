# tiktok-reporter-app-android

To set an upload key or the base URL for development and testing:

* For build, set environment variables:
  * `FYP_REPORTER_UPLOAD_API_KEY`
  * `FYP_REPORTER_STORAGE_URL`
  * `FYP_REPORTER_BASE_URL`
* For `local.properties`:
  * `fypReporterUploadKey`
  * `fypReporterStorageUrl`
  * `baseUrl`

An example base URL for local development is: `http://192.168.1.2:8080/`
You'll also want to add `android:usesCleartextTraffic="true"` to `app/src/main/AndroidManifest.xml`.