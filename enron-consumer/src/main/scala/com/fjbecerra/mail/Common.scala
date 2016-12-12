package com.fjbecerra.mail


object Common {

  /**
    * This val represents fix characters in an email
    */

  val To = "to:"
  val Cc = "cc:"
  val BCc = "bcc"
  val Date = "date:"
  val From = "from:"
  val Subject = "subject:"
  val Xsdoc = "x-sdoc:"
  val XzlidLine = "x-zlid:"
  val LDAP = "</o="
  val OriginalMessage = "-----Original Message-----" toLowerCase
  val Attachment = "attachment:"
  val AsterikAndDashes = "([-*]+)$"
  val LegalTerms = "EDRM Enron Email Data Set has been produced in EML, PST and NSF format by ZL Technologies, Inc. This Data Set is licensed under a Creative Commons Attribution 3.0 United States License <http://creativecommons.org/licenses/by/3.0/us/> . To provide attribution, please cite to \"ZL Technologies, Inc. (http://www.zlti.com).\"" toLowerCase
  val ForwardedDashes = AsterikAndDashes + "Forwarded" toLowerCase
  val FowardedEndAM = "AM " + AsterikAndDashes toLowerCase
  val FowardedEndPM = "PM " + AsterikAndDashes toLowerCase
  val FowardedSpell ="FYI" toLowerCase
  val Sent = "sent:"
  val EmailPattern = """(?:"?([^"]*)"?\s)?(?:<?(.+@[^>]+)>?)""".r
  val FirstNameLastNamePattern = """(\w+)\s+(\w+)""".r
  val TwoFirstNameLastNamePattern = """(\w+)\s+(\w+)\s+(\w)?""".r
  val FirstNameLargeAndShortLastNamePattern = """(\w+)\s+(\w)\s+(\w+)?""".r

}
