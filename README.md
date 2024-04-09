# tiktok-reporter-app-android

To set an upload key or the base URL for development and testing:

* For build, set environment variables:
  * `TTREPORTER_UPLOAD_API_KEY`
  * `TTREPORTER_BASE_URL`
* For `local.properties`:
  * `ttreporterUploadKey`
  * `baseUrl`

An example base URL for local development is: `http://192.168.1.2:8080/`
You'll also want to add `android:usesCleartextTraffic="true"` to `app/src/main/AndroidManifest.xml`.