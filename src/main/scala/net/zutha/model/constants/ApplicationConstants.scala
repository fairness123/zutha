package net.zutha.model.constants

object ApplicationConstants {
  val TM_DATA_PATH = "src/main/resources/tm_data/"

  val HTML_EXT = "html"

  val THIS_HOST_ID                = "0"
  val THIS_HOST_URI               = "http://" + THIS_HOST_ID + ".zutha.net/"
  val THIS_HOST_PSI_PREFIX        = THIS_HOST_URI + "psi/"
  val ZID_TICKER_SI               = THIS_HOST_PSI_PREFIX + "zid-ticker"
  val NEXT_ZID_OCCURRENCE_TYPE_SI  = THIS_HOST_PSI_PREFIX + "next-zid"
}
